package com.example.ui.components

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FindInPage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoSurfaceVariant

@Composable
fun PromptInputSection(
    query: String,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onRunComparison: () -> Unit,
    onPreviewRetrieval: () -> Unit,
    onSelectSample: (String) -> Unit
) {
    val sampleQueries = listOf(
        "Warranty clause for CryoCore ERR-804 thermal drift?",
        "Daily meal per-diem and flight booking advance notice?",
        "Maximum wind resistance limit for Vanguard-7 drone?",
        "How is liquid Helium-3 replenishment scheduled?",
        "What is the equipment stipend amount and GL code?",
        "What frequency does the emergency beacon broadcast on?"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Geometric Balance Container: #E1E2E9 background, #C4C6CF border, rounded-2xl
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = GeoSurfaceVariant,
            border = BorderStroke(1.dp, GeoOutline),
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Eyebrow and Search Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PROMPT",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        BasicTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("query_input_field"),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Normal
                            ),
                            cursorBrush = SolidColor(GeoPrimary),
                            decorationBox = { innerTextField ->
                                if (query.isEmpty()) {
                                    Text(
                                        text = "Ask about audit findings, specs, or internal policies...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }

                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = { onQueryChange("") },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("clear_query_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear query",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Circular Geometric Action Button in #005AC1
                    Surface(
                        shape = CircleShape,
                        color = GeoPrimary,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape),
                        shadowElevation = 2.dp
                    ) {
                        IconButton(
                            onClick = onRunComparison,
                            enabled = query.isNotBlank() && !isLoading,
                            modifier = Modifier.size(38.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Run comparison",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons Row with geometric rounding
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onRunComparison,
                        enabled = query.isNotBlank() && !isLoading,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("run_comparison_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GeoPrimary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Compare", fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = onPreviewRetrieval,
                        enabled = query.isNotBlank() && !isLoading,
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("preview_retrieval_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, GeoOutline)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FindInPage,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = GeoPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Preview RAG",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Quick Test Questions Carousel
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "QUICK BENCHMARK QUERIES",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sampleQueries.forEach { sample ->
                    FilterChip(
                        selected = query == sample,
                        onClick = { onSelectSample(sample) },
                        label = {
                            Text(
                                text = sample,
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GeoPrimaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = query == sample,
                            borderColor = GeoOutline
                        )
                    )
                }
            }
        }
    }
}

