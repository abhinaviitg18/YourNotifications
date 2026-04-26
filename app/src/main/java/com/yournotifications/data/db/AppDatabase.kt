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

    companion object {
        val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `webhook_settings` (`id` INTEGER NOT NULL, `url` TEXT NOT NULL, `isEnabled` INTEGER NOT NULL, `targetBuckets` TEXT NOT NULL, `signingSecret` TEXT NOT NULL, `customHeaders` TEXT NOT NULL, PRIMARY KEY(`id`))"
                )
            }
        }

        val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `webhook_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `url` TEXT NOT NULL, `requestBody` TEXT NOT NULL, `httpStatusCode` INTEGER, `responseBody` TEXT, `isSuccess` INTEGER NOT NULL, `errorMessage` TEXT, `sentAt` INTEGER NOT NULL)"
                )
            }
        }
    }
}
