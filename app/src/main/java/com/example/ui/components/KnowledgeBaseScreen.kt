package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DocumentEntity
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoError
import com.example.ui.theme.GeoOnBackground
import com.example.ui.theme.GeoOnPrimary
import com.example.ui.theme.GeoOnPrimaryContainer
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoOutlineVariant
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoSecondary
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoSurfaceVariant

@Composable
fun KnowledgeBaseScreen(
    documents: List<DocumentEntity>,
    onToggleActive: (DocumentEntity) -> Unit,
    onDeleteDocument: (DocumentEntity) -> Unit,
    onOpenAddDocDialog: () -> Unit,
    onResetDefaults: () -> Unit,
    onSelectDocForDetails: (DocumentEntity) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GeoBackground)
            .testTag("knowledge_base_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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
                        text = "Knowledge Base",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = GeoOnBackground
                    )
                    Text(
                        text = "${documents.size} local documents indexed for RAG retrieval",
                        style = MaterialTheme.typography.bodySmall,
                        color = GeoSecondary
                    )
                }

                OutlinedButton(
                    onClick = onResetDefaults,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GeoOutline)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        tint = GeoPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset", color = GeoPrimary, fontWeight = FontWeight.Medium)
                }
            }

            // Document List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(documents, key = { it.id }) { doc ->
                    DocumentItemCard(
                        doc = doc,
                        onToggleActive = { onToggleActive(doc) },
                        onDelete = { onDeleteDocument(doc) },
                        onClick = { onSelectDocForDetails(doc) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }

        // Floating Action Button to Add Document
        FloatingActionButton(
            onClick = onOpenAddDocDialog,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_document_fab"),
            containerColor = GeoPrimary,
            contentColor = GeoOnPrimary,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Document")
        }
    }
}

@Composable
fun DocumentItemCard(
    doc: DocumentEntity,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("doc_card_${doc.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (doc.isActive) GeoSurface else GeoSurfaceVariant
        ),
        border = BorderStroke(1.dp, if (doc.isActive) GeoOutline else GeoOutlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = if (doc.isActive) 1.dp else 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (doc.isActive) GeoPrimaryContainer else GeoSurfaceVariant,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = if (doc.isActive) GeoPrimary else GeoSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = doc.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (doc.isActive) GeoOnBackground else GeoSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GeoSurfaceVariant,
                            border = BorderStroke(1.dp, GeoOutlineVariant)
                        ) {
                            Text(
                                text = doc.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = GeoPrimary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Switch(
                        checked = doc.isActive,
                        onCheckedChange = { onToggleActive() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GeoPrimary,
                            checkedTrackColor = GeoPrimaryContainer
                        ),
                        modifier = Modifier.testTag("doc_switch_${doc.id}")
                    )
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete document",
                            tint = GeoError
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = doc.content.take(160).replace("\n", " ") + if (doc.content.length > 160) "..." else "",
                style = MaterialTheme.typography.bodySmall,
                color = GeoSecondary,
                lineHeight = 18.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (doc.isActive) "✓ Active in BM25 search index" else "⊘ Disabled from search index",
                style = MaterialTheme.typography.labelSmall,
                color = if (doc.isActive) GeoPrimary else GeoSecondary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AddDocumentDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, category: String, content: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Internal Spec") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = GeoSurface,
        title = {
            Text(text = "Add Knowledge Document", fontWeight = FontWeight.Bold, color = GeoOnBackground)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Text will be split into overlapping passages and indexed into Room DB for RAG search.",
                    style = MaterialTheme.typography.bodySmall,
                    color = GeoSecondary
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Document Title") },
                    placeholder = { Text("e.g. Project Helios Architecture") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_doc_title_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GeoPrimary,
                        unfocusedBorderColor = GeoOutline
                    )
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GeoPrimary,
                        unfocusedBorderColor = GeoOutline
                    )
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Document Content") },
                    placeholder = { Text("Paste policy text, specs, instructions, or error codes...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .testTag("new_doc_content_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GeoPrimary,
                        unfocusedBorderColor = GeoOutline
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(title, category, content) },
                enabled = title.isNotBlank() && content.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                modifier = Modifier.testTag("confirm_add_doc_button")
            ) {
                Text("Index & Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = GeoSecondary)
            }
        }
    )
}

@Composable
fun DocumentDetailDialog(
    doc: DocumentEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = GeoSurface,
        title = {
            Column {
                Text(text = doc.title, fontWeight = FontWeight.Bold, color = GeoOnBackground)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GeoPrimaryContainer
                ) {
                    Text(
                        text = doc.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = GeoOnPrimaryContainer,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = doc.content,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 20.sp,
                    color = GeoOnBackground
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
            ) {
                Text("Close")
            }
        }
    )
}

