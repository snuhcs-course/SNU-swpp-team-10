package com.example.calendy.data

import androidx.room.TypeConverter
import com.example.calendy.utils.DateHelper
import com.example.calendy.utils.DateHelper.toLocalTimeString
import java.text.ParseException
import java.util.Date

class RoomConverters {
    @TypeConverter
    fun fromString(value: String): Date {
        return try {
            DateHelper.parseLocalTimeString(value)
        } catch (e: ParseException) {
            e.printStackTrace()
            DateHelper.getDate(2023, 12 - 1, 31)
        }
    }

    @TypeConverter
    fun dateToString(date: Date): String {
        return date.toLocalTimeString()
    }
}