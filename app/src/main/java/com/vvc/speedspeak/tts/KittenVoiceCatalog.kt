package com.vvc.speedspeak.tts

import com.vvc.speedspeak.model.TtsVoice

object KittenVoiceCatalog {

    val kittenVoices = listOf(
        TtsVoice(
            id = "kitten_es_valentina",
            name = "Valentina",
            localeTag = "es-MX",
            displayName = "Valentina • KittenML Latino",
            gender = "Femenino",
            accentFlag = "🇲🇽",
            isKitten = true,
            description = "Voz insignia de KittenTTS: prosodia ultra-humana, entonación cálida y dicción sin tonos mecánicos."
        ),
        TtsVoice(
            id = "kitten_es_mateo",
            name = "Mateo",
            localeTag = "es-MX",
            displayName = "Mateo • KittenML Dinámico",
            gender = "Masculino",
            accentFlag = "🇲🇽",
            isKitten = true,
            description = "Excelente articulación para lectura acelerada, tono enérgico y fluido."
        ),
        TtsVoice(
            id = "kitten_es_lucas",
            name = "Lucas",
            localeTag = "es-419",
            displayName = "Lucas • KittenML Narrador",
            gender = "Masculino",
            accentFlag = "🌎",
            isKitten = true,
            description = "Timbre barítono relajado, ideal para audiolibros, documentos largos y resúmenes."
        ),
        TtsVoice(
            id = "kitten_es_camila",
            name = "Camila",
            localeTag = "es-ES",
            displayName = "Camila • KittenML Estudio",
            gender = "Femenino",
            accentFlag = "🇪🇸",
            isKitten = true,
            description = "Claridad cristalina y pausas naturales calculadas para máxima comprensión."
        ),
        TtsVoice(
            id = "kitten_en_oliver",
            name = "Oliver",
            localeTag = "en-US",
            displayName = "Oliver • KittenML Conversational",
            gender = "Masculino",
            accentFlag = "🇺🇸",
            isKitten = true,
            description = "Compact low-latency English neural voice with natural cadence and inflection."
        ),
        TtsVoice(
            id = "kitten_en_emma",
            name = "Emma",
            localeTag = "en-US",
            displayName = "Emma • KittenML Studio",
            gender = "Femenino",
            accentFlag = "🇺🇸",
            isKitten = true,
            description = "Bright studio articulation with expressive human cadence."
        )
    )

    fun getDefaultVoice(): TtsVoice = kittenVoices.first()

    fun getVoiceById(id: String): TtsVoice? = kittenVoices.find { it.id == id }

    fun getVoicePitchModifier(voiceId: String): Float = when (voiceId) {
        "kitten_es_valentina" -> 1.04f
        "kitten_es_mateo" -> 0.96f
        "kitten_es_lucas" -> 0.88f
        "kitten_es_camila" -> 1.08f
        "kitten_en_oliver" -> 0.94f
        "kitten_en_emma" -> 1.06f
        else -> 1.0f
    }
}
