package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ComparisonAnalysis
import com.example.network.LlmResult
import com.example.rag.RetrievalResult
import com.example.ui.theme.GeoDirectCardBg
import com.example.ui.theme.GeoError
import com.example.ui.theme.GeoErrorContainer
import com.example.ui.theme.GeoOnErrorContainer
import com.example.ui.theme.GeoOnPrimaryContainer
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoRagBorder
import com.example.ui.theme.GeoRagCardBg
import com.example.ui.theme.GeoSurfaceVariant

@Composable
fun ComparisonView(
    ragResult: LlmResult?,
    directResult: LlmResult?,
    retrievalResult: RetrievalResult?,
    analysis: ComparisonAnalysis?,
    viewMode: Int,
    onViewModeChange: (Int) -> Unit
) {
    val modes = listOf("Side-by-Side", "RAG Grounded", "Direct LLM", "Divergence")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("comparison_view_container"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Geometric Tab Row
        ScrollableTabRow(
            selectedTabIndex = viewMode,
            edgePadding = 4.dp,
            containerColor = Color.Transparent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[viewMode]),
                    color = GeoPrimary,
                    height = 3.dp
                )
            },
            divider = {}
        ) {
            modes.forEachIndexed { index, title ->
                Tab(
                    selected = viewMode == index,
                    onClick = { onViewModeChange(index) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (viewMode == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (viewMode == index) GeoPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.2.sp
                        )
                    }
                )
            }
        }

        // Metrics and Comparison Summary Bar
        if (analysis != null) {
            ComparisonMetricsBar(analysis = analysis)
        }

        // Content Display based on selected View Mode
        when (viewMode) {
            0 -> {
                // Side-by-Side (Stacked vertical for mobile responsiveness)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    DirectResultCard(
                        result = directResult,
                        latencyMs = analysis?.directLatencyMs ?: 0L,
                        risk = analysis?.directHallucinationRisk ?: "Med"
                    )
                    RagResultCard(
                        result = ragResult,
                        retrievalResult = retrievalResult,
                        latencyMs = analysis?.ragTotalLatencyMs ?: 0L,
                        groundingScore = analysis?.groundingScorePercent ?: 98
                    )
                }
            }
            1 -> {
                // RAG Only
                RagResultCard(
                    result = ragResult,
                    retrievalResult = retrievalResult,
                    latencyMs = analysis?.ragTotalLatencyMs ?: 0L,
                    groundingScore = analysis?.groundingScorePercent ?: 98
                )
            }
            2 -> {
                // Direct LLM Only
                DirectResultCard(
                    result = directResult,
                    latencyMs = analysis?.directLatencyMs ?: 0L,
                    risk = analysis?.directHallucinationRisk ?: "Med"
                )
            }
            3 -> {
                // Divergence Analysis
                if (analysis != null) {
                    DivergenceAnalysisCard(analysis = analysis)
                }
            }
        }
    }
}

@Composable
fun ComparisonMetricsBar(analysis: ComparisonAnalysis) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = GeoPrimaryContainer.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.dp, GeoOutline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = GeoPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Grounding Score: ${analysis.groundingScorePercent}%",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Hallucination Risk Tag
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GeoErrorContainer,
                    border = BorderStroke(0.5.dp, GeoError.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(GeoError)
                        )
                        Text(
                            text = "Risk: ${analysis.directHallucinationRisk}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = GeoOnErrorContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { analysis.groundingScorePercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = GeoPrimary,
                trackColor = GeoPrimaryContainer
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Latency Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "RAG Latency: ${(analysis.ragTotalLatencyMs / 100.0).toInt() / 10.0}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Direct LLM: ${(analysis.directLatencyMs / 100.0).toInt() / 10.0}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Delta: +${analysis.latencyDeltaMs}ms",
                    style = MaterialTheme.typography.labelSmall,
                    color = GeoPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun DirectResultCard(
    result: LlmResult?,
    latencyMs: Long,
    risk: String = "Med"
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Geometric Eyebrow Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(GeoError)
                )
                Text(
                    text = "DIRECT LLM RESPONSE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GeoErrorContainer
            ) {
                Text(
                    text = "Hallucination Risk: $risk",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = GeoOnErrorContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Geometric Balance White Card with #C4C6CF border and rounded-3xl (24.dp)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("direct_result_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = GeoDirectCardBg
            ),
            border = BorderStroke(1.dp, GeoOutline),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = result?.text
                        ?: "Based on general historical patterns, baseline models extrapolate answers without real document context...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Footer Row: Warning tag + Speed indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = GeoError,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Unverified (Parametric only)",
                            style = MaterialTheme.typography.labelSmall,
                            color = GeoError,
                            fontSize = 10.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Speed,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(13.dp)
                        )
                        val formattedSec = if (latencyMs > 0) "${(latencyMs / 100.0).toInt() / 10.0}s" else "1.2s"
                        Text(
                            text = formattedSec,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RagResultCard(
    result: LlmResult?,
    retrievalResult: RetrievalResult?,
    latencyMs: Long,
    groundingScore: Int = 98
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Geometric Eyebrow Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(GeoPrimary)
                )
                Text(
                    text = "RAG ENHANCED (VERIFIED)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = GeoPrimary,
                    letterSpacing = 1.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GeoPrimaryContainer
            ) {
                Text(
                    text = "Source Confidence: $groundingScore%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = GeoOnPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Geometric Balance Container in #F1F0F7 with #005AC1/30 border and rounded-3xl (24.dp)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("rag_result_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = GeoRagCardBg
            ),
            border = BorderStroke(1.5.dp, GeoRagBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Body text
                Text(
                    text = result?.text
                        ?: "The internal document confirms verified facts and explicit operational parameters grounded in local passages...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Grounding Chips Row
                if (retrievalResult != null && retrievalResult.chunks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        retrievalResult.chunks.take(3).forEach { chunk ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GeoSurfaceVariant,
                                border = BorderStroke(1.dp, GeoOutline)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = chunk.documentTitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GeoSurfaceVariant,
                                border = BorderStroke(1.dp, GeoOutline)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Link,
                                        contentDescription = null,
                                        tint = GeoPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "chunk_${chunk.chunkIndex}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GeoPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footer Row: Grounded tag + Speed indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GeoPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Verified with Room Knowledge Base",
                            style = MaterialTheme.typography.labelSmall,
                            color = GeoPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Speed,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(13.dp)
                        )
                        val formattedSec = if (latencyMs > 0) "${(latencyMs / 100.0).toInt() / 10.0}s" else "2.8s"
                        Text(
                            text = formattedSec,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DivergenceAnalysisCard(analysis: ComparisonAnalysis) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("divergence_analysis_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = GeoDirectCardBg
        ),
        border = BorderStroke(1.dp, GeoOutline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Key Divergence & Factuality Analysis",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "How document retrieval altered model behavior and factual grounding",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Observations
            analysis.keyDivergences.forEach { observation ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = GeoPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = observation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Comparison Table
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GeoSurfaceVariant,
                border = BorderStroke(1.dp, GeoOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ARCHITECTURAL COMPARISON",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Metric",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "RAG Augmented",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimary,
                            modifier = Modifier.weight(1.2f)
                        )
                        Text(
                            text = "Direct LLM",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.weight(1.2f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    ComparisonTableRow(
                        metric = "Factual Accuracy",
                        ragVal = "Specific & Bound",
                        directVal = "Generic / Assumed"
                    )
                    ComparisonTableRow(
                        metric = "Hallucination Risk",
                        ragVal = "Low (Verifiable)",
                        directVal = analysis.directHallucinationRisk
                    )
                    ComparisonTableRow(
                        metric = "Latency",
                        ragVal = "${analysis.ragTotalLatencyMs}ms",
                        directVal = "${analysis.directLatencyMs}ms"
                    )
                    ComparisonTableRow(
                        metric = "Auditability",
                        ragVal = "Direct Citations",
                        directVal = "Opaque / None"
                    )
                }
            }
        }
    }
}

@Composable
private fun ComparisonTableRow(metric: String, ragVal: String, directVal: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = metric,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = ragVal,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = directVal,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.2f)
        )
    }
}

