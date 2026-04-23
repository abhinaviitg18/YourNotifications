package com.yournotifications.ui.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yournotifications.data.db.NotificationEntity
import com.yournotifications.data.model.BucketType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationListScreen(
    groupedNotifications: Map<String, List<NotificationEntity>>,
    selectedBucket: BucketType?,
    onBucketSelected: (BucketType?) -> Unit,
    onSettingsClick: () -> Unit,
    onAppManagementClick: () -> Unit,
    onDeleteNotification: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("YourNotifications", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onAppManagementClick) {
                        Icon(Icons.Default.List, contentDescription = "Manage Apps")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            BucketTabs(
                selectedBucket = selectedBucket,
                onBucketSelected = onBucketSelected
            )
            
            if (groupedNotifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No notifications in this bucket", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    groupedNotifications.forEach { (appName, notifications) ->
                        item(key = appName) {
                            AppGroupHeader(appName, notifications.size)
                        }
                        items(notifications, key = { it.id }) { notification ->
                            NotificationItem(notification, onDelete = { onDeleteNotification(notification.id) })
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 16.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppGroupHeader(appName: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = appName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
            Text(text = count.toString(), modifier = Modifier.padding(horizontal = 4.dp))
        }
    }
}

@Composable
fun NotificationItem(notification: NotificationEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTime(notification.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                if (notification.isWebhookSent) {
                    Text(
                        "✓ Forwarded",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.title ?: "No Title",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = notification.text ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 5,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BucketTabs(
    selectedBucket: BucketType?,
    onBucketSelected: (BucketType?) -> Unit
) {
    val buckets = listOf(null) + BucketType.values().toList()
    
    ScrollableTabRow(
        selectedTabIndex = buckets.indexOf(selectedBucket).coerceAtLeast(0),
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = {}
    ) {
        buckets.forEach { bucket ->
            Tab(
                selected = selectedBucket == bucket,
                onClick = { onBucketSelected(bucket) },
                text = {
                    Text(
                        text = bucket?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "All",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )
        }
    }
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
