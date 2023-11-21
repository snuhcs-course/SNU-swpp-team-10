package com.example.calendy.data

import androidx.room.TypeConverter
import com.example.calendy.utils.DateHelper
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class RoomConverters {
    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    // IMPORTANT: SQLite assumes that dates are stored in UTC
    // iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));

    @TypeConverter
    fun fromString(value: String): Date {
        return try {
            format.parse(value)!!
        } catch (e: ParseException) {
            e.printStackTrace()
            DateHelper.getDate(2023, 12 - 1, 31)
        }
    }

    @TypeConverter
    fun dateToString(date: Date): String {
        return format.format(date)
    }
}