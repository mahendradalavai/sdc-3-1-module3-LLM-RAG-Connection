package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ComparisonHistoryEntity
import com.example.data.local.DocumentEntity
import com.example.data.repository.KnowledgeRepository
import com.example.model.ComparisonAnalysis
import com.example.model.ComparisonEvaluator
import com.example.network.GeminiClient
import com.example.network.LlmResult
import com.example.rag.RagRetriever
import com.example.rag.RetrievalResult
import com.example.rag.RetrievedChunk
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MainUiState(
    val selectedNavTab: Int = 0, // 0: Compare, 1: Knowledge Base, 2: History
    val query: String = "",
    val isLoading: Boolean = false,
    val isRetrieving: Boolean = false,
    val isQueryingRag: Boolean = false,
    val isQueryingDirect: Boolean = false,
    val retrievalResult: RetrievalResult? = null,
    val ragResult: LlmResult? = null,
    val directResult: LlmResult? = null,
    val comparisonAnalysis: ComparisonAnalysis? = null,
    val viewMode: Int = 0, // 0: Side-by-Side, 1: RAG View, 2: Direct View, 3: Difference Analysis
    val showApiKeyDialog: Boolean = false,
    val showAddDocDialog: Boolean = false,
    val selectedDocForDetails: DocumentEntity? = null,
    val isContextExpanded: Boolean = true,
    val statusMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = KnowledgeRepository(database)
    val geminiClient = GeminiClient()

    val documents: StateFlow<List<DocumentEntity>> = repository.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<ComparisonHistoryEntity>> = repository.allHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
        }
    }

    fun setNavTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedNavTab = index)
    }

    fun setViewMode(mode: Int) {
        _uiState.value = _uiState.value.copy(viewMode = mode)
    }

    fun onQueryChange(text: String) {
        _uiState.value = _uiState.value.copy(query = text)
    }

    fun toggleContextExpanded() {
        _uiState.value = _uiState.value.copy(isContextExpanded = !_uiState.value.isContextExpanded)
    }

    fun setApiKeyDialogVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showApiKeyDialog = visible)
    }

    fun setAddDocDialogVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showAddDocDialog = visible)
    }

    fun setSelectedDoc(doc: DocumentEntity?) {
        _uiState.value = _uiState.value.copy(selectedDocForDetails = doc)
    }

    fun saveApiKey(key: String) {
        geminiClient.customApiKey = key.trim()
        _uiState.value = _uiState.value.copy(
            showApiKeyDialog = false,
            statusMessage = if (key.isNotBlank()) "Custom API Key saved" else "Using default/demo mode"
        )
    }

    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }

    fun runRetrievalPreview() {
        val q = _uiState.value.query.trim()
        if (q.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRetrieving = true)
            val chunks = repository.getActiveChunks()
            val result = RagRetriever.retrieve(q, chunks, topK = 3)
            _uiState.value = _uiState.value.copy(
                isRetrieving = false,
                retrievalResult = result,
                isContextExpanded = true
            )
        }
    }

    fun runComparison(queryOverride: String? = null) {
        val q = (queryOverride ?: _uiState.value.query).trim()
        if (q.isBlank()) return

        if (queryOverride != null) {
            _uiState.value = _uiState.value.copy(query = q)
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                isRetrieving = true,
                isQueryingRag = true,
                isQueryingDirect = true,
                ragResult = null,
                directResult = null,
                comparisonAnalysis = null
            )

            // Step 1: Retrieval from Room Active Chunks
            val activeChunks = repository.getActiveChunks()
            val retrievalResult = RagRetriever.retrieve(q, activeChunks, topK = 3)
            _uiState.value = _uiState.value.copy(
                isRetrieving = false,
                retrievalResult = retrievalResult
            )

            // Step 2: Concurrent LLM queries: RAG vs Direct
            val ragDeferred = async {
                geminiClient.generateRagResponse(q, retrievalResult.chunks)
            }
            val directDeferred = async {
                geminiClient.generateDirectResponse(q)
            }

            val ragRes = ragDeferred.await()
            _uiState.value = _uiState.value.copy(isQueryingRag = false, ragResult = ragRes)

            val directRes = directDeferred.await()
            _uiState.value = _uiState.value.copy(isQueryingDirect = false, directResult = directRes)

            // Step 3: Compute comparison analysis metrics
            val analysis = ComparisonEvaluator.evaluate(
                query = q,
                retrievalResult = retrievalResult,
                ragResult = ragRes,
                directResult = directRes
            )

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                comparisonAnalysis = analysis
            )

            // Step 4: Persist to History in Room Database
            val chunksSummary = retrievalResult.chunks.joinToString(" | ") {
                "${it.documentTitle} (Score: ${(it.score * 100).toInt()}%)"
            }
            val historyEntity = ComparisonHistoryEntity(
                query = q,
                ragAnswer = ragRes.text,
                directAnswer = directRes.text,
                retrievedChunksJson = chunksSummary,
                ragLatencyMs = analysis.ragTotalLatencyMs,
                directLatencyMs = analysis.directLatencyMs,
                retrievalLatencyMs = retrievalResult.retrievalTimeMs
            )
            repository.saveComparison(historyEntity)
        }
    }

    fun toggleDocumentActive(doc: DocumentEntity) {
        viewModelScope.launch {
            repository.toggleDocumentActive(doc)
            _uiState.value = _uiState.value.copy(
                statusMessage = if (!doc.isActive) "Included in RAG context" else "Excluded from RAG context"
            )
        }
    }

    fun deleteDocument(doc: DocumentEntity) {
        viewModelScope.launch {
            repository.deleteDocument(doc)
            _uiState.value = _uiState.value.copy(
                selectedDocForDetails = null,
                statusMessage = "Document removed from knowledge base"
            )
        }
    }

    fun addDocument(title: String, category: String, content: String) {
        viewModelScope.launch {
            repository.addDocument(title, category, content)
            _uiState.value = _uiState.value.copy(
                showAddDocDialog = false,
                statusMessage = "Document ingested & indexed into chunks"
            )
        }
    }

    fun resetKnowledgeBase() {
        viewModelScope.launch {
            repository.resetToDefaultSampleData()
            _uiState.value = _uiState.value.copy(
                statusMessage = "Knowledge base reset to standard documents"
            )
        }
    }

    fun loadHistoryItem(item: ComparisonHistoryEntity) {
        _uiState.value = _uiState.value.copy(
            selectedNavTab = 0,
            query = item.query,
            ragResult = LlmResult(
                text = item.ragAnswer,
                latencyMs = item.ragLatencyMs,
                isSimulated = false
            ),
            directResult = LlmResult(
                text = item.directAnswer,
                latencyMs = item.directLatencyMs,
                isSimulated = false
            ),
            comparisonAnalysis = ComparisonEvaluator.evaluate(
                query = item.query,
                retrievalResult = null,
                ragResult = LlmResult(text = item.ragAnswer, latencyMs = item.ragLatencyMs, isSimulated = false),
                directResult = LlmResult(text = item.directAnswer, latencyMs = item.directLatencyMs, isSimulated = false)
            ),
            statusMessage = "Loaded comparison from history"
        )
    }

    fun deleteHistoryItem(item: ComparisonHistoryEntity) {
        viewModelScope.launch {
            repository.deleteHistory(item)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
            _uiState.value = _uiState.value.copy(statusMessage = "History cleared")
        }
    }
}
