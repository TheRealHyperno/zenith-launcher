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
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppItem
import com.example.data.model.BadgeStyle
import com.example.data.model.IconShape
import com.example.data.model.IconThemeMode
import com.example.data.model.WidgetSize
import com.example.ui.components.AppIconView

@Composable
fun AppClusterWidget(
    title: String,
    allApps: List<AppItem>,
    iconShape: IconShape,
    iconThemeMode: IconThemeMode,
    size: WidgetSize = WidgetSize.STANDARD,
    onAppClick: (AppItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayTitle = if (title.isBlank()) "Quick App Cluster" else title

    // Show 4 prominent apps
    val clusterApps = remember(allApps) {
        allApps.filter { !it.isHidden }.take(4)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.GridView,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = displayTitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            clusterApps.forEach { app ->
                AppIconView(
                    app = app,
                    iconShape = iconShape,
                    iconThemeMode = iconThemeMode,
                    showLabel = size != WidgetSize.COMPACT,
                    iconScale = if (size == WidgetSize.EXPANDED) 1.05f else 0.88f,
                    badgeStyle = BadgeStyle.DOT,
                    badgeCount = app.badgeCount,
                    modifier = Modifier.weight(1f),
                    onClick = { onAppClick(app) },
                    onLongClick = {}
                )
            }
        }
    }
}
