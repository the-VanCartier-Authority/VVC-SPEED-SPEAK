package com.vvc.speedspeak.util

import android.content.Context
import android.net.Uri
import com.vvc.speedspeak.model.ExtractedDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Locale
import java.util.zip.InflaterInputStream

object DocumentExtractor {

    suspend fun extractFromUri(context: Context, uri: Uri): ExtractedDocument = withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri) ?: ""
        var fileName = "documento"

        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex) ?: "documento"
            }
        }

        val isPdf = fileName.lowercase(Locale.ROOT).endsWith(".pdf") || mimeType.contains("pdf")
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("No se pudo abrir el archivo seleccionado.")

        val rawText = if (isPdf) {
            extractPdfText(inputStream)
        } else {
            extractTxtText(inputStream)
        }

        buildExtractedDocument(fileName, if (isPdf) "PDF" else "TXT", rawText)
    }

    private fun extractTxtText(inputStream: InputStream): String {
        return inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    }

    /**
     * Extracts text from PDF bytes 100% offline without external network dependencies.
     * Decodes FlateDecode streams and parses standard PDF text operators (BT ... ET, Tj, TJ).
     */
    fun extractPdfText(inputStream: InputStream): String {
        val bytes = inputStream.use { it.readBytes() }
        val sb = StringBuilder()

        // 1. Look for stream objects and decode them
        val streamMarkers = findStreamMarkers(bytes)
        for ((start, end, isFlate) in streamMarkers) {
            val streamBytes = bytes.copyOfRange(start, end)
            val decodedBytes = if (isFlate) {
                inflateSafely(streamBytes)
            } else {
                streamBytes
            }
            if (decodedBytes.isNotEmpty()) {
                val streamContent = String(decodedBytes, Charsets.ISO_8859_1)
                val textFromStream = parsePdfTextStream(streamContent)
                if (textFromStream.isNotBlank()) {
                    sb.append(textFromStream).append("\n\n")
                }
            }
        }

        var extracted = sb.toString().trim()

        // If stream parsing was sparse, also scan uncompressed text blocks in the entire PDF
        if (extracted.length < 50) {
            val fullPdfString = String(bytes, Charsets.ISO_8859_1)
            val fallbackText = parsePdfTextStream(fullPdfString)
            if (fallbackText.length > extracted.length) {
                extracted = fallbackText
            }
        }

        // If still empty or minimal, provide a helpful notice with readable strings
        if (extracted.isBlank()) {
            extracted = extractReadableStrings(bytes)
        }

        return if (extracted.isNotBlank()) extracted else "No se pudo extraer texto seleccionable del archivo PDF. Puede ser un documento escaneado como imagen."
    }

    private data class StreamMarker(val start: Int, val end: Int, val isFlate: Boolean)

    private fun findStreamMarkers(bytes: ByteArray): List<StreamMarker> {
        val list = mutableListOf<StreamMarker>()
        val streamBytes = "stream".toByteArray(Charsets.ISO_8859_1)
        val endStreamBytes = "endstream".toByteArray(Charsets.ISO_8859_1)
        val flateBytes = "/FlateDecode".toByteArray(Charsets.ISO_8859_1)

        var i = 0
        while (i < bytes.size - 6) {
            if (matchesAt(bytes, i, streamBytes)) {
                // Find start of stream data (after stream\r\n or stream\n)
                var dataStart = i + streamBytes.size
                if (dataStart < bytes.size && bytes[dataStart] == '\r'.code.toByte()) dataStart++
                if (dataStart < bytes.size && bytes[dataStart] == '\n'.code.toByte()) dataStart++

                // Check backwards for /FlateDecode in previous 200 bytes
                val checkBackStart = maxOf(0, i - 250)
                val isFlate = containsSubarray(bytes, checkBackStart, i, flateBytes)

                // Find endstream
                var dataEnd = -1
                var j = dataStart
                while (j < bytes.size - 9) {
                    if (matchesAt(bytes, j, endStreamBytes)) {
                        dataEnd = j
                        // Strip trailing \r\n before endstream
                        if (dataEnd > dataStart && bytes[dataEnd - 1] == '\n'.code.toByte()) dataEnd--
                        if (dataEnd > dataStart && bytes[dataEnd - 1] == '\r'.code.toByte()) dataEnd--
                        break
                    }
                    j++
                }

                if (dataEnd > dataStart) {
                    list.add(StreamMarker(dataStart, dataEnd, isFlate))
                    i = j + endStreamBytes.size
                } else {
                    i += 6
                }
            } else {
                i++
            }
        }
        return list
    }

    private fun matchesAt(source: ByteArray, offset: Int, pattern: ByteArray): Boolean {
        if (offset + pattern.size > source.size) return false
        for (k in pattern.indices) {
            if (source[offset + k] != pattern[k]) return false
        }
        return true
    }

    private fun containsSubarray(source: ByteArray, start: Int, end: Int, sub: ByteArray): Boolean {
        val limit = minOf(end, source.size - sub.size)
        for (idx in start..limit) {
            if (matchesAt(source, idx, sub)) return true
        }
        return false
    }

    private fun inflateSafely(compressed: ByteArray): ByteArray {
        return try {
            InflaterInputStream(ByteArrayInputStream(compressed)).use { inflater ->
                val buffer = ByteArray(4096)
                val out = ByteArrayOutputStream()
                var read: Int
                while (inflater.read(buffer).also { read = it } != -1) {
                    out.write(buffer, 0, read)
                }
                out.toByteArray()
            }
        } catch (_: Exception) {
            // If decompression fails (e.g., partial block or not zlib), return original
            compressed
        }
    }

    private fun parsePdfTextStream(streamContent: String): String {
        val textBuilder = StringBuilder()
        val btIndices = mutableListOf<Int>()
        var searchPos = 0

        while (true) {
            val btPos = streamContent.indexOf("BT", searchPos)
            if (btPos == -1) break
            val etPos = streamContent.indexOf("ET", btPos)
            if (etPos != -1) {
                val block = streamContent.substring(btPos + 2, etPos)
                extractTextFromBlock(block, textBuilder)
                searchPos = etPos + 2
            } else {
                break
            }
        }

        return textBuilder.toString()
    }

    private fun extractTextFromBlock(block: String, out: StringBuilder) {
        var idx = 0
        while (idx < block.length) {
            val char = block[idx]
            if (char == '(') {
                // Literal string ( ... )
                val endParen = findMatchingParen(block, idx)
                if (endParen != -1) {
                    val rawStr = block.substring(idx + 1, endParen)
                    val unescaped = unescapePdfString(rawStr)
                    out.append(unescaped).append(" ")
                    idx = endParen + 1
                    continue
                }
            } else if (char == '[') {
                // Array string [ (Hello) -20 (World) ] TJ
                val endBracket = block.indexOf(']', idx)
                if (endBracket != -1) {
                    val arrayContent = block.substring(idx + 1, endBracket)
                    extractStringsFromArray(arrayContent, out)
                    idx = endBracket + 1
                    continue
                }
            } else if (char == '\n' || char == '\r') {
                // newline in PDF stream
            }
            idx++
        }
    }

    private fun findMatchingParen(str: String, startIdx: Int): Int {
        var depth = 1
        var i = startIdx + 1
        while (i < str.length) {
            if (str[i] == '\\') {
                i += 2 // skip escaped character
                continue
            }
            if (str[i] == '(') depth++
            if (str[i] == ')') {
                depth--
                if (depth == 0) return i
            }
            i++
        }
        return -1
    }

    private fun extractStringsFromArray(arrayContent: String, out: StringBuilder) {
        var i = 0
        while (i < arrayContent.length) {
            if (arrayContent[i] == '(') {
                val endParen = findMatchingParen(arrayContent, i)
                if (endParen != -1) {
                    val unescaped = unescapePdfString(arrayContent.substring(i + 1, endParen))
                    out.append(unescaped)
                    i = endParen + 1
                    continue
                }
            }
            i++
        }
        out.append(" ")
    }

    private fun unescapePdfString(str: String): String {
        val clean = StringBuilder()
        var i = 0
        while (i < str.length) {
            if (str[i] == '\\' && i + 1 < str.length) {
                when (val next = str[i + 1]) {
                    'n' -> clean.append('\n')
                    'r' -> clean.append('\r')
                    't' -> clean.append('\t')
                    'b' -> clean.append('\b')
                    'f' -> clean.append('\u000C')
                    '(' -> clean.append('(')
                    ')' -> clean.append(')')
                    '\\' -> clean.append('\\')
                    else -> clean.append(next)
                }
                i += 2
            } else {
                clean.append(str[i])
                i++
            }
        }
        return clean.toString()
    }

    private fun extractReadableStrings(bytes: ByteArray): String {
        val out = StringBuilder()
        var current = StringBuilder()
        for (b in bytes) {
            val ch = b.toInt().toChar()
            if (ch in ' '..'~' || ch in 'á'..'ú' || ch in 'Á'..'Ú' || ch == 'ñ' || ch == 'Ñ') {
                current.append(ch)
            } else {
                if (current.length >= 4) {
                    val word = current.toString()
                    if (!word.startsWith("/") && !word.startsWith("obj") && !word.startsWith("xref")) {
                        out.append(word).append(" ")
                    }
                }
                current = StringBuilder()
            }
        }
        return out.toString().trim()
    }

    fun buildExtractedDocument(fileName: String, fileType: String, text: String): ExtractedDocument {
        val cleanedText = text
            .replace(Regex("[ \\t]+"), " ")
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()

        val paragraphs = cleanedText.split(Regex("\\n+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val sentences = mutableListOf<String>()
        val sentenceRegex = Regex("(?<=[.!?])\\s+(?=[A-ZÁÉÍÓÚÑa-záéíóúñ¿¡])")
        for (p in paragraphs) {
            val parts = p.split(sentenceRegex).map { it.trim() }.filter { it.isNotBlank() }
            if (parts.isNotEmpty()) {
                sentences.addAll(parts)
            } else {
                sentences.add(p)
            }
        }

        val words = if (cleanedText.isBlank()) 0 else cleanedText.split(Regex("\\s+")).size
        val chars = cleanedText.length
        val estimatedMinutes = words / 150.0 // avg reading speed 150 wpm

        return ExtractedDocument(
            fileName = fileName,
            fileType = fileType,
            fullText = cleanedText,
            wordCount = words,
            charCount = chars,
            paragraphs = paragraphs,
            sentences = sentences,
            estimatedDurationMinutes = estimatedMinutes
        )
    }

    val sampleDocuments = listOf(
        Pair(
            "⚡ Manifiesto VVC SPEED SPEAK",
            """
            Bienvenido a VVC SPEED SPEAK, tu sistema de síntesis y lectura acelerada 100% offline.
            
            Diseñado para la máxima privacidad, todo el procesamiento de tus documentos PDF y archivos de texto plano se ejecuta de forma completamente local en tu dispositivo, sin requerir conexión a internet ni enviar tus datos a servidores externos.
            
            Con la integración de KittenTTS de KittenML como motor principal, disfrutas de voces ultra-naturales, entonación fluida y dicción sin pausas mecánicas ni tonos robóticos. Además, cuentas con Kokoro TTS como motor de respaldo automático ante cualquier fallo.
            
            Usa los controles del reproductor para ajustar la velocidad de lectura desde cero punto cinco hasta tres veces la velocidad normal, adaptándolo a tu ritmo óptimo de comprensión auditiva.
            """.trimIndent()
        ),
        Pair(
            "🐱 KittenTTS (KittenML) & Kokoro Dual Engine",
            """
            KittenTTS de KittenML ha revolucionado la síntesis local en dispositivos móviles por su extrema ligereza, baja latencia y prosodia humana en español latinoamericano.
            
            La arquitectura de doble motor en VVC SPEED SPEAK combina KittenTTS como procesador primario y Kokoro-82M como alternativa de alta expresividad, garantizando que nunca te quedes sin voz incluso en documentos de cientos de páginas.
            
            Todo funciona de manera 100% offline y en tu dispositivo.
            """.trimIndent()
        ),
        Pair(
            "📖 Test de Comprensión y Velocidad",
            """
            En un lugar de la Mancha, de cuyo nombre no quiero acordarme, no ha mucho tiempo que vivía un hidalgo de los de lanza en astillero, adarga antigua, rocín flaco y galgo corredor.
            
            Una olla de algo más vaca que carnero, salpicón las más noches, duelos y quebrantos los sábados, lantejas los viernes, algún palomino de añadidura los domingos, consumían las tres partes de su hacienda.
            
            El resto della concluían sayo de velarte, calzas de velludo para las fiestas, con sus pantuflos de lo mesmo, y los días de entresemana se honraba con su vellorí de lo más fino.
            """.trimIndent()
        )
    )
}
