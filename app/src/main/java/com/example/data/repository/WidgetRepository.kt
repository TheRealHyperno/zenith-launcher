package com.example.data.repository

import com.example.data.local.LauncherDao
import com.example.data.local.QuickNoteEntity
import com.example.data.local.WidgetConfigEntity
import com.example.data.model.WidgetConfig
import com.example.data.model.WidgetType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WidgetRepository(private val dao: LauncherDao) {

    private val defaultWidgets = listOf(
        WidgetConfig(WidgetType.CLOCK, isEnabled = true, sortOrder = 0),
        WidgetConfig(WidgetType.GLANCE, isEnabled = true, sortOrder = 1),
        WidgetConfig(WidgetType.SYSTEM_STATS, isEnabled = true, sortOrder = 2),
        WidgetConfig(WidgetType.QUICK_ACTIONS, isEnabled = true, sortOrder = 3),
        WidgetConfig(WidgetType.QUICK_NOTE, isEnabled = false, sortOrder = 4)
    )

    fun observeWidgets(): Flow<List<WidgetConfig>> {
        return dao.getAllWidgets().map { entities ->
            if (entities.isEmpty()) {
                defaultWidgets
            } else {
                val dbMap = entities.associateBy { it.widgetId }
                WidgetType.entries.map { type ->
                    val entity = dbMap[type.id]
                    if (entity != null) {
                        WidgetConfig(
                            type = type,
                            isEnabled = entity.isEnabled,
                            sortOrder = entity.sortOrder,
                            styleType = entity.styleType
                        )
                    } else {
                        defaultWidgets.find { it.type == type }
                            ?: WidgetConfig(type, isEnabled = false, sortOrder = 99)
                    }
                }.sortedBy { it.sortOrder }
            }
        }
    }

    suspend fun toggleWidget(type: WidgetType, enabled: Boolean) {
        dao.saveWidgetConfig(
            WidgetConfigEntity(
                widgetId = type.id,
                isEnabled = enabled,
                sortOrder = defaultWidgets.indexOfFirst { it.type == type }.coerceAtLeast(0)
            )
        )
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
