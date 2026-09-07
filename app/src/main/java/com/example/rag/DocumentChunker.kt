package com.example.rag

import com.example.data.local.DocumentChunkEntity
import com.example.data.local.DocumentEntity

object DocumentChunker {

    fun chunkDocument(doc: DocumentEntity, maxChunkChars: Int = 380, overlapChars: Int = 60): List<DocumentChunkEntity> {
        val rawSections = doc.content
            .split(Regex("\n\\s*\n"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val chunks = mutableListOf<String>()

        for (section in rawSections) {
            if (section.length <= maxChunkChars) {
                chunks.add(section)
            } else {
                // Split long section into overlapping chunks
                var start = 0
                while (start < section.length) {
                    val end = minOf(start + maxChunkChars, section.length)
                    val chunkText = section.substring(start, end).trim()
                    if (chunkText.isNotEmpty()) {
                        chunks.add(chunkText)
                    }
                    if (end >= section.length) break
                    start += (maxChunkChars - overlapChars)
                }
            }
        }

        return chunks.mapIndexed { index, text ->
            val approximateTokens = text.split(Regex("\\s+")).filter { it.isNotBlank() }.size
            DocumentChunkEntity(
                documentId = doc.id,
                documentTitle = doc.title,
                chunkIndex = index + 1,
                text = text,
                tokenCount = approximateTokens
            )
        }
    }
}
