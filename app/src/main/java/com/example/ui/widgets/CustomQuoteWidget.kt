package com.example.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WidgetSize

@Composable
fun CustomQuoteWidget(
    title: String,
    customData: String, // "Quote text | Author"
    size: WidgetSize = WidgetSize.STANDARD,
    onUpdateConfig: (title: String, customData: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }

    val parts = remember(customData) {
        if (customData.contains("|")) customData.split("|")
        else listOf("Simplicity is the soul of efficiency.", "Austin Freeman")
    }
    val quoteText = parts.getOrNull(0)?.trim() ?: "Simplicity is the soul of efficiency."
    val authorText = parts.getOrNull(1)?.trim() ?: "Austin Freeman"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showEditDialog = true }
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FormatQuote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = if (title.isNotBlank()) title else "Daily Focus",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "\"$quoteText\"",
            fontSize = if (size == WidgetSize.COMPACT) 12.sp else 14.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = if (size == WidgetSize.COMPACT) 16.sp else 20.sp
        )

        if (size != WidgetSize.COMPACT && authorText.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "— $authorText",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }

    if (showEditDialog) {
        var editQuote by remember { mutableStateOf(quoteText) }
        var editAuthor by remember { mutableStateOf(authorText) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Daily Affirmation") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editQuote,
                        onValueChange = { editQuote = it },
                        label = { Text("Quote / Motto") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAuthor,
                        onValueChange = { editAuthor = it },
                        label = { Text("Author / Note") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateConfig(title, "${editQuote.trim()}|${editAuthor.trim()}")
                        showEditDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
