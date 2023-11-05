package com.example.calendy.utils

import com.prolificinteractive.materialcalendarview.CalendarDay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateHelper {
    /**
     * Returns Date object from exact time
     * Optional parameter hour, minute is set default to 0
     * We do not use second & millisecond. second & millisecond is set to 0
     * @param softWrap if set to true, return valid date (e.g. 02.28)
     * if set to false, assert if date is valid (e.g. 02.31) will cause exception
     */
    fun getDate(
        year: Int,
        monthZeroIndexed: Int,
        day: Int,
        hourOfDay: Int = 0,
        minute: Int = 0,
        softWrap: Boolean = true
    ): Date = with(Calendar.getInstance()) {
        // set to date with no problem at all.
        // if this was (month: Mar, day: 31) -> argument (month: Feb, day: 31) will pass
        this.set(2020, 1 - 1, 1, 0, 0)

        for ((calendarField, value) in listOf(
            Pair(Calendar.YEAR, year),
            Pair(Calendar.MONTH, monthZeroIndexed),
            Pair(Calendar.DATE, day),
            Pair(Calendar.HOUR_OF_DAY, hourOfDay),
            Pair(Calendar.MINUTE, minute),
            Pair(Calendar.SECOND, 0),
            Pair(Calendar.MILLISECOND, 0),
        )) {
            if (softWrap) {
                var safeValue = value
                if (value < this.getActualMinimum(calendarField)) {
                    safeValue = this.getActualMinimum(calendarField)
                }
                if (this.getActualMaximum(calendarField) < value) {
                    safeValue = this.getActualMaximum(calendarField)
                }
                set(calendarField, safeValue)
            } else {
                assert(this.getActualMinimum(calendarField) <= value)
                assert(value <= this.getActualMaximum(calendarField))
                set(calendarField, value)
            }
        }
        this.time
    }

    fun getDateFromMillis(dateInMillis: Long): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis
        return calendar.time
    }

    data class DateFields(
        val year: Int, val monthZeroIndexed: Int, val day: Int, val hour: Int, val minute: Int
    )

    fun Date.extract(): DateFields {
        val calendar = Calendar.getInstance()
        calendar.time = this
        return DateFields(
            year = calendar.get(Calendar.YEAR),
            monthZeroIndexed = calendar.get(Calendar.MONTH),
            day = calendar.get(Calendar.DATE),
            hour = calendar.get(Calendar.HOUR_OF_DAY),
            minute = calendar.get(Calendar.MINUTE),
        )
    }

    /**
     * Returns Date object for end of (year or month or ...)
     * Usage: endOf(2023). endOf(2023, 8), endOf(2023, 8, 31)
     */
    fun endOf(
        year: Int,
        monthZeroIndexed: Int? = null,
        day: Int? = null,
        hourOfDay: Int? = null,
        minute: Int? = null,
    ): Date = getDate(
        year = year,
        monthZeroIndexed = monthZeroIndexed ?: (13 - 1),
        day = day ?: 32,
        hourOfDay = hourOfDay ?: 25,
        minute = minute ?: 61,
        softWrap = true
    )


    /**
     * DueTime for tod0
     */
    fun getYearlyDueTime(year: Int): Date = endOf(year)

    /**
     * DueTime for tod0
     * @param monthZeroIndexed 0 ~ 11 based
     */
    fun getMonthlyDueTime(year: Int, monthZeroIndexed: Int): Date = endOf(year, monthZeroIndexed)

    /**
     * DueTime for tod0
     * @param monthZeroIndexed 0 ~ 11 based
     */
    fun getDailyDueTime(year: Int, monthZeroIndexed: Int, day: Int): Date =
        endOf(year, monthZeroIndexed, day)

    /**
     * String formatter for date
     */
    fun getDayOfWeek(date: Date): String {
        val sdf = SimpleDateFormat("EEEE", Locale.KOREA)
        val yesterday: Date =
            Date(date.getTime() - 1000 * 60 * 60 * 24) // add one day. 요일이 하루씩 밀리는 문제가 있었음.
        return sdf.format(yesterday)
    }
}

// extension for CalendarDay  and Date

fun CalendarDay.toDate(): Date = Date(year, month, day)
fun CalendarDay.toStartTime(): Date = Date(year, month, day, 0, 0)
fun CalendarDay.toEndTime(): Date = Date(year, month, day, 23, 59)
fun CalendarDay.getWeekDay(): String = DateHelper.getDayOfWeek(toDate())
fun Date.toCalendarDay(): CalendarDay = CalendarDay.from(this)
