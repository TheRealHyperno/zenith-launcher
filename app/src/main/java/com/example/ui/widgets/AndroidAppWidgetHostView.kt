package com.example.ui.widgets

import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.os.Bundle
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.WidgetSize
import com.example.service.AppWidgetHostManager

@Composable
fun AndroidAppWidgetHostView(
    appWidgetId: Int,
    componentNameString: String,
    size: WidgetSize = WidgetSize.STANDARD,
    isPerformanceMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val host = remember { AppWidgetHostManager.getHost(context) }
    val manager = remember { AppWidgetHostManager.getManager(context) }

    var widgetInfo by remember(appWidgetId) {
        mutableStateOf(manager.getAppWidgetInfo(appWidgetId))
    }

    val targetHeight = when (size) {
        WidgetSize.COMPACT -> 120.dp
        WidgetSize.STANDARD -> 185.dp
        WidgetSize.EXPANDED -> 280.dp
    }

    val widgetHeight = if (isPerformanceMode) {
        targetHeight
    } else {
        val animatedHeight by animateDpAsState(
            targetValue = targetHeight,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "app_widget_height_$appWidgetId"
        )
        animatedHeight
    }

    LaunchedEffect(size, appWidgetId) {
        try {
            val heightDpInt = when (size) {
                WidgetSize.COMPACT -> 120
                WidgetSize.STANDARD -> 185
                WidgetSize.EXPANDED -> 280
            }
            val options = Bundle().apply {
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, heightDpInt)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, heightDpInt)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 200)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 400)
            }
            manager.updateAppWidgetOptions(appWidgetId, options)
        } catch (_: Exception) {}
    }

    val info = widgetInfo
    if (info != null) {
        var hasError by remember { mutableStateOf(false) }

        if (!hasError) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(widgetHeight)
            ) {
                AndroidView(
                    factory = { ctx ->
                        try {
                            val view = host.createView(ctx, appWidgetId, info)
                            view.setAppWidget(appWidgetId, info)
                            view
                        } catch (t: Throwable) {
                            hasError = true
                            AppWidgetHostView(ctx)
                        }
                    },
                    update = { view ->
                        try {
                            view.setAppWidget(appWidgetId, info)
                        } catch (_: Exception) {}
                    },
                    modifier = Modifier.fillMaxWidth().height(widgetHeight)
                )
            }
        } else {
            WidgetErrorFallback(
                message = "Error rendering widget (${info.loadLabel(context.packageManager)})",
                height = widgetHeight
            )
        }
    } else {
        // Provider info was not found (app uninstalled or ID revoked)
        val appName = remember(componentNameString) {
            try {
                val cn = ComponentName.unflattenFromString(componentNameString)
                cn?.packageName ?: "App"
            } catch (_: Exception) {
                "App"
            }
        }
        WidgetErrorFallback(
            message = "Widget from $appName is no longer available",
            height = widgetHeight
        )
    }
}

@Composable
fun WidgetErrorFallback(
    message: String,
    height: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Widgets,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}
