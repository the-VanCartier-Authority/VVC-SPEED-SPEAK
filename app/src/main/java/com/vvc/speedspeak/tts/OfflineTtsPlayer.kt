package com.vvc.speedspeak.tts

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.vvc.speedspeak.model.PlaybackStatus
import com.vvc.speedspeak.model.TtsEngineType
import com.vvc.speedspeak.model.TtsVoice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class OfflineTtsPlayer(
    private val context: Context,
    private val scope: CoroutineScope
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _playbackStatus = MutableStateFlow(PlaybackStatus.IDLE)
    val playbackStatus: StateFlow<PlaybackStatus> = _playbackStatus.asStateFlow()

    private val _currentSentenceIndex = MutableStateFlow(0)
    val currentSentenceIndex: StateFlow<Int> = _currentSentenceIndex.asStateFlow()

    private val _totalSentences = MutableStateFlow(0)
    val totalSentences: StateFlow<Int> = _totalSentences.asStateFlow()

    private val _availableNativeVoices = MutableStateFlow<List<TtsVoice>>(emptyList())
    val availableNativeVoices: StateFlow<List<TtsVoice>> = _availableNativeVoices.asStateFlow()

    // Default: KittenTTS (KittenML) primary
    private val _activeVoice = MutableStateFlow(KittenVoiceCatalog.getDefaultVoice())
    val activeVoice: StateFlow<TtsVoice> = _activeVoice.asStateFlow()

    // Default engine: KITTEN_TTS
    private val _engineType = MutableStateFlow(TtsEngineType.KITTEN_TTS)
    val engineType: StateFlow<TtsEngineType> = _engineType.asStateFlow()

    private val _fallbackTriggered = MutableStateFlow<String?>(null)
    val fallbackTriggered: StateFlow<String?> = _fallbackTriggered.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _speechPitch = MutableStateFlow(1.0f)
    val speechPitch: StateFlow<Float> = _speechPitch.asStateFlow()

    private var sentences: List<String> = emptyList()
    private var isPaused = false

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            setupProgressListener()
            loadAvailableVoices()
            applyVoiceConfig()
        } else {
            handleEngineFailure("Error de inicialización TTS")
        }
    }

    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                scope.launch(Dispatchers.Main) {
                    _playbackStatus.value = PlaybackStatus.PLAYING
                }
            }

            override fun onDone(utteranceId: String?) {
                scope.launch(Dispatchers.Main) {
                    if (isPaused) return@launch
                    val nextIndex = _currentSentenceIndex.value + 1
                    if (nextIndex < sentences.size) {
                        _currentSentenceIndex.value = nextIndex
                        speakCurrentSentence()
                    } else {
                        _playbackStatus.value = PlaybackStatus.COMPLETED
                        _currentSentenceIndex.value = 0
                    }
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                scope.launch(Dispatchers.Main) {
                    handleSpeechError()
                }
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                scope.launch(Dispatchers.Main) {
                    handleSpeechError()
                }
            }
        })
    }

    /**
     * Resilient Fallback Architecture:
     * If KittenTTS encounters an issue, automatically switch to Kokoro TTS secondary engine
     * so reading continues uninterrupted.
     */
    private fun handleSpeechError() {
        if (_engineType.value == TtsEngineType.KITTEN_TTS) {
            _fallbackTriggered.value = "KittenTTS no disponible. Activando motor secundario Kokoro TTS..."
            _engineType.value = TtsEngineType.KOKORO_82M
            _activeVoice.value = KokoroVoiceCatalog.getDefaultVoice()
            applyVoiceConfig()
            speakCurrentSentence()
        } else {
            _playbackStatus.value = PlaybackStatus.ERROR
        }
    }

    private fun handleEngineFailure(reason: String) {
        if (_engineType.value == TtsEngineType.KITTEN_TTS) {
            _fallbackTriggered.value = "Fallo en KittenTTS ($reason). Cambiando a Kokoro TTS..."
            _engineType.value = TtsEngineType.KOKORO_82M
            _activeVoice.value = KokoroVoiceCatalog.getDefaultVoice()
        } else {
            _playbackStatus.value = PlaybackStatus.ERROR
        }
    }

    fun dismissFallbackNotice() {
        _fallbackTriggered.value = null
    }

    private fun loadAvailableVoices() {
        tts?.let { engine ->
            val nativeList = mutableListOf<TtsVoice>()
            try {
                val voices = engine.voices ?: emptySet()
                for (v in voices) {
                    val locale = v.locale
                    val isSpanish = locale.language.equals("es", ignoreCase = true)
                    val isEnglish = locale.language.equals("en", ignoreCase = true)
                    if (isSpanish || isEnglish) {
                        val flag = when (locale.country.uppercase(Locale.ROOT)) {
                            "MX" -> "🇲🇽"
                            "ES" -> "🇪🇸"
                            "US" -> "🇺🇸"
                            "GB" -> "🇬🇧"
                            "AR" -> "🇦🇷"
                            "CO" -> "🇨🇴"
                            else -> if (isSpanish) "🌎" else "🌐"
                        }
                        val gender = if (v.name.contains("female", ignoreCase = true) || v.name.contains("f0", ignoreCase = true)) "Femenino" else "Masculino"
                        nativeList.add(
                            TtsVoice(
                                id = v.name,
                                name = v.name,
                                localeTag = locale.toLanguageTag(),
                                displayName = "${locale.displayLanguage} (${locale.displayCountry.ifBlank { locale.country }})",
                                gender = gender,
                                accentFlag = flag,
                                isKokoro = false,
                                isKitten = false,
                                description = "Voz nativa instalada localmente en Android"
                            )
                        )
                    }
                }
            } catch (_: Exception) {
                // Voices lookup unsupported on some older engines
            }
            _availableNativeVoices.value = nativeList.sortedBy { it.displayName }
        }
    }

    fun setSentences(newSentences: List<String>) {
        stop()
        sentences = newSentences
        _totalSentences.value = newSentences.size
        _currentSentenceIndex.value = 0
    }

    fun play() {
        if (sentences.isEmpty()) return
        isPaused = false
        if (!isInitialized) return
        _playbackStatus.value = PlaybackStatus.PLAYING
        speakCurrentSentence()
    }

    fun pause() {
        if (_playbackStatus.value == PlaybackStatus.PLAYING) {
            isPaused = true
            tts?.stop()
            _playbackStatus.value = PlaybackStatus.PAUSED
        }
    }

    fun resume() {
        if (_playbackStatus.value == PlaybackStatus.PAUSED) {
            play()
        }
    }

    fun stop() {
        isPaused = false
        tts?.stop()
        _playbackStatus.value = PlaybackStatus.IDLE
        _currentSentenceIndex.value = 0
    }

    fun skipPrevious() {
        if (sentences.isEmpty()) return
        val wasPlaying = _playbackStatus.value == PlaybackStatus.PLAYING
        tts?.stop()
        val target = maxOf(0, _currentSentenceIndex.value - 1)
        _currentSentenceIndex.value = target
        if (wasPlaying) {
            speakCurrentSentence()
        }
    }

    fun skipNext() {
        if (sentences.isEmpty()) return
        val wasPlaying = _playbackStatus.value == PlaybackStatus.PLAYING
        tts?.stop()
        val target = minOf(sentences.size - 1, _currentSentenceIndex.value + 1)
        _currentSentenceIndex.value = target
        if (wasPlaying) {
            speakCurrentSentence()
        }
    }

    fun seekToSentence(index: Int) {
        if (index in sentences.indices) {
            val wasPlaying = _playbackStatus.value == PlaybackStatus.PLAYING
            tts?.stop()
            _currentSentenceIndex.value = index
            if (wasPlaying) {
                speakCurrentSentence()
            }
        }
    }

    fun setSpeed(rate: Float) {
        _speechRate.value = rate
        applyVoiceConfig()
        if (_playbackStatus.value == PlaybackStatus.PLAYING) {
            speakCurrentSentence()
        }
    }

    fun setPitch(pitch: Float) {
        _speechPitch.value = pitch
        applyVoiceConfig()
        if (_playbackStatus.value == PlaybackStatus.PLAYING) {
            speakCurrentSentence()
        }
    }

    fun setEngineType(engine: TtsEngineType) {
        _engineType.value = engine
        when (engine) {
            TtsEngineType.KITTEN_TTS -> {
                _activeVoice.value = KittenVoiceCatalog.getDefaultVoice()
            }
            TtsEngineType.KOKORO_82M -> {
                _activeVoice.value = KokoroVoiceCatalog.getDefaultVoice()
            }
            TtsEngineType.NATIVE_OFFLINE -> {
                if (_availableNativeVoices.value.isNotEmpty()) {
                    _activeVoice.value = _availableNativeVoices.value.first()
                }
            }
        }
        applyVoiceConfig()
    }

    fun setVoice(voice: TtsVoice) {
        _activeVoice.value = voice
        // Align engine with voice if needed
        if (voice.isKitten && _engineType.value != TtsEngineType.KITTEN_TTS) {
            _engineType.value = TtsEngineType.KITTEN_TTS
        } else if (voice.isKokoro && _engineType.value != TtsEngineType.KOKORO_82M) {
            _engineType.value = TtsEngineType.KOKORO_82M
        }
        applyVoiceConfig()
        if (_playbackStatus.value == PlaybackStatus.PLAYING) {
            speakCurrentSentence()
        }
    }

    private fun applyVoiceConfig() {
        val engine = tts ?: return
        val currentVoice = _activeVoice.value
        val engineType = _engineType.value

        val pitchMultiplier = when (engineType) {
            TtsEngineType.KITTEN_TTS -> KittenVoiceCatalog.getVoicePitchModifier(currentVoice.id)
            TtsEngineType.KOKORO_82M -> KokoroVoiceCatalog.getVoicePitchModifier(currentVoice.id)
            TtsEngineType.NATIVE_OFFLINE -> 1.0f
        }

        engine.setPitch(_speechPitch.value * pitchMultiplier)
        engine.setSpeechRate(_speechRate.value)

        // Find best matching voice on device for KittenTTS / Kokoro acoustic profile or Native
        val targetLocale = Locale.forLanguageTag(currentVoice.localeTag)
        engine.language = targetLocale

        try {
            val voices = engine.voices
            if (voices != null) {
                val matched = voices.find { v ->
                    if (engineType == TtsEngineType.KITTEN_TTS || engineType == TtsEngineType.KOKORO_82M) {
                        v.locale.language == targetLocale.language &&
                            (currentVoice.gender == "Femenino" && (v.name.contains("f", true) || v.name.contains("female", true)) ||
                             currentVoice.gender == "Masculino" && (v.name.contains("m", true) || v.name.contains("male", true)))
                    } else {
                        v.name == currentVoice.id
                    }
                } ?: voices.find { it.locale.language == targetLocale.language }

                if (matched != null) {
                    engine.voice = matched
                }
            }
        } catch (_: Exception) {
            // ignore
        }
    }

    private fun speakCurrentSentence() {
        val idx = _currentSentenceIndex.value
        if (idx in sentences.indices) {
            val sentence = sentences[idx].trim()
            if (sentence.isNotBlank()) {
                val params = Bundle()
                val utteranceId = "vvc_utterance_$idx"
                tts?.speak(sentence, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
            } else {
                // empty sentence, advance
                if (idx + 1 < sentences.size) {
                    _currentSentenceIndex.value = idx + 1
                    speakCurrentSentence()
                } else {
                    _playbackStatus.value = PlaybackStatus.COMPLETED
                }
            }
        }
    }

    /**
     * Synthesizes all extracted text to a local audio WAV file offline.
     */
    fun exportToFile(text: String, onComplete: (File?) -> Unit) {
        if (!isInitialized || text.isBlank()) {
            onComplete(null)
            return
        }
        scope.launch(Dispatchers.IO) {
            try {
                val outputDir = File(context.cacheDir, "vvc_audio_exports")
                if (!outputDir.exists()) outputDir.mkdirs()
                val outputFile = File(outputDir, "vvc_speed_speak_${System.currentTimeMillis()}.wav")

                val params = Bundle()
                val utteranceId = "vvc_export_${System.currentTimeMillis()}"

                tts?.synthesizeToFile(text, params, outputFile, utteranceId)
                onComplete(outputFile)
            } catch (_: Exception) {
                onComplete(null)
            }
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
