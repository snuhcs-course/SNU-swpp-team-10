package com.example.calendy.utils

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import java.time.LocalDate
import com.example.calendy.R
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateHelper {
    /**
     * Returns Date object from exact time
     * Optional parameter hour, minute, second, millisecond is default to 0
     */
    fun getDate(year: Int, month: Int, day: Int, hourOfDay: Int = 0, minute: Int = 0, second: Int = 0, millisecond: Int = 0): Date =
            with(Calendar.getInstance()) {
                for ((calendarField, value) in listOf(
                        Pair(Calendar.YEAR, year),
                        Pair(Calendar.MONTH, month),
                        Pair(Calendar.DATE, day),
                        Pair(Calendar.HOUR_OF_DAY, hourOfDay),
                        Pair(Calendar.MINUTE, minute),
                        Pair(Calendar.SECOND, second),
                        Pair(Calendar.MILLISECOND, millisecond),
                )) {
                    assert(this.getActualMinimum(calendarField) <= value)
                    assert(year <= this.getActualMaximum(calendarField))
                    set(calendarField, value)
                }
                this.time
            }

    /**
     * Returns Date object for end of (year or month or ...)
     */
    fun endOf(year: Int, month: Int? = null, day: Int? = null, hourOfDay: Int? = null, minute: Int? = null, second: Int? = null, millisecond: Int? = null): Date =
            with(Calendar.getInstance()) {
                for ((calendarField, nullableValue) in listOf(
                        Pair(Calendar.YEAR, year),
                        Pair(Calendar.MONTH, month),
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
     * @param month 0 ~ 11 based
     */
    fun getMonthlyDueTime(year: Int, month: Int): Date = endOf(year, month)

    /**
     * DueTime for todo
     * @param month 0 ~ 11 based
     */
    fun getDailyDueTime(year: Int, month: Int, day: Int): Date = endOf(year, month, day)

    /**
     * String formatter for date
     */
    fun getDayOfWeek(date: Date): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(date)
    }
}

// extension for CalendarDay  and Date

fun CalendarDay.toDate() : Date = Date(year,month,day)
fun CalendarDay.toStartTime() : Date = Date(year,month,day,0,0)
fun CalendarDay.toEndTime() : Date = Date(year,month,day,23,59)
fun CalendarDay.getWeekDay() : String = DateHelper.getDayOfWeek(toDate())

