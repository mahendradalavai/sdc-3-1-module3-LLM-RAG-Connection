package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ComparisonHistoryEntity
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoError
import com.example.ui.theme.GeoOnBackground
import com.example.ui.theme.GeoOnPrimaryContainer
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoSecondary
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoSurfaceVariant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    historyList: List<ComparisonHistoryEntity>,
    onSelectHistoryItem: (ComparisonHistoryEntity) -> Unit,
    onDeleteHistoryItem: (ComparisonHistoryEntity) -> Unit,
    onClearAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GeoBackground)
            .padding(16.dp)
            .testTag("history_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Benchmark History",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GeoOnBackground
                )
                Text(
                    text = "${historyList.size} logged comparison sessions",
                    style = MaterialTheme.typography.bodySmall,
                    color = GeoSecondary
                )
            }

            if (historyList.isNotEmpty()) {
                OutlinedButton(
                    onClick = onClearAll,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GeoOutline)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = GeoSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Clear All", color = GeoSecondary, fontWeight = FontWeight.Medium)
                }
            }
        }

        if (historyList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = GeoOutline
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No comparisons recorded yet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = GeoOnBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Run benchmark queries on the Compare tab to inspect and contrast RAG vs Direct LLM answers.",
                    style = MaterialTheme.typography.bodySmall,
                    color = GeoSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historyList, key = { it.id }) { item ->
                    HistoryItemCard(
                        item = item,
                        onClick = { onSelectHistoryItem(item) },
                        onDelete = { onDeleteHistoryItem(item) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    item: ComparisonHistoryEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(item.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("history_item_${item.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = GeoSurface
        ),
        border = BorderStroke(1.dp, GeoOutline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                        imageVector = Icons.AutoMirrored.Outlined.CompareArrows,
                        contentDescription = null,
                        tint = GeoPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = GeoSecondary
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete item",
                        tint = GeoError.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.query,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = GeoOnBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GeoPrimaryContainer
                ) {
                    Text(
                        text = "RAG: ${(item.ragLatencyMs / 100.0).toInt() / 10.0}s",
                        style = MaterialTheme.typography.labelSmall,
                        color = GeoOnPrimaryContainer,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GeoSurfaceVariant,
                    border = BorderStroke(1.dp, GeoOutlineVariant)
                ) {
                    Text(
                        text = "Direct: ${(item.directLatencyMs / 100.0).toInt() / 10.0}s",
                        style = MaterialTheme.typography.labelSmall,
                        color = GeoSecondary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            if (item.retrievedChunksJson.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sources: ${item.retrievedChunksJson}",
                    style = MaterialTheme.typography.labelSmall,
                    color = GeoSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

