package com.yournotifications.domain.classifier

import com.yournotifications.data.model.BucketType
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationClassifier @Inject constructor() {

    private val OTP_PATTERN = Pattern.compile("\\b(\\d{4,8})\\b")
    private val OTP_KEYWORDS = listOf("otp", "verification", "code", "password", "pin")

    fun classify(packageName: String, title: String, text: String): BucketType {
        val content = "${title.lowercase()} ${text.lowercase()}"
        
        // Rule 1: OTP
        if (OTP_KEYWORDS.any { content.contains(it) } && OTP_PATTERN.matcher(content).find()) {
            return BucketType.OTP
        }

        // Rule 2: Personal (Messaging apps)
        val messagingApps = listOf(
            "com.whatsapp", "org.telegram.messenger", "com.facebook.orca",
            "com.google.android.apps.messaging", "com.viber.voip", "com.signal.messenger"
        )
        if (messagingApps.any { packageName.contains(it) }) {
            return BucketType.PERSONAL
        }

        // Rule 3: Alerts (System, battery, etc.)
        val systemApps = listOf("android", "com.android.systemui", "com.google.android.gms")
        if (systemApps.any { packageName.contains(it) }) {
            return BucketType.ALERTS
        }

        // Rule 4: Transactions
        val bankingKeywords = listOf("bank", "transaction", "debited", "credited", "payment")
        if (bankingKeywords.any { content.contains(it) }) {
            return BucketType.TRANSACTIONS
        }

        // Rule 5: Broadcast (Marketing/News)
        val broadcastKeywords = listOf("offer", "deal", "discount", "news", "update", "subscribe")
        if (broadcastKeywords.any { content.contains(it) }) {
            return BucketType.BROADCAST
        }

        return BucketType.UNKNOWN
    }
}
