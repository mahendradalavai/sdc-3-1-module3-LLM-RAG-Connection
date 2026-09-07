package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.ComparisonHistoryEntity
import com.example.data.local.DocumentChunkEntity
import com.example.data.local.DocumentEntity
import com.example.data.local.PreloadedData
import com.example.rag.DocumentChunker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class KnowledgeRepository(private val database: AppDatabase) {

    private val documentDao = database.documentDao()
    private val historyDao = database.historyDao()

    val allDocuments: Flow<List<DocumentEntity>> = documentDao.getAllDocuments()
    val allHistory: Flow<List<ComparisonHistoryEntity>> = historyDao.getAllHistory()

    suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
        val currentDocs = documentDao.getAllDocuments().first()
        if (currentDocs.isEmpty()) {
            for (doc in PreloadedData.sampleDocuments) {
                val docId = documentDao.insertDocument(doc.copy(id = 0))
                val savedDoc = doc.copy(id = docId)
                val chunks = DocumentChunker.chunkDocument(savedDoc)
                documentDao.insertChunks(chunks)
            }
        }
    }

    suspend fun addDocument(title: String, category: String, content: String): Long = withContext(Dispatchers.IO) {
        val newDoc = DocumentEntity(
            title = title.trim(),
            category = category.trim(),
            content = content.trim(),
            createdAt = System.currentTimeMillis(),
            isActive = true
        )
        val docId = documentDao.insertDocument(newDoc)
        val savedDoc = newDoc.copy(id = docId)
        val chunks = DocumentChunker.chunkDocument(savedDoc)
        documentDao.insertChunks(chunks)
        docId
    }

    suspend fun toggleDocumentActive(doc: DocumentEntity) = withContext(Dispatchers.IO) {
        documentDao.updateDocument(doc.copy(isActive = !doc.isActive))
    }

    suspend fun deleteDocument(doc: DocumentEntity) = withContext(Dispatchers.IO) {
        documentDao.deleteChunksForDocument(doc.id)
        documentDao.deleteDocument(doc)
    }

    suspend fun resetToDefaultSampleData() = withContext(Dispatchers.IO) {
        documentDao.clearAllChunks()
        documentDao.clearAllDocuments()
        for (doc in PreloadedData.sampleDocuments) {
            val docId = documentDao.insertDocument(doc.copy(id = 0))
            val savedDoc = doc.copy(id = docId)
            val chunks = DocumentChunker.chunkDocument(savedDoc)
            documentDao.insertChunks(chunks)
        }
    }

    suspend fun getActiveChunks(): List<DocumentChunkEntity> = withContext(Dispatchers.IO) {
        documentDao.getActiveChunks()
    }

    suspend fun getChunksForDocument(docId: Long): Flow<List<DocumentChunkEntity>> {
        return documentDao.getChunksForDocument(docId)
    }

    suspend fun saveComparison(item: ComparisonHistoryEntity): Long = withContext(Dispatchers.IO) {
        historyDao.insertHistory(item)
    }

    suspend fun deleteHistory(item: ComparisonHistoryEntity) = withContext(Dispatchers.IO) {
        historyDao.deleteHistory(item)
    }

    suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        historyDao.clearAllHistory()
    }
}
