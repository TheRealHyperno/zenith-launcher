package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppItem
import com.example.data.model.BadgeStyle
import com.example.data.model.IconShape
import com.example.data.model.IconThemeMode
import com.example.data.repository.IconCacheManager
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIconView(
    app: AppItem,
    iconShape: IconShape,
    iconThemeMode: IconThemeMode,
    showLabel: Boolean,
    iconScale: Float,
    badgeStyle: BadgeStyle,
    badgeCount: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val context = LocalContext.current
    var iconBitmap by remember(app.packageName) {
        mutableStateOf(IconCacheManager.getFromCache(app.packageName))
    }

    if (iconBitmap == null) {
        LaunchedEffect(app.packageName) {
            val loaded = IconCacheManager.getAppIcon(context, app.packageName)
            if (loaded != null) {
                iconBitmap = loaded
            }
        }
    }

    val shape = remember(iconShape) { getShapeForIcon(iconShape) }
    val baseSize = 56.dp * iconScale

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .testTag("app_item_${app.packageName}")
            .semantics { contentDescription = app.displayName }
            .padding(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(baseSize)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(baseSize)
                    .clip(shape)
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick
                    )
            ) {
                if (iconBitmap != null) {
                    val colorFilter = when (iconThemeMode) {
                        IconThemeMode.SYSTEM -> null
                        IconThemeMode.MONOCHROME -> ColorFilter.tint(Color(0xFFE2E8F0))
                        IconThemeMode.NEON -> ColorFilter.tint(Color(0xFF00E5FF))
                        IconThemeMode.PASTEL -> ColorFilter.tint(Color(0xFFBAE6FD))
                        IconThemeMode.EMERALD -> ColorFilter.tint(Color(0xFF34D399))
                    }
                    val bgTint = when (iconThemeMode) {
                        IconThemeMode.SYSTEM -> Color.Transparent
                        IconThemeMode.MONOCHROME -> Color(0xFF1E293B)
                        IconThemeMode.NEON -> Color(0xFF0F172A)
                        IconThemeMode.PASTEL -> Color(0xFF1E1E24)
                        IconThemeMode.EMERALD -> Color(0xFF064E3B)
                    }

                    Box(
                        modifier = Modifier
                            .size(baseSize)
                            .background(bgTint),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = iconBitmap!!,
                            contentDescription = null,
                            colorFilter = colorFilter,
                            modifier = Modifier
                                .size(baseSize * if (iconThemeMode == IconThemeMode.SYSTEM) 1.0f else 0.75f)
                                .clip(if (iconThemeMode == IconThemeMode.SYSTEM) shape else CircleShape)
                        )
                    }
                } else {
                    // Fallback initials monogram
                    val initials = app.displayName.take(2).uppercase()
                    val hashColor = remember(app.packageName) {
                        val colors = listOf(
                            Color(0xFF3B82F6),
                            Color(0xFF8B5CF6),
                            Color(0xFFEC4899),
                            Color(0xFF10B981),
                            Color(0xFFF59E0B),
                            Color(0xFF06B6D4)
                        )
                        colors[app.packageName.hashCode().absoluteValue % colors.size]
                    }
                    Box(
                        modifier = Modifier
                            .size(baseSize)
                            .background(hashColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp * iconScale
                        )
                    }
                }
            }

            // Notification Badge
            if (badgeStyle != BadgeStyle.NONE && badgeCount > 0) {
                when (badgeStyle) {
                    BadgeStyle.DOT -> {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .size(10.dp)
                                .background(Color(0xFFEF4444), CircleShape)
                        )
                    }
                    BadgeStyle.NUMBER -> {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
                                .background(Color(0xFFEF4444), CircleShape)
                                .padding(horizontal = 5.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    else -> {}
                }
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.displayName,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }
    }
}
