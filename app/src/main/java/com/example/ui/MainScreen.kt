package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddDocumentDialog
import com.example.ui.components.ApiKeyDialog
import com.example.ui.components.ComparisonView
import com.example.ui.components.DocumentDetailDialog
import com.example.ui.components.HistoryScreen
import com.example.ui.components.KnowledgeBaseScreen
import com.example.ui.components.PromptInputSection
import com.example.ui.components.RetrievedChunksView
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoNavBackground
import com.example.ui.theme.GeoOnBackground
import com.example.ui.theme.GeoOnPrimaryContainer
import com.example.ui.theme.GeoOnSecondaryContainer
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoSecondary
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val documents by viewModel.documents.collectAsState()
    val history by viewModel.history.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = GeoBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // Geometric Balance Header: 64dp high, border-b #E1E2E9
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(
                            color = GeoOutlineVariant,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    },
                color = GeoBackground
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Brand Section: 40dp circle menu + Title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GeoSurfaceVariant,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = GeoSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Text(
                            text = "RAG Bench",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            color = GeoOnBackground,
                            letterSpacing = (-0.3).sp
                        )
                    }

                    // Right Actions Section: Circular Account / API Key Status
                    Surface(
                        shape = CircleShape,
                        color = GeoPrimaryContainer,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .testTag("api_key_dialog_button")
                    ) {
                        IconButton(
                            onClick = { viewModel.setApiKeyDialogVisible(true) },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (viewModel.geminiClient.hasValidApiKey()) {
                                    Icons.Default.AccountCircle
                                } else {
                                    Icons.Default.Key
                                },
                                contentDescription = "API Status",
                                tint = GeoOnPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Geometric Balance Bottom Nav: #F3F3FA background with top border #E1E2E9
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(
                            color = GeoOutlineVariant,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    },
                color = GeoNavBackground
            ) {
                NavigationBar(
                    containerColor = GeoNavBackground,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(72.dp)
                ) {
                    NavigationBarItem(
                        selected = uiState.selectedNavTab == 0,
                        onClick = { viewModel.setNavTab(0) },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                                contentDescription = "Compare",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                "Compare",
                                fontWeight = if (uiState.selectedNavTab == 0) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GeoOnSecondaryContainer,
                            selectedTextColor = GeoOnBackground,
                            indicatorColor = GeoPrimaryContainer,
                            unselectedIconColor = GeoSecondary,
                            unselectedTextColor = GeoSecondary
                        ),
                        modifier = Modifier.testTag("nav_compare")
                    )

                    NavigationBarItem(
                        selected = uiState.selectedNavTab == 1,
                        onClick = { viewModel.setNavTab(1) },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.LibraryBooks,
                                contentDescription = "Documents",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                "Documents",
                                fontWeight = if (uiState.selectedNavTab == 1) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GeoOnSecondaryContainer,
                            selectedTextColor = GeoOnBackground,
                            indicatorColor = GeoPrimaryContainer,
                            unselectedIconColor = GeoSecondary,
                            unselectedTextColor = GeoSecondary
                        ),
                        modifier = Modifier.testTag("nav_documents")
                    )

                    NavigationBarItem(
                        selected = uiState.selectedNavTab == 2,
                        onClick = { viewModel.setNavTab(2) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                "History",
                                fontWeight = if (uiState.selectedNavTab == 2) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GeoOnSecondaryContainer,
                            selectedTextColor = GeoOnBackground,
                            indicatorColor = GeoPrimaryContainer,
                            unselectedIconColor = GeoSecondary,
                            unselectedTextColor = GeoSecondary
                        ),
                        modifier = Modifier.testTag("nav_history")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(GeoBackground)
        ) {
            when (uiState.selectedNavTab) {
                0 -> {
                    // Compare Screen
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Prompt Input Section
                        PromptInputSection(
                            query = uiState.query,
                            isLoading = uiState.isLoading,
                            onQueryChange = { viewModel.onQueryChange(it) },
                            onRunComparison = { viewModel.runComparison() },
                            onPreviewRetrieval = { viewModel.runRetrievalPreview() },
                            onSelectSample = { sample ->
                                viewModel.onQueryChange(sample)
                                viewModel.runComparison(sample)
                            }
                        )

                        // Retrieved Chunks View (when available)
                        uiState.retrievalResult?.let { result ->
                            RetrievedChunksView(
                                retrievalResult = result,
                                isExpanded = uiState.isContextExpanded,
                                onToggleExpand = { viewModel.toggleContextExpanded() }
                            )
                        }

                        // Comparison Results (when available)
                        if (uiState.ragResult != null || uiState.directResult != null) {
                            ComparisonView(
                                ragResult = uiState.ragResult,
                                directResult = uiState.directResult,
                                retrievalResult = uiState.retrievalResult,
                                analysis = uiState.comparisonAnalysis,
                                viewMode = uiState.viewMode,
                                onViewModeChange = { viewModel.setViewMode(it) }
                            )
                        } else if (uiState.retrievalResult == null && !uiState.isLoading) {
                            // Empty State / Concept Intro
                            ConceptHeroCard(
                                onSelectQuickPrompt = { sample ->
                                    viewModel.onQueryChange(sample)
                                    viewModel.runComparison(sample)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                1 -> {
                    // Knowledge Base Documents Screen
                    KnowledgeBaseScreen(
                        documents = documents,
                        onToggleActive = { viewModel.toggleDocumentActive(it) },
                        onDeleteDocument = { viewModel.deleteDocument(it) },
                        onOpenAddDocDialog = { viewModel.setAddDocDialogVisible(true) },
                        onResetDefaults = { viewModel.resetKnowledgeBase() },
                        onSelectDocForDetails = { viewModel.setSelectedDoc(it) }
                    )
                }

                2 -> {
                    // History Screen
                    HistoryScreen(
                        historyList = history,
                        onSelectHistoryItem = { viewModel.loadHistoryItem(it) },
                        onDeleteHistoryItem = { viewModel.deleteHistoryItem(it) },
                        onClearAll = { viewModel.clearAllHistory() }
                    )
                }
            }
        }
    }

    // Dialogs
    if (uiState.showApiKeyDialog) {
        ApiKeyDialog(
            currentKey = viewModel.geminiClient.customApiKey ?: "",
            onDismiss = { viewModel.setApiKeyDialogVisible(false) },
            onSave = { viewModel.saveApiKey(it) }
        )
    }

    if (uiState.showAddDocDialog) {
        AddDocumentDialog(
            onDismiss = { viewModel.setAddDocDialogVisible(false) },
            onAdd = { title, category, content ->
                viewModel.addDocument(title, category, content)
            }
        )
    }

    uiState.selectedDocForDetails?.let { doc ->
        DocumentDetailDialog(
            doc = doc,
            onDismiss = { viewModel.setSelectedDoc(null) }
        )
    }
}

@Composable
fun ConceptHeroCard(onSelectQuickPrompt: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("concept_hero_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = GeoSurface
        ),
        border = BorderStroke(1.dp, GeoOutline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = GeoPrimaryContainer,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Lightbulb,
                            contentDescription = null,
                            tint = GeoPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = "UNDERSTANDING RAG BENCHMARK",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GeoSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Compare the factual difference between querying a Foundation LLM directly versus augmenting the prompt with verified passages retrieved from local documents.",
                style = MaterialTheme.typography.bodyMedium,
                color = GeoSecondary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Step comparison
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GeoSurfaceVariant,
                border = BorderStroke(1.dp, GeoOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("1.", fontWeight = FontWeight.Bold, color = GeoPrimary)
                        Text(
                            "Document Indexing: Knowledge articles are split into indexed passages stored in the local Room database.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("2.", fontWeight = FontWeight.Bold, color = GeoPrimary)
                        Text(
                            "RAG Retrieval: The BM25 retrieval engine scans indexed chunks for keyword & semantic overlap with the query.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("3.", fontWeight = FontWeight.Bold, color = GeoPrimary)
                        Text(
                            "Dual Execution: Both RAG (grounded with passages) and Direct LLM (isolated) run side-by-side to highlight factual citations vs hallucinations.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Tap any benchmark query above to run the side-by-side comparison.",
                style = MaterialTheme.typography.labelSmall,
                color = GeoPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

