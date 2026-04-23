package com.yournotifications.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yournotifications.data.db.NotificationEntity
import com.yournotifications.data.db.WebhookLogEntity
import com.yournotifications.data.db.WebhookSettingsEntity
import com.yournotifications.data.repository.NotificationRepository
import com.yournotifications.data.db.WebhookSettingsDao
import com.yournotifications.data.model.BucketType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository,
    private val settingsDao: WebhookSettingsDao
) : ViewModel() {

    private val _selectedBucket = MutableStateFlow<BucketType?>(null)
    val selectedBucket: StateFlow<BucketType?> = _selectedBucket

    // Grouping notifications by app name for each bucket
    val groupedNotifications: StateFlow<Map<String, List<NotificationEntity>>> = _selectedBucket
        .flatMapLatest { bucket ->
            if (bucket == null) {
                repository.getAllNotifications()
            } else {
                repository.getNotificationsByBuckets(listOf(bucket))
            }
        }
        .map { list ->
            list.groupBy { it.appName }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val trackedApps: StateFlow<List<com.yournotifications.data.db.AppEntity>> = repository.getAllApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val webhookLogs: StateFlow<List<WebhookLogEntity>> = repository.getRecentWebhookLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectBucket(bucket: BucketType?) {
        _selectedBucket.value = bucket
    }

    fun toggleAppStatus(packageName: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.updateAppStatus(packageName, enabled)
        }
    }

    fun toggleAppWebhookStatus(packageName: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.updateAppWebhookStatus(packageName, enabled)
        }
    }

    suspend fun getWebhookSettings(): WebhookSettingsEntity? {
        return settingsDao.getSettings()
    }

    fun saveWebhookSettings(url: String, isEnabled: Boolean, secret: String, targetBuckets: List<BucketType>) {
        viewModelScope.launch {
            val currentSettings = settingsDao.getSettings()
            settingsDao.saveSettings(
                WebhookSettingsEntity(
                    id = currentSettings?.id ?: 0,
                    url = url,
                    isEnabled = isEnabled,
                    signingSecret = secret,
                    targetBuckets = targetBuckets
                )
            )
        }
    }

    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }
}
