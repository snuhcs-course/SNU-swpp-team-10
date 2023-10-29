package com.example.calendy.utils

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarDay.today
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


object DateHelper {
    /**
     * Returns Date object from exact time
     * Optional parameter hour, minute, second, millisecond is default to 0
     * @param assertValueIsValid if set to true, try assertion if date is valid (e.g. 02.31).
     * if set to false, return valid date (e.g. 02.28)
     */
    fun getDate(
        year: Int,
        monthZeroIndexed: Int,
        day: Int,
        hourOfDay: Int = 0,
        minute: Int = 0,
        second: Int = 0,
        millisecond: Int = 0,
        assertValueIsValid: Boolean = true
    ): Date = with(Calendar.getInstance()) {
        for ((calendarField, value) in listOf(
            Pair(Calendar.YEAR, year),
            Pair(Calendar.MONTH, monthZeroIndexed),
            Pair(Calendar.DATE, day),
            Pair(Calendar.HOUR_OF_DAY, hourOfDay),
            Pair(Calendar.MINUTE, minute),
            Pair(Calendar.SECOND, second),
            Pair(Calendar.MILLISECOND, millisecond),
        )) {
            if (assertValueIsValid) {
                assert(this.getActualMinimum(calendarField) <= value)
                assert(value <= this.getActualMaximum(calendarField))
                set(calendarField, value)
            } else {
                var safeValue = value
                if (value < this.getActualMinimum(calendarField)) {
                    safeValue = this.getActualMinimum(calendarField)
                }
                if (this.getActualMaximum(calendarField) < value) {
                    safeValue = this.getActualMaximum(calendarField)
                }
                set(calendarField, safeValue)
            }
        }
        this.time
    }

    fun getDateInMillis(dateInMillis: Long): Date {
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
     */
    fun endOf(
        year: Int,
        monthZeroIndexed: Int? = null,
        day: Int? = null,
        hourOfDay: Int? = null,
        minute: Int? = null,
        second: Int? = null,
        millisecond: Int? = null
    ): Date = with(Calendar.getInstance()) {
        for ((calendarField, nullableValue) in listOf(
            Pair(Calendar.YEAR, year),
            Pair(Calendar.MONTH, monthZeroIndexed),
            Pair(Calendar.DATE, day),
            Pair(Calendar.HOUR_OF_DAY, hourOfDay),
            Pair(Calendar.MINUTE, minute),
            Pair(Calendar.SECOND, second),
            Pair(Calendar.MILLISECOND, millisecond),
        )) {
            val value = nullableValue ?: this.getActualMaximum(calendarField)
            assert(this.getActualMinimum(calendarField) <= value)
            assert(value <= this.getActualMaximum(calendarField))
            set(calendarField, value)
        }
        this.time
    }


    /**
     * DueTime for todo
     */
    fun getYearlyDueTime(year: Int): Date = endOf(year)

    /**
     * DueTime for todo
     * @param monthZeroIndexed 0 ~ 11 based
     */
    fun getMonthlyDueTime(year: Int, monthZeroIndexed: Int): Date = endOf(year, monthZeroIndexed)

    /**
     * DueTime for todo
     * @param monthZeroIndexed 0 ~ 11 based
     */
    fun getDailyDueTime(year: Int, monthZeroIndexed: Int, day: Int): Date =
        endOf(year, monthZeroIndexed, day)

    /**
     * String formatter for date
     */
    fun getDayOfWeek(date: Date): String {
        val sdf = SimpleDateFormat("EEEE", Locale.KOREA)
        val yesterday: Date = Date(date.getTime() - 1000 * 60 * 60 * 24) // add one day. 요일이 하루씩 밀리는 문제가 있었음.
        return sdf.format(yesterday)
    }
}

// extension for CalendarDay  and Date

fun CalendarDay.toDate() : Date = Date(year,month,day)
fun CalendarDay.toStartTime() : Date = Date(year,month,day,0,0)
fun CalendarDay.toEndTime() : Date = Date(year,month,day,23,59)
fun CalendarDay.getWeekDay() : String = DateHelper.getDayOfWeek(toDate())
fun Date.toCalendarDay() : CalendarDay = CalendarDay.from(this)
