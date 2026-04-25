package com.yournotifications.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yournotifications.data.model.BucketType
import com.yournotifications.ui.notifications.NotificationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebhookSettingsScreen(
    viewModel: NotificationViewModel,
    onBack: () -> Unit,
    onHistoryClick: () -> Unit
) {
    var url by remember { mutableStateOf("") }
    var secret by remember { mutableStateOf("") }
    var isEnabled by remember { mutableStateOf(true) }
    var targetBuckets by remember { mutableStateOf(setOf<BucketType>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val settings = viewModel.getWebhookSettings()
        if (settings != null) {
            url = settings.url
            secret = settings.signingSecret
            isEnabled = settings.isEnabled
            targetBuckets = settings.targetBuckets.toSet()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Webhook Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onHistoryClick) {
                        Text("History")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Webhook URL") },
                placeholder = { Text("https://your-api.com/webhook") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = secret,
                onValueChange = { secret = it },
                label = { Text("Signing Secret (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = isEnabled, onCheckedChange = { isEnabled = it })
                Spacer(modifier = Modifier.width(12.dp))
                Text("Enable Webhook Forwarding", style = MaterialTheme.typography.bodyLarge)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Target Buckets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Select which notification types to forward", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(8.dp))
            
            BucketType.values().forEach { bucket ->
                val isChecked = targetBuckets.contains(bucket)
                Surface(
                    onClick = {
                        targetBuckets = if (isChecked) {
                            targetBuckets - bucket
                        } else {
                            targetBuckets + bucket
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = null // Handled by Surface click
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(bucket.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    viewModel.saveWebhookSettings(url, isEnabled, secret, targetBuckets.toList())
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Save Configuration")
            }
        }
    }
}
