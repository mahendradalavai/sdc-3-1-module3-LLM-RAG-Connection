package com.example

import com.example.data.local.PreloadedData
import com.example.rag.DocumentChunker
import com.example.rag.RagRetriever
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RagRetrieverTest {

    @Test
    fun testRetrievalReturnsRelevantPassages() {
        val sampleDoc = PreloadedData.sampleDocuments.first()
        val chunks = DocumentChunker.chunkDocument(sampleDoc)

        assertFalse("Chunks should not be empty", chunks.isEmpty())

        val result = RagRetriever.retrieve("What is error code ERR-804 and warranty replacement?", chunks, topK = 2)

        assertFalse("Retrieved chunks should not be empty", result.chunks.isEmpty())
        val topChunk = result.chunks.first()
        assertTrue("Top chunk should contain error code or warranty", topChunk.text.contains("ERR-804") || topChunk.text.contains("Warranty"))
        assertTrue("Score should be positive", topChunk.score > 0f)
    }

    @Test
    fun testHRPolicyRetrieval() {
        val hrDoc = PreloadedData.sampleDocuments[1]
        val chunks = DocumentChunker.chunkDocument(hrDoc)

        val result = RagRetriever.retrieve("What is the equipment stipend under GL code EXP-9421?", chunks, topK = 2)

        assertFalse("Retrieved chunks should not be empty", result.chunks.isEmpty())
        val topChunk = result.chunks.first()
        assertTrue("Top chunk should mention EXP-9421 or stipend", topChunk.text.contains("EXP-9421") || topChunk.text.contains("stipend"))
    }
}
