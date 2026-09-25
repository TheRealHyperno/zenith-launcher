package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AlphabetScrubber(
    availableLetters: List<Char>,
    selectedLetter: Char?,
    onLetterSelected: (Char) -> Unit,
    modifier: Modifier = Modifier
) {
    val letters = remember { ('A'..'Z').toList() }
    val view = LocalView.current
    var columnHeight by remember { mutableStateOf(1) }
    var lastVibratedChar by remember { mutableStateOf<Char?>(null) }
    var lastSelectedChar by remember { mutableStateOf<Char?>(null) }

    Box(
        modifier = modifier
            .width(26.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.35f))
            .padding(vertical = 6.dp)
            .onGloballyPositioned { coordinates ->
                columnHeight = coordinates.size.height.coerceAtLeast(1)
            }
            .pointerInput(letters) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        val itemHeight = (columnHeight.toFloat() / letters.size).coerceAtLeast(1f)
                        val index = (offset.y / itemHeight).toInt().coerceIn(0, letters.size - 1)
                        val char = letters[index]
                        if (char != lastSelectedChar) {
                            lastSelectedChar = char
                            onLetterSelected(char)
                        }
                        if (char != lastVibratedChar) {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            lastVibratedChar = char
                        }
                    },
                    onDragEnd = {
                        lastSelectedChar = null
                        lastVibratedChar = null
                    },
                    onDragCancel = {
                        lastSelectedChar = null
                        lastVibratedChar = null
                    },
                    onVerticalDrag = { change, _ ->
                        val itemHeight = (columnHeight.toFloat() / letters.size).coerceAtLeast(1f)
                        val index = (change.position.y / itemHeight).toInt().coerceIn(0, letters.size - 1)
                        val char = letters[index]
                        if (char != lastSelectedChar) {
                            lastSelectedChar = char
                            onLetterSelected(char)
                        }
                        if (char != lastVibratedChar) {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            lastVibratedChar = char
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.padding(vertical = 2.dp)
        ) {
            letters.forEach { letter ->
                val isAvailable = availableLetters.contains(letter)
                val isSelected = selectedLetter == letter
                Text(
                    text = letter.toString(),
                    fontSize = 9.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else if (isAvailable) FontWeight.Medium else FontWeight.Normal,
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.primary
                        isAvailable -> MaterialTheme.colorScheme.onSurface
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                    }
                )
            }
        }
    }
}
