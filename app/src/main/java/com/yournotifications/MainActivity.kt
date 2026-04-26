package com.yournotifications

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.yournotifications.ui.notifications.NotificationListScreen
import com.yournotifications.ui.notifications.NotificationViewModel
import com.yournotifications.ui.theme.YourNotificationsTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

import com.yournotifications.ui.settings.WebhookSettingsScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            YourNotificationsTheme {
                val navController = rememberNavController()
                val viewModel: NotificationViewModel = hiltViewModel()
                val groupedNotifications by viewModel.groupedNotifications.collectAsState()
                val selectedBucket by viewModel.selectedBucket.collectAsState()
                val apps by viewModel.trackedApps.collectAsState()

                val isListenerEnabled = remember { mutableStateOf(true) }
                
                LaunchedEffect(Unit) {
                    val enabledListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
                    isListenerEnabled.value = enabledListeners?.contains(packageName) ?: false
                }

                NavHost(navController = navController, startDestination = "list") {
                    composable("list") {
                        val context = LocalContext.current
                        Column {
                            if (!isListenerEnabled.value) {
                                Surface(
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    modifier = Modifier.padding(16.dp).clickable {
                                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                                    },
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text(
                                        "Notification Listener is disabled. Tap to enable it in system settings.",
                                        modifier = Modifier.padding(16.dp),
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                            NotificationListScreen(
                                groupedNotifications = groupedNotifications,
                                selectedBucket = selectedBucket,
                                onBucketSelected = { viewModel.selectBucket(it) },
                                onSettingsClick = { 
                                    navController.navigate("settings")
                                },
                                onAppManagementClick = {
                                    navController.navigate("apps")
                                },
                                onDeleteNotification = { viewModel.deleteNotification(it) }
                            )
                        }
                    }
                    composable("settings") {
                        WebhookSettingsScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onHistoryClick = { navController.navigate("history") }
                        )
                    }
                    composable("history") {
                        val logs by viewModel.webhookLogs.collectAsState()
                        com.yournotifications.ui.settings.WebhookHistoryScreen(
                            logs = logs,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("apps") {
                        com.yournotifications.ui.settings.AppSettingsScreen(
                            apps = apps,
                            onToggleApp = { pkg, enabled -> viewModel.toggleAppStatus(pkg, enabled) },
                            onToggleWebhook = { pkg, enabled -> viewModel.toggleAppWebhookStatus(pkg, enabled) },
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
