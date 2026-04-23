package com.yournotifications.data.model

enum class BucketType {
    ALL,
    PERSONAL,
    OTP,
    BROADCAST,
    TRANSACTIONS,
    ALERTS,
    UNKNOWN;

    val displayName: String get() = when (this) {
        ALL -> "All"
        PERSONAL -> "Personal"
        OTP -> "OTP"
        BROADCAST -> "Broadcast"
        TRANSACTIONS -> "Transactions"
        ALERTS -> "Alerts"
        UNKNOWN -> "Unknown"
    }
}
