package com.yournotifications.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [NotificationEntity::class, AppEntity::class, WebhookSettingsEntity::class, WebhookLogEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
    abstract fun appDao(): AppDao
    abstract fun webhookSettingsDao(): WebhookSettingsDao
    abstract fun webhookLogDao(): WebhookLogDao
}
