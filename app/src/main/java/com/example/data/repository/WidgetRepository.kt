package com.example.data.repository

import com.example.data.local.LauncherDao
import com.example.data.local.QuickNoteEntity
import com.example.data.local.WidgetInstanceEntity
import com.example.data.model.WidgetConfig
import com.example.data.model.WidgetSize
import com.example.data.model.WidgetType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class WidgetRepository(private val dao: LauncherDao) {

    private val defaultWidgetInstances = listOf(
        WidgetConfig(
            id = "default_clock",
            type = WidgetType.CLOCK,
            isEnabled = true,
            sortOrder = 0,
            size = WidgetSize.STANDARD
        ),
        WidgetConfig(
            id = "default_glance",
            type = WidgetType.GLANCE,
            isEnabled = true,
            sortOrder = 1,
            size = WidgetSize.STANDARD
        ),
        WidgetConfig(
            id = "default_music",
            type = WidgetType.MUSIC_PLAYER,
            isEnabled = true,
            sortOrder = 2,
            size = WidgetSize.STANDARD
        ),
        WidgetConfig(
            id = "default_system_stats",
            type = WidgetType.SYSTEM_STATS,
            isEnabled = true,
            sortOrder = 3,
            size = WidgetSize.STANDARD
        ),
        WidgetConfig(
            id = "default_quick_actions",
            type = WidgetType.QUICK_ACTIONS,
            isEnabled = true,
            sortOrder = 4,
            size = WidgetSize.STANDARD
        ),
        WidgetConfig(
            id = "default_quick_note",
            type = WidgetType.QUICK_NOTE,
            isEnabled = false,
            sortOrder = 5,
            size = WidgetSize.STANDARD
        )
    )

    fun observeWidgetInstances(): Flow<List<WidgetConfig>> {
        return dao.getAllWidgetInstances().map { entities ->
            if (entities.isEmpty()) {
                // Seed default widgets if empty
                val seeded = defaultWidgetInstances.map {
                    WidgetInstanceEntity(
                        id = it.id,
                        type = it.type.id,
                        isEnabled = it.isEnabled,
                        sortOrder = it.sortOrder,
                        size = it.size.name,
                        title = it.title,
                        customData = it.customData
                    )
                }
                dao.saveWidgetInstances(seeded)
                defaultWidgetInstances
            } else {
                entities.mapNotNull { entity ->
                    val type = try { WidgetType.valueOf(entity.type) } catch (_: Exception) { null }
                    if (type != null) {
                        val size = try { WidgetSize.valueOf(entity.size) } catch (_: Exception) { WidgetSize.STANDARD }
                        WidgetConfig(
                            id = entity.id,
                            type = type,
                            isEnabled = entity.isEnabled,
                            sortOrder = entity.sortOrder,
                            size = size,
                            title = entity.title,
                            customData = entity.customData
                        )
                    } else null
                }.sortedBy { it.sortOrder }
            }
        }
    }

    suspend fun addWidget(type: WidgetType, currentCount: Int, customData: String = "", title: String = ""): String {
        val newId = "widget_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
        val entity = WidgetInstanceEntity(
            id = newId,
            type = type.id,
            isEnabled = true,
            sortOrder = currentCount,
            size = type.defaultSize.name,
            title = if (title.isBlank()) type.displayName else title,
            customData = customData
        )
        dao.saveWidgetInstance(entity)
        return newId
    }

    suspend fun addAndroidAppWidget(
        appWidgetId: Int,
        providerComponent: String,
        title: String,
        customData: String,
        currentCount: Int
    ): String {
        val newId = "android_widget_${appWidgetId}"
        val entity = WidgetInstanceEntity(
            id = newId,
            type = WidgetType.ANDROID_APPWIDGET.id,
            isEnabled = true,
            sortOrder = currentCount,
            size = WidgetSize.STANDARD.name,
            title = title,
            customData = customData
        )
        dao.saveWidgetInstance(entity)
        return newId
    }

    suspend fun updateWidgetSize(id: String, newSize: WidgetSize) {
        dao.updateWidgetSize(id, newSize.name)
    }

    suspend fun removeWidget(id: String) {
        dao.deleteWidgetInstance(id)
    }

    suspend fun moveWidget(id: String, direction: Int, allWidgets: List<WidgetConfig>) {
        val currentIndex = allWidgets.indexOfFirst { it.id == id }
        if (currentIndex == -1) return
        val targetIndex = (currentIndex + direction).coerceIn(0, allWidgets.size - 1)
        if (targetIndex == currentIndex) return

        val mutable = allWidgets.toMutableList()
        val item = mutable.removeAt(currentIndex)
        mutable.add(targetIndex, item)

        val updated = mutable.mapIndexed { index, widget ->
            WidgetInstanceEntity(
                id = widget.id,
                type = widget.type.id,
                isEnabled = widget.isEnabled,
                sortOrder = index,
                size = widget.size.name,
                title = widget.title,
                customData = widget.customData
            )
        }
        dao.saveWidgetInstances(updated)
    }

    suspend fun updateWidgetCustomData(id: String, title: String, customData: String) {
        dao.updateWidgetCustomData(id, title, customData)
    }

    suspend fun toggleWidget(id: String, isEnabled: Boolean) {
        // Toggle specific instance
        val current = dao.getAllWidgetInstances()
        // If it's a default type toggle from settings, handle accordingly
    }

    fun observeQuickNote(): Flow<String> {
        return dao.getQuickNote().map { it?.content ?: "" }
    }

    suspend fun saveQuickNote(text: String) {
        dao.saveQuickNote(
            QuickNoteEntity(
                id = 1,
                content = text,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}
