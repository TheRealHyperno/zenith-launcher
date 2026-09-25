package com.example.data.repository

import com.example.data.local.GestureConfigEntity
import com.example.data.local.LauncherDao
import com.example.data.model.GestureAction
import com.example.data.model.GestureBinding
import com.example.data.model.GestureTrigger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GestureRepository(private val dao: LauncherDao) {

    private val defaultBindings = mapOf(
        GestureTrigger.SWIPE_UP to GestureAction.OPEN_DRAWER,
        GestureTrigger.SWIPE_DOWN to GestureAction.OPEN_SEARCH,
        GestureTrigger.DOUBLE_TAP to GestureAction.OPEN_SETTINGS,
        GestureTrigger.PINCH_IN to GestureAction.OPEN_SETTINGS,
        GestureTrigger.TWO_FINGER_SWIPE_UP to GestureAction.OPEN_HIDDEN_APPS,
        GestureTrigger.LONG_PRESS_DESKTOP to GestureAction.OPEN_SETTINGS
    )

    fun observeGestures(): Flow<Map<GestureTrigger, GestureBinding>> {
        return dao.getAllGestures().map { entities ->
            val map = mutableMapOf<GestureTrigger, GestureBinding>()
            // Populate defaults first
            defaultBindings.forEach { (trigger, action) ->
                map[trigger] = GestureBinding(trigger, action)
            }
            // Override from DB
            entities.forEach { entity ->
                try {
                    val trigger = GestureTrigger.valueOf(entity.gestureTrigger)
                    val action = GestureAction.valueOf(entity.actionType)
                    map[trigger] = GestureBinding(trigger, action, entity.targetPackage)
                } catch (_: Exception) {}
            }
            map
        }
    }

    suspend fun updateGesture(trigger: GestureTrigger, action: GestureAction, targetPackage: String? = null) {
        dao.saveGesture(
            GestureConfigEntity(
                gestureTrigger = trigger.name,
                actionType = action.name,
                targetPackage = targetPackage
            )
        )
    }

    suspend fun resetToDefaults() {
        val list = defaultBindings.map { (trigger, action) ->
            GestureConfigEntity(
                gestureTrigger = trigger.name,
                actionType = action.name
            )
        }
        dao.saveGestures(list)
    }
}
