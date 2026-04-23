package com.yournotifications.di

import android.content.Context
import androidx.room.Room
import com.yournotifications.data.db.AppDatabase
import com.yournotifications.data.db.NotificationDao
import com.yournotifications.data.db.WebhookLogDao
import com.yournotifications.data.db.WebhookSettingsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "your_notifications.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideNotificationDao(db: AppDatabase): NotificationDao = db.notificationDao()

    @Provides
    fun provideAppDao(db: AppDatabase): com.yournotifications.data.db.AppDao = db.appDao()

    @Provides
    fun provideWebhookSettingsDao(db: AppDatabase): WebhookSettingsDao = db.webhookSettingsDao()

    @Provides
    fun provideWebhookLogDao(db: AppDatabase): WebhookLogDao = db.webhookLogDao()
}
