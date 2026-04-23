package com.yournotifications.data.repository

import com.yournotifications.data.db.NotificationDao
import com.yournotifications.data.db.NotificationEntity
import com.yournotifications.data.db.WebhookLogDao
import com.yournotifications.data.db.WebhookLogEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao,
    private val appDao: com.yournotifications.data.db.AppDao,
    private val webhookLogDao: WebhookLogDao
) {
    fun getAllNotifications(): Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()

    fun getNotificationsByBuckets(buckets: List<com.yournotifications.data.model.BucketType>): Flow<List<NotificationEntity>> =
        notificationDao.getNotificationsByBuckets(buckets)

    fun getNotificationsByApp(packageName: String): Flow<List<NotificationEntity>> =
        notificationDao.getNotificationsByApp(packageName)

    suspend fun insertNotification(notification: NotificationEntity): Long {
        return notificationDao.insert(notification)
    }

    suspend fun updateWebhookStatus(id: Long, sent: Boolean) {
        notificationDao.updateWebhookStatus(id, sent)
    }

    suspend fun insertWebhookLog(log: WebhookLogEntity) {
        webhookLogDao.insertLog(log)
    }
    
    fun getRecentWebhookLogs(): Flow<List<WebhookLogEntity>> = webhookLogDao.getRecentLogs()

    suspend fun ensureAppTracked(packageName: String, appName: String) {
        appDao.insertApp(com.yournotifications.data.db.AppEntity(packageName, appName))
    }

    fun getAllApps(): Flow<List<com.yournotifications.data.db.AppEntity>> = appDao.getAllApps()

    suspend fun updateAppStatus(packageName: String, enabled: Boolean) {
        appDao.updateAppStatus(packageName, enabled)
    }

    suspend fun updateAppWebhookStatus(packageName: String, enabled: Boolean) {
        appDao.updateAppWebhookStatus(packageName, enabled)
    }

    suspend fun getApp(packageName: String) = appDao.getApp(packageName)

    suspend fun deleteNotification(id: Long) = notificationDao.deleteNotification(id)
}
