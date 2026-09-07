package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

@Entity(tableName = "document_chunks")
data class DocumentChunkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val documentId: Long,
    val documentTitle: String,
    val chunkIndex: Int,
    val text: String,
    val tokenCount: Int = 0
)

@Entity(tableName = "comparison_history")
data class ComparisonHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val query: String,
    val ragAnswer: String,
    val directAnswer: String,
    val retrievedChunksJson: String,
    val ragLatencyMs: Long,
    val directLatencyMs: Long,
    val retrievalLatencyMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)
