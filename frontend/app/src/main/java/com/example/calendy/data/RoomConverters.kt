package com.example.calendy.data

import androidx.room.TypeConverter
import java.util.Date

class RoomConverters {
    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}