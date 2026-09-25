package com.example.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.example.data.model.GestureTrigger

@Composable
fun Modifier.launcherDesktopGestures(
    onTriggerGesture: (GestureTrigger) -> Unit
): Modifier {
    var accumulatedDragY by remember { mutableFloatStateOf(0f) }
    var dragTriggered by remember { mutableStateOf(false) }

    return this
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = {
                    onTriggerGesture(GestureTrigger.DOUBLE_TAP)
                },
                onLongPress = {
                    onTriggerGesture(GestureTrigger.LONG_PRESS_DESKTOP)
                }
            )
        }
        .pointerInput(Unit) {
            detectTransformGestures { _, _, zoom, _ ->
                if (zoom < 0.85f) {
                    onTriggerGesture(GestureTrigger.PINCH_IN)
                }
            }
        }
        .pointerInput(Unit) {
            detectVerticalDragGestures(
                onDragStart = {
                    accumulatedDragY = 0f
                    dragTriggered = false
                },
                onDragEnd = {
                    accumulatedDragY = 0f
                    dragTriggered = false
                },
                onDragCancel = {
                    accumulatedDragY = 0f
                    dragTriggered = false
                },
                onVerticalDrag = { change, dragAmount ->
                    accumulatedDragY += dragAmount
                    if (!dragTriggered) {
                        if (accumulatedDragY < -120f) {
                            dragTriggered = true
                            onTriggerGesture(GestureTrigger.SWIPE_UP)
                        } else if (accumulatedDragY > 120f) {
                            dragTriggered = true
                            onTriggerGesture(GestureTrigger.SWIPE_DOWN)
                        }
                    }
                }
            )
        }
}
