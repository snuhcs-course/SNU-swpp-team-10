package com.swpp10.calendy.data

import androidx.room.TypeConverter
import com.swpp10.calendy.utils.DateHelper
import com.swpp10.calendy.utils.DateHelper.toLocalTimeString
import java.text.ParseException
import java.util.Date

class RoomConverters {
    @TypeConverter
    fun fromString(value: String): Date {
        return try {
            val date = DateHelper.parseLocalTimeString(value)
            date
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