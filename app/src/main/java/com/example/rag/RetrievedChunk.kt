package com.example.rag

data class RetrievedChunk(
    val chunkId: Long,
    val documentTitle: String,
    val chunkIndex: Int,
    val text: String,
    val score: Float,
    val matchedTerms: List<String>
)

data class RetrievalResult(
    val chunks: List<RetrievedChunk>,
    val retrievalTimeMs: Long,
    val totalChunksSearched: Int
)
