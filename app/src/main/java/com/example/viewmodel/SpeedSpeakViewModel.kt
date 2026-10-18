package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.ExtractedDocument
import com.example.model.PlaybackStatus
import com.example.model.TtsEngineType
import com.example.model.TtsVoice
import com.example.tts.KittenVoiceCatalog
import com.example.tts.KokoroVoiceCatalog
import com.example.tts.OfflineTtsPlayer
import com.example.util.DocumentExtractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpeedSpeakViewModel(application: Application) : AndroidViewModel(application) {

    private val player = OfflineTtsPlayer(application, viewModelScope)

    val playbackStatus: StateFlow<PlaybackStatus> = player.playbackStatus
    val currentSentenceIndex: StateFlow<Int> = player.currentSentenceIndex
    val totalSentences: StateFlow<Int> = player.totalSentences
    val speechRate: StateFlow<Float> = player.speechRate
    val speechPitch: StateFlow<Float> = player.speechPitch
    val engineType: StateFlow<TtsEngineType> = player.engineType
    val activeVoice: StateFlow<TtsVoice> = player.activeVoice
    val availableNativeVoices: StateFlow<List<TtsVoice>> = player.availableNativeVoices
    val availableKittenVoices: List<TtsVoice> = KittenVoiceCatalog.kittenVoices
    val availableKokoroVoices: List<TtsVoice> = KokoroVoiceCatalog.kokoroVoices
    val fallbackTriggered: StateFlow<String?> = player.fallbackTriggered

    private val _document = MutableStateFlow(ExtractedDocument.empty())
    val document: StateFlow<ExtractedDocument> = _document.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _exportMessage = MutableStateFlow<String?>(null)
    val exportMessage: StateFlow<String?> = _exportMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        // Load default sample document
        val defaultSample = DocumentExtractor.sampleDocuments.first()
        loadSample(defaultSample.first, defaultSample.second)
    }

    fun loadUri(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val doc = DocumentExtractor.extractFromUri(getApplication(), uri)
                _document.value = doc
                player.setSentences(doc.sentences)
            } catch (e: Exception) {
                _errorMessage.value = "Error al procesar archivo: ${e.localizedMessage ?: "Formato no compatible"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadSample(title: String, content: String) {
        val doc = DocumentExtractor.buildExtractedDocument(title, "TEXTO DEMO", content)
        _document.value = doc
        player.setSentences(doc.sentences)
    }

    fun updateManualText(text: String) {
        val doc = DocumentExtractor.buildExtractedDocument("Texto Editado", "MANUAL", text)
        _document.value = doc
        player.setSentences(doc.sentences)
    }

    fun clearDocument() {
        player.stop()
        _document.value = ExtractedDocument.empty()
        player.setSentences(emptyList())
    }

    fun play() {
        if (_document.value.sentences.isEmpty()) return
        if (playbackStatus.value == PlaybackStatus.PAUSED) {
            player.resume()
        } else {
            player.play()
        }
    }

    fun pause() {
        player.pause()
    }

    fun stop() {
        player.stop()
    }

    fun skipPrevious() {
        player.skipPrevious()
    }

    fun skipNext() {
        player.skipNext()
    }

    fun seekToSentence(index: Int) {
        player.seekToSentence(index)
    }

    fun setSpeed(rate: Float) {
        player.setSpeed(rate)
    }

    fun setPitch(pitch: Float) {
        player.setPitch(pitch)
    }

    fun setEngineType(type: TtsEngineType) {
        player.setEngineType(type)
    }

    fun setVoice(voice: TtsVoice) {
        player.setVoice(voice)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun dismissExportMessage() {
        _exportMessage.value = null
    }

    fun dismissErrorMessage() {
        _errorMessage.value = null
    }

    fun dismissFallbackNotice() {
        player.dismissFallbackNotice()
    }

    fun exportCurrentSpeechToAudio() {
        val text = _document.value.fullText
        if (text.isBlank()) {
            _errorMessage.value = "No hay texto para exportar."
            return
        }
        _exportMessage.value = "Generando archivo de audio local (WAV)..."
        player.exportToFile(text) { file ->
            if (file != null && file.exists()) {
                _exportMessage.value = "Audio exportado con éxito en: ${file.name} (${file.length() / 1024} KB)"
            } else {
                _exportMessage.value = "No se pudo generar el archivo de audio."
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.shutdown()
    }
}
