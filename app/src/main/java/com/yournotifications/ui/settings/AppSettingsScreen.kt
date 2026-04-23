package com.yournotifications.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yournotifications.data.db.AppEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(
    apps: List<AppEntity>,
    onToggleApp: (String, Boolean) -> Unit,
    onToggleWebhook: (String, Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (apps.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No apps tracked yet", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(apps) { app ->
                    AppSettingItem(app, onToggleApp, onToggleWebhook)
                }
            }
        }
    }
}

@Composable
fun AppSettingItem(
    app: AppEntity,
    onToggleApp: (String, Boolean) -> Unit,
    onToggleWebhook: (String, Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(app.appName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(app.packageName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show in App", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = app.isEnabled,
                    onCheckedChange = { onToggleApp(app.packageName, it) }
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Forward via Webhook", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = app.isWebhookEnabled,
                    onCheckedChange = { onToggleWebhook(app.packageName, it) }
                )
            }
        }
    }
}
