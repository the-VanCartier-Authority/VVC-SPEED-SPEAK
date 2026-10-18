package com.vvc.speedspeak.model

enum class TtsEngineType(val label: String, val badge: String, val description: String) {
    KITTEN_TTS(
        label = "KittenTTS (KittenML)",
        badge = "KITTENTTS PRINCIPAL",
        description = "Motor de síntesis neuronal principal de KittenML 100% offline. Ultra-rápido, prosodia humana fluida y libre de tonos robóticos."
    ),
    KOKORO_82M(
        label = "Kokoro TTS (82M)",
        badge = "KOKORO RESPALDO",
        description = "Motor neuronal secundario de respaldo (82M parámetros) que entra en acción automáticamente como alternativa o si KittenTTS presenta fallos."
    ),
    NATIVE_OFFLINE(
        label = "Android TTS Nativo",
        badge = "MOTOR DEL SISTEMA",
        description = "Motor de síntesis de voz del dispositivo 100% offline con soporte para todas las voces instaladas en el sistema."
    )
}

data class TtsVoice(
    val id: String,
    val name: String,
    val localeTag: String,
    val displayName: String,
    val gender: String,
    val accentFlag: String,
    val isKokoro: Boolean = false,
    val isKitten: Boolean = false,
    val description: String = ""
)

data class ExtractedDocument(
    val fileName: String,
    val fileType: String,
    val fullText: String,
    val wordCount: Int,
    val charCount: Int,
    val paragraphs: List<String>,
    val sentences: List<String>,
    val estimatedDurationMinutes: Double
) {
    companion object {
        fun empty(): ExtractedDocument = ExtractedDocument(
            fileName = "",
            fileType = "",
            fullText = "",
            wordCount = 0,
            charCount = 0,
            paragraphs = emptyList(),
            sentences = emptyList(),
            estimatedDurationMinutes = 0.0
        )
    }
}

enum class PlaybackStatus {
    IDLE,
    PLAYING,
    PAUSED,
    COMPLETED,
    ERROR
}
