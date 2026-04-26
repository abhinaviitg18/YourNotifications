package com.yournotifications.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.yournotifications.data.db.NotificationEntity
import com.yournotifications.data.repository.NotificationRepository
import com.yournotifications.domain.classifier.NotificationClassifier
import com.yournotifications.messaging.WebhookSender
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationListener : NotificationListenerService() {

    @Inject
    lateinit var repository: NotificationRepository

    @Inject
    lateinit var webhookSender: WebhookSender

    @Inject
    lateinit var classifier: NotificationClassifier

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val title = extras.getString("android.title") ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val packageName = sbn.packageName
        val timestamp = sbn.postTime

        val pm = packageManager
        val appName = try {
            pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
        } catch (e: Exception) {
            packageName
        }

        val bucket = classifier.classify(packageName, title, text)

        scope.launch {
            // Ensure app is tracked in the database
            repository.ensureAppTracked(packageName, appName)
            
            // Check if app is enabled (not blocked)
            val app = repository.getApp(packageName)
            if (app?.isEnabled == false) {
                Log.d("NotificationListener", "Skipping notification from blocked app: $packageName")
                return@launch
            }

            // Duplicate suppression: Compare with last notification for this app
            val lastNotification = repository.getLatestNotificationForApp(packageName)
            if (lastNotification != null && 
                lastNotification.title == title && 
                lastNotification.text == text) {
                Log.d("NotificationListener", "Skipping duplicate notification from $packageName")
                return@launch
            }

            val entity = NotificationEntity(
                packageName = packageName,
                appName = appName,
                title = title,
                text = text,
                bucket = bucket,
                timestamp = timestamp
            )
            val id = repository.insertNotification(entity)
            
            // Trigger webhook
            webhookSender.send(id, packageName, appName, title, text, bucket, timestamp)
        }
    }

    override fun onListenerConnected() {
        Log.d("NotificationListener", "Listener connected")
        // Initial sync: fetch active notifications
        scope.launch {
            activeNotifications.forEach { sbn ->
                onNotificationPosted(sbn)
            }
        }
    }
}
