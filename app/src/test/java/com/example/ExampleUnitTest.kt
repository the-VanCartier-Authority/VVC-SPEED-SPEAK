package com.example

import com.example.model.TtsEngineType
import com.example.tts.KittenVoiceCatalog
import com.example.tts.KokoroVoiceCatalog
import com.example.util.DocumentExtractor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testKittenVoiceCatalog() {
        val defaultVoice = KittenVoiceCatalog.getDefaultVoice()
        assertNotNull(defaultVoice)
        assertEquals("kitten_es_valentina", defaultVoice.id)
        assertTrue(defaultVoice.isKitten)
        assertTrue(KittenVoiceCatalog.kittenVoices.size >= 4)

        val mateoVoice = KittenVoiceCatalog.getVoiceById("kitten_es_mateo")
        assertNotNull(mateoVoice)
        assertEquals("Mateo", mateoVoice?.name)
    }

    @Test
    fun testKokoroFallbackVoiceCatalog() {
        val defaultVoice = KokoroVoiceCatalog.getDefaultVoice()
        assertNotNull(defaultVoice)
        assertEquals("es_m_diego", defaultVoice.id)
        assertTrue(defaultVoice.isKokoro)
        assertTrue(KokoroVoiceCatalog.kokoroVoices.size >= 5)

        val sofiaVoice = KokoroVoiceCatalog.getVoiceById("es_f_sofia")
        assertNotNull(sofiaVoice)
        assertEquals("Sofía", sofiaVoice?.name)
    }

    @Test
    fun testEngineTypesOrder() {
        // KittenTTS is primary (first entry)
        assertEquals(TtsEngineType.KITTEN_TTS, TtsEngineType.values()[0])
        // Kokoro is secondary (second entry)
        assertEquals(TtsEngineType.KOKORO_82M, TtsEngineType.values()[1])
    }

    @Test
    fun testDocumentExtractorSentenceParsing() {
        val sampleText = "Primera frase de prueba. Segunda frase con entonación natural. ¿Tercera frase interrogativa?"
        val doc = DocumentExtractor.buildExtractedDocument("test.txt", "TXT", sampleText)

        assertEquals("test.txt", doc.fileName)
        assertEquals("TXT", doc.fileType)
        assertEquals(3, doc.sentences.size)
        assertTrue(doc.wordCount > 5)
        assertTrue(doc.estimatedDurationMinutes > 0)
    }
}
