package com.example.calendy.utils

import java.util.Date

class DateFormatter {
    // fixed date format
    // "yyyy-MM-dd HH:mm:ss"

    fun format(date: Date): String {
        val yyyy = date.year + 1900
        val MM = date.month + 1
        val dd = date.date
        val HH = date.hours
        val mm = date.minutes
        val ss = date.seconds
        return String.format("%d-%02d-%02d %02d:%02d:%02d", yyyy, MM, dd, HH, mm, ss)
    }
    fun parse(dateString: String): Date {
        val yyyy = dateString.substring(0, 4).toInt()
        val MM = dateString.substring(5, 7).toInt()
        val dd = dateString.substring(8, 10).toInt()
        val HH = dateString.substring(11, 13).toInt()
        val mm = dateString.substring(14, 16).toInt()
        val ss = dateString.substring(17, 19).toInt()
        return Date(yyyy-1900, MM-1, dd, HH, mm, ss)
    }
}