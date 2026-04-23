package com.yournotifications.ui

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
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.yournotifications.data.db.AppEntity
import com.yournotifications.data.model.BucketType
import com.yournotifications.ui.settings.AppSettingsScreen
import com.yournotifications.ui.theme.YourNotificationsTheme
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockWebhookSettingsScreen() {
    val url = "https://example.com/webhook"
    val secret = "sk_test_example_secret"
    val isEnabled = true
    val targetBuckets = setOf(BucketType.PERSONAL, BucketType.OTP, BucketType.TRANSACTIONS)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Webhook Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
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
            OutlinedTextField(value = url, onValueChange = { }, label = { Text("Webhook URL") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = secret, onValueChange = { }, label = { Text("Signing Secret (Optional)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = isEnabled, onCheckedChange = { })
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
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isChecked, onCheckedChange = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(bucket.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
                Text("Save Configuration")
            }
        }
    }
}

val mockApps = listOf(
    AppEntity("com.whatsapp", "WhatsApp", true, true),
    AppEntity("com.google.android.gm", "Gmail", true, false),
    AppEntity("com.slack", "Slack", true, true),
    AppEntity("com.google.android.apps.messaging", "Messages", true, true)
)

class PhoneScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.NEXUS_5.copy(softButtons = false), useDeviceResolution = true)

    @Test
    fun filterWebhooks() {
        paparazzi.snapshot("filter_webhooks_phone") {
            YourNotificationsTheme(darkTheme = true) { AppSettingsScreen(apps = mockApps, onToggleApp = {_,_->}, onToggleWebhook = {_,_->}, onBackClick = {}) }
        }
    }

    @Test
    fun setupWebhooks() {
        paparazzi.snapshot("setup_webhooks_phone") {
            YourNotificationsTheme(darkTheme = true) { MockWebhookSettingsScreen() }
        }
    }
}

class Tablet7ScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.NEXUS_5.copy(softButtons = false), useDeviceResolution = true)

    @Test
    fun filterWebhooks() {
        paparazzi.snapshot("filter_webhooks_7inch") {
            YourNotificationsTheme(darkTheme = true) { AppSettingsScreen(apps = mockApps, onToggleApp = {_,_->}, onToggleWebhook = {_,_->}, onBackClick = {}) }
        }
    }

    @Test
    fun setupWebhooks() {
        paparazzi.snapshot("setup_webhooks_7inch") {
            YourNotificationsTheme(darkTheme = true) { MockWebhookSettingsScreen() }
        }
    }
}

class Tablet10ScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.NEXUS_5.copy(softButtons = false), useDeviceResolution = true)

    @Test
    fun filterWebhooks() {
        paparazzi.snapshot("filter_webhooks_10inch") {
            YourNotificationsTheme(darkTheme = true) { AppSettingsScreen(apps = mockApps, onToggleApp = {_,_->}, onToggleWebhook = {_,_->}, onBackClick = {}) }
        }
    }

    @Test
    fun setupWebhooks() {
        paparazzi.snapshot("setup_webhooks_10inch") {
            YourNotificationsTheme(darkTheme = true) { MockWebhookSettingsScreen() }
        }
    }
}
