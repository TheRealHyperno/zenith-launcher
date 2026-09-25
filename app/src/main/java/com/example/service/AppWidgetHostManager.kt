package com.example.service

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class InstalledAppWidgetInfo(
    val providerInfo: AppWidgetProviderInfo,
    val appPackage: String,
    val appName: String,
    val widgetLabel: String,
    val previewDrawable: Drawable?,
    val appIcon: Drawable?,
    val minWidth: Int,
    val minHeight: Int
)

data class AppWithWidgets(
    val appPackage: String,
    val appName: String,
    val appIcon: Drawable?,
    val widgets: List<InstalledAppWidgetInfo>
)

object AppWidgetHostManager {
    const val APPWIDGET_HOST_ID = 2048

    @Volatile
    private var host: AppWidgetHost? = null

    @Volatile
    private var manager: AppWidgetManager? = null

    fun getHost(context: Context): AppWidgetHost {
        return host ?: synchronized(this) {
            host ?: AppWidgetHost(context.applicationContext, APPWIDGET_HOST_ID).also { host = it }
        }
    }

    fun getManager(context: Context): AppWidgetManager {
        return manager ?: synchronized(this) {
            manager ?: (context.applicationContext.getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager).also { manager = it }
        }
    }

    fun startListening(context: Context) {
        try {
            getHost(context).startListening()
        } catch (_: Exception) {}
    }

    fun stopListening(context: Context) {
        try {
            getHost(context).stopListening()
        } catch (_: Exception) {}
    }

    fun deleteWidgetId(context: Context, appWidgetId: Int) {
        try {
            getHost(context).deleteAppWidgetId(appWidgetId)
        } catch (_: Exception) {}
    }

    suspend fun getInstalledWidgetsGrouped(context: Context): List<AppWithWidgets> {
        return withContext(Dispatchers.IO) {
            try {
                val appWidgetManager = getManager(context)
                val pm = context.packageManager
                val providers = appWidgetManager.installedProviders ?: emptyList()

                val items = providers.mapNotNull { info ->
                    try {
                        val pkg = info.provider.packageName
                        val appInfo = pm.getApplicationInfo(pkg, 0)
                        val appName = pm.getApplicationLabel(appInfo).toString()
                        val widgetLabel = info.loadLabel(pm).takeIf { it.isNotBlank() } ?: appName
                        val appIcon = pm.getApplicationIcon(appInfo)
                        val preview = info.loadPreviewImage(context, 0) ?: appIcon

                        InstalledAppWidgetInfo(
                            providerInfo = info,
                            appPackage = pkg,
                            appName = appName,
                            widgetLabel = widgetLabel,
                            previewDrawable = preview,
                            appIcon = appIcon,
                            minWidth = info.minWidth,
                            minHeight = info.minHeight
                        )
                    } catch (_: Exception) {
                        null
                    }
                }

                items.groupBy { it.appPackage }
                    .map { (pkg, widgetList) ->
                        val first = widgetList.first()
                        AppWithWidgets(
                            appPackage = pkg,
                            appName = first.appName,
                            appIcon = first.appIcon,
                            widgets = widgetList.sortedBy { it.widgetLabel }
                        )
                    }
                    .sortedBy { it.appName.lowercase() }
            } catch (_: Exception) {
                emptyList()
            }
        }
    }
}
