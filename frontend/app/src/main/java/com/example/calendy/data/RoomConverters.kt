package com.example.calendy.data

import androidx.room.TypeConverter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RoomConverters {
    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    @TypeConverter
    fun fromString(value: String?): Date? {
        return try {
            value?.let { format.parse(it) }
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    @TypeConverter
    fun dateToString(date: Date?): String? {
        return date?.let { format.format(it) }
    }
}