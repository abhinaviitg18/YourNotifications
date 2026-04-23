package com.yournotifications.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity): Long

    @Query("""
        SELECT n.* FROM notifications n
        INNER JOIN apps a ON n.packageName = a.packageName
        WHERE a.isEnabled = 1
        ORDER BY n.timestamp DESC
    """)
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("""
        SELECT n.* FROM notifications n
        INNER JOIN apps a ON n.packageName = a.packageName
        WHERE n.bucket IN (:buckets) AND a.isEnabled = 1
        ORDER BY n.timestamp DESC
    """)
    fun getNotificationsByBuckets(buckets: List<com.yournotifications.data.model.BucketType>): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE packageName = :packageName ORDER BY timestamp DESC")
    fun getNotificationsByApp(packageName: String): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET isWebhookSent = :sent WHERE id = :id")
    suspend fun updateWebhookStatus(id: Long, sent: Boolean)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)
}

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertApp(app: AppEntity)

    @Query("UPDATE apps SET isEnabled = :enabled WHERE packageName = :packageName")
    suspend fun updateAppStatus(packageName: String, enabled: Boolean)

    @Query("UPDATE apps SET isWebhookEnabled = :enabled WHERE packageName = :packageName")
    suspend fun updateAppWebhookStatus(packageName: String, enabled: Boolean)

    @Query("SELECT * FROM apps ORDER BY appName ASC")
    fun getAllApps(): Flow<List<AppEntity>>

    @Query("SELECT * FROM apps WHERE packageName = :packageName")
    suspend fun getApp(packageName: String): AppEntity?
}

@Dao
interface WebhookSettingsDao {
    @Query("SELECT * FROM webhook_settings LIMIT 1")
    suspend fun getSettings(): WebhookSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: WebhookSettingsEntity)
}

@Dao
interface WebhookLogDao {
    @Insert
    suspend fun insertLog(log: WebhookLogEntity)

    @Query("SELECT * FROM webhook_logs ORDER BY sentAt DESC LIMIT 100")
    fun getRecentLogs(): Flow<List<WebhookLogEntity>>
}
