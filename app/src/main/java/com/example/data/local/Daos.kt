package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocumentById(id: Long): DocumentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DocumentEntity): Long

    @Update
    suspend fun updateDocument(doc: DocumentEntity)

    @Delete
    suspend fun deleteDocument(doc: DocumentEntity)

    @Query("DELETE FROM documents")
    suspend fun clearAllDocuments()

    // Chunks
    @Query("SELECT * FROM document_chunks ORDER BY documentId ASC, chunkIndex ASC")
    fun getAllChunks(): Flow<List<DocumentChunkEntity>>

    @Query("SELECT * FROM document_chunks WHERE documentId = :documentId ORDER BY chunkIndex ASC")
    fun getChunksForDocument(documentId: Long): Flow<List<DocumentChunkEntity>>

    @Query("SELECT dc.* FROM document_chunks dc INNER JOIN documents d ON dc.documentId = d.id WHERE d.isActive = 1")
    suspend fun getActiveChunks(): List<DocumentChunkEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChunks(chunks: List<DocumentChunkEntity>)

    @Query("DELETE FROM document_chunks WHERE documentId = :documentId")
    suspend fun deleteChunksForDocument(documentId: Long)

    @Query("DELETE FROM document_chunks")
    suspend fun clearAllChunks()
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM comparison_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<ComparisonHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: ComparisonHistoryEntity): Long

    @Delete
    suspend fun deleteHistory(item: ComparisonHistoryEntity)

    @Query("DELETE FROM comparison_history")
    suspend fun clearAllHistory()
}
