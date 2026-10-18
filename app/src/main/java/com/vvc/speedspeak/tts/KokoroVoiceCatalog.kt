package com.vvc.speedspeak.tts

import com.vvc.speedspeak.model.TtsVoice

object KokoroVoiceCatalog {

    val kokoroVoices = listOf(
        TtsVoice(
            id = "es_m_diego",
            name = "Diego",
            localeTag = "es-MX",
            displayName = "Diego • Español Latino",
            gender = "Masculino",
            accentFlag = "🇲🇽",
            isKokoro = true,
            description = "Tono cálido, fluido y conversacional. Ideal para audiolibros y artículos."
        ),
        TtsVoice(
            id = "es_f_sofia",
            name = "Sofía",
            localeTag = "es-MX",
            displayName = "Sofía • Español México",
            gender = "Femenino",
            accentFlag = "🇲🇽",
            isKokoro = true,
            description = "Voz expresiva, clara y suave con cadencia natural latina."
        ),
        TtsVoice(
            id = "es_m_carlos",
            name = "Carlos",
            localeTag = "es-ES",
            displayName = "Carlos • Español Narrador",
            gender = "Masculino",
            accentFlag = "🇪🇸",
            isKokoro = true,
            description = "Resonante y autoritario, óptimo para documentos técnicos y noticias."
        ),
        TtsVoice(
            id = "es_f_lucia",
            name = "Lucía",
            localeTag = "es-ES",
            displayName = "Lucía • Español Estudio",
            gender = "Femenino",
            accentFlag = "🇪🇸",
            isKokoro = true,
            description = "Articulación cristalina y dicción precisa para lectura de alta velocidad."
        ),
        TtsVoice(
            id = "es_m_mateo",
            name = "Mateo",
            localeTag = "es-419",
            displayName = "Mateo • Español Dinámico",
            gender = "Masculino",
            accentFlag = "🌎",
            isKokoro = true,
            description = "Estilo enérgico y moderno, adaptado para ritmo veloz."
        ),
        TtsVoice(
            id = "en_f_sarah",
            name = "Sarah",
            localeTag = "en-US",
            displayName = "Sarah • English US Natural",
            gender = "Femenino",
            accentFlag = "🇺🇸",
            isKokoro = true,
            description = "Ultra-realistic natural American voice from the Kokoro-82M core weights."
        ),
        TtsVoice(
            id = "en_m_michael",
            name = "Michael",
            localeTag = "en-US",
            displayName = "Michael • English US Deep",
            gender = "Masculino",
            accentFlag = "🇺🇸",
            isKokoro = true,
            description = "Deep baritone tone, rich resonance for long-form reading."
        ),
        TtsVoice(
            id = "en_f_bella",
            name = "Bella",
            localeTag = "en-GB",
            displayName = "Bella • English UK Studio",
            gender = "Femenino",
            accentFlag = "🇬🇧",
            isKokoro = true,
            description = "British RP accent with melodic cadence and natural breath patterns."
        )
    )

    fun getDefaultVoice(): TtsVoice = kokoroVoices.first()

    fun getVoiceById(id: String): TtsVoice? = kokoroVoices.find { it.id == id }

    fun getVoicePitchModifier(voiceId: String): Float = when (voiceId) {
        "es_m_diego" -> 0.95f
        "es_f_sofia" -> 1.08f
        "es_m_carlos" -> 0.90f
        "es_f_lucia" -> 1.12f
        "es_m_mateo" -> 1.02f
        "en_f_sarah" -> 1.05f
        "en_m_michael" -> 0.88f
        "en_f_bella" -> 1.10f
        else -> 1.0f
    }
}
