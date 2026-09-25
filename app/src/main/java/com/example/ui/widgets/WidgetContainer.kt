package com.example.ui.widgets

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WidgetConfig
import com.example.data.model.WidgetSize

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun WidgetContainer(
    widget: WidgetConfig,
    isEditMode: Boolean,
    cornerRadiusDp: Int,
    opacity: Float,
    isPerformanceMode: Boolean = false,
    onResize: (WidgetSize) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit,
    onToggleEditMode: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (WidgetSize) -> Unit
) {
    val view = LocalView.current
    val shape = RoundedCornerShape(cornerRadiusDp.dp)

    val surfaceModifier = if (isPerformanceMode) {
        modifier
            .fillMaxWidth()
            .clip(shape)
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    onToggleEditMode()
                }
            )
    } else {
        modifier
            .fillMaxWidth()
            .clip(shape)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    onToggleEditMode()
                }
            )
    }

    Surface(
        shape = shape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = opacity),
        border = if (isEditMode) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
        shadowElevation = if (isEditMode) 6.dp else 2.dp,
        modifier = surfaceModifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Edit Mode Controls Toolbar
            AnimatedVisibility(visible = isEditMode) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    // Size Switcher Chips [S] [M] [L]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Size:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(end = 2.dp)
                        )
                        WidgetSize.entries.forEach { sizeOption ->
                            val isSelected = widget.size == sizeOption
                            val primaryColor = MaterialTheme.colorScheme.primary
                            val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
                            val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

                            val chipBgColor by animateColorAsState(
                                targetValue = if (isSelected) primaryColor else surfaceVariantColor,
                                animationSpec = if (isPerformanceMode) androidx.compose.animation.core.snap() else spring(stiffness = Spring.StiffnessMediumLow),
                                label = "chip_bg_${sizeOption.name}"
                            )

                            val chipTextColor by animateColorAsState(
                                targetValue = if (isSelected) Color.Black else onSurfaceVariantColor,
                                animationSpec = if (isPerformanceMode) androidx.compose.animation.core.snap() else spring(stiffness = Spring.StiffnessMediumLow),
                                label = "chip_text_${sizeOption.name}"
                            )

                            val chipScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.06f else 1.0f,
                                animationSpec = if (isPerformanceMode) androidx.compose.animation.core.snap() else spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                ),
                                label = "chip_scale_${sizeOption.name}"
                            )

                            Box(
                                modifier = Modifier
                                    .graphicsLayer {
                                        scaleX = chipScale
                                        scaleY = chipScale
                                    }
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(chipBgColor)
                                    .combinedClickable(
                                        onClick = {
                                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                            onResize(sizeOption)
                                        }
                                    )
                                    .padding(horizontal = 9.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sizeOption.shortLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = chipTextColor
                                )
                            }
                        }
                    }

                    // Move Up, Move Down & Delete
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                onMoveUp()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Move Up",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                onMoveDown()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Move Down",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                onDelete()
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444).copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete Widget",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Widget Content - Animated or Static based on Performance Mode
            if (isPerformanceMode) {
                content(widget.size)
            } else {
                AnimatedContent(
                    targetState = widget.size,
                    transitionSpec = {
                        val isExpanding = targetState.ordinal > initialState.ordinal
                        (fadeIn(animationSpec = tween(170, delayMillis = 30)) +
                            slideInVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                ),
                                initialOffsetY = { h -> if (isExpanding) h / 4 else -h / 4 }
                            ) +
                            scaleIn(
                                initialScale = 0.96f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            )
                        ).togetherWith(
                            fadeOut(animationSpec = tween(110)) +
                            slideOutVertically(
                                animationSpec = tween(110),
                                targetOffsetY = { h -> if (isExpanding) -h / 6 else h / 6 }
                            )
                        ).using(
                            SizeTransform(clip = false)
                        )
                    },
                    label = "widget_size_morph_${widget.id}"
                ) { currentSize ->
                    content(currentSize)
                }
            }
        }
    }
}
