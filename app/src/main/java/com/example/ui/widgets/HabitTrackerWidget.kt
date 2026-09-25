package com.example.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WidgetSize

data class HabitItem(val name: String, val isCompleted: Boolean)

@Composable
fun HabitTrackerWidget(
    title: String,
    customData: String, // Comma-separated: "Habit 1:1,Habit 2:0,Habit 3:0"
    size: WidgetSize = WidgetSize.STANDARD,
    onUpdateConfig: (title: String, customData: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    val displayTitle = if (title.isBlank()) "Daily Habits" else title

    // Parse habits or default
    val habits = remember(customData) {
        if (customData.isBlank()) {
            listOf(
                HabitItem("Drink 2L Water", false),
                HabitItem("Workout / Walk 30m", false),
                HabitItem("Read 15 mins", false)
            )
        } else {
            customData.split(",").mapNotNull { entry ->
                val parts = entry.split(":")
                if (parts.isNotEmpty()) {
                    val habitName = parts[0]
                    val isDone = parts.getOrNull(1) == "1"
                    HabitItem(habitName, isDone)
                } else null
            }
        }
    }

    val completedCount = habits.count { it.isCompleted }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(13.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = displayTitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$completedCount/${habits.size} Done",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (completedCount == habits.size && habits.isNotEmpty()) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit habits",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        val displayHabits = when (size) {
            WidgetSize.COMPACT -> habits.take(2)
            WidgetSize.STANDARD -> habits.take(3)
            WidgetSize.EXPANDED -> habits
        }

        displayHabits.forEachIndexed { index, habit ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        val newHabits = habits.toMutableList()
                        val originalIndex = habits.indexOfFirst { it.name == habit.name }
                        if (originalIndex >= 0) {
                            newHabits[originalIndex] = habit.copy(isCompleted = !habit.isCompleted)
                            val serialized = newHabits.joinToString(",") { "${it.name}:${if (it.isCompleted) "1" else "0"}" }
                            onUpdateConfig(displayTitle, serialized)
                        }
                    }
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(
                            if (habit.isCompleted) Color(0xFF10B981)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (habit.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = habit.name,
                    fontSize = 12.sp,
                    color = if (habit.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (habit.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
            }
        }
    }

    if (showEditDialog) {
        var habit1 by remember { mutableStateOf(habits.getOrNull(0)?.name ?: "Drink 2L Water") }
        var habit2 by remember { mutableStateOf(habits.getOrNull(1)?.name ?: "Workout / Walk 30m") }
        var habit3 by remember { mutableStateOf(habits.getOrNull(2)?.name ?: "Read 15 mins") }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Customize Habits") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = habit1,
                        onValueChange = { habit1 = it },
                        label = { Text("Habit 1") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = habit2,
                        onValueChange = { habit2 = it },
                        label = { Text("Habit 2") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = habit3,
                        onValueChange = { habit3 = it },
                        label = { Text("Habit 3") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = listOf(
                            HabitItem(habit1.trim(), false),
                            HabitItem(habit2.trim(), false),
                            HabitItem(habit3.trim(), false)
                        )
                        val serialized = updated.joinToString(",") { "${it.name}:0" }
                        onUpdateConfig(displayTitle, serialized)
                        showEditDialog = false
                    }
                ) {
                    Text("Save & Reset Today")
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
