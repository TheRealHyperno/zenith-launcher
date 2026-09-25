package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LauncherDao {
    @Query("SELECT * FROM app_configs")
    fun getAllAppConfigs(): Flow<List<AppConfigEntity>>

    @Query("SELECT * FROM app_configs WHERE packageName = :packageName LIMIT 1")
    suspend fun getAppConfig(packageName: String): AppConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAppConfig(config: AppConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAppConfigs(configs: List<AppConfigEntity>)

    @Query("UPDATE app_configs SET isPinnedHome = :pinned WHERE packageName = :packageName")
    suspend fun setPinnedHome(packageName: String, pinned: Boolean)

    @Query("UPDATE app_configs SET isPinnedDock = :pinned, dockIndex = :dockIndex WHERE packageName = :packageName")
    suspend fun setPinnedDock(packageName: String, pinned: Boolean, dockIndex: Int)

    @Query("UPDATE app_configs SET isHidden = :hidden WHERE packageName = :packageName")
    suspend fun setHidden(packageName: String, hidden: Boolean)

    @Query("UPDATE app_configs SET launchCount = launchCount + 1, lastLaunchTime = :timestamp WHERE packageName = :packageName")
    suspend fun recordAppLaunch(packageName: String, timestamp: Long = System.currentTimeMillis())

    // Gestures
    @Query("SELECT * FROM gesture_configs")
    fun getAllGestures(): Flow<List<GestureConfigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGesture(gesture: GestureConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGestures(gestures: List<GestureConfigEntity>)

    // Widget Configs (Legacy)
    @Query("SELECT * FROM widget_configs ORDER BY sortOrder ASC")
    fun getAllWidgets(): Flow<List<WidgetConfigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWidgetConfig(widget: WidgetConfigEntity)

    // Widget Instances (Modern customizable & resizable widgets)
    @Query("SELECT * FROM widget_instances ORDER BY sortOrder ASC")
    fun getAllWidgetInstances(): Flow<List<WidgetInstanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWidgetInstance(instance: WidgetInstanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWidgetInstances(instances: List<WidgetInstanceEntity>)

    @Query("DELETE FROM widget_instances WHERE id = :id")
    suspend fun deleteWidgetInstance(id: String)

    @Query("UPDATE widget_instances SET size = :size WHERE id = :id")
    suspend fun updateWidgetSize(id: String, size: String)

    @Query("UPDATE widget_instances SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateWidgetOrder(id: String, sortOrder: Int)

    @Query("UPDATE widget_instances SET customData = :customData, title = :title WHERE id = :id")
    suspend fun updateWidgetCustomData(id: String, title: String, customData: String)

    // Quick Note
    @Query("SELECT * FROM quick_notes WHERE id = 1 LIMIT 1")
    fun getQuickNote(): Flow<QuickNoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQuickNote(note: QuickNoteEntity)
}
