package com.yournotifications.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yournotifications.data.model.BucketType

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val appName: String,
    val title: String?,
    val text: String?,
    val bucket: BucketType,
    val timestamp: Long,
    val isWebhookSent: Boolean = false
)

@Entity(tableName = "apps")
data class AppEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val isEnabled: Boolean = true,
    val isWebhookEnabled: Boolean = true
)

@Entity(tableName = "webhook_settings")
data class WebhookSettingsEntity(
    @PrimaryKey val id: Int = 0,
    val url: String,
    val isEnabled: Boolean = true,
    val targetBuckets: List<BucketType> = listOf(BucketType.PERSONAL, BucketType.OTP, BucketType.TRANSACTIONS, BucketType.ALERTS),
    val signingSecret: String = "",
    val customHeaders: String = "{}"
)

@Entity(tableName = "webhook_logs")
data class WebhookLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val requestBody: String,
    val httpStatusCode: Int?,
    val responseBody: String?,
    val isSuccess: Boolean,
    val errorMessage: String?,
    val sentAt: Long
)
