package com.yournotifications.data.db

import androidx.room.TypeConverter
import com.yournotifications.data.model.BucketType

class RoomConverters {
    @TypeConverter
    fun fromBucketType(value: BucketType): String {
        return value.name
    }

    @TypeConverter
    fun toBucketType(value: String): BucketType {
        return try {
            BucketType.valueOf(value)
        } catch (e: Exception) {
            BucketType.UNKNOWN
        }
    }

    @TypeConverter
    fun fromBucketTypeList(value: List<BucketType>): String {
        return value.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toBucketTypeList(value: String): List<BucketType> {
        if (value.isEmpty()) return emptyList()
        return value.split(",").map {
            try {
                BucketType.valueOf(it)
            } catch (e: Exception) {
                BucketType.UNKNOWN
            }
        }
    }
}
