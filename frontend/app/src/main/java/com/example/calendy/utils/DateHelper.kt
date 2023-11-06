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


}

// extension for CalendarDay  and Date
fun Date.dayOfWeek(): String {
    val sdf = SimpleDateFormat("EEEE", Locale.KOREA)
//    val yesterday: Date = Date(getTime() - 1000 * 60 * 60 * 24) // add one day. 요일이 하루씩 밀리는 문제가 있었음.
    return sdf.format(this)
}
fun CalendarDay.toDate(): Date = Date(year-1900, month, day)
fun CalendarDay.toStartTime(): Date = Date(year-1900, month, day, 0, 0)
fun CalendarDay.toEndTime(): Date = Date(year-1900, month, day, 23, 59)
fun CalendarDay.toFirstDateOfMonth():Date = Date(year-1900,month,1)
fun CalendarDay.toLastDateOfMonth():Date = Date(year - 1900, month,1).lastDayOfMonth()
fun CalendarDay.getWeekDay(): String = toDate().dayOfWeek()
fun CalendarDay.afterDays(amount:Int): CalendarDay=toDate().afterDays(amount).toCalendarDay()
fun Date.toCalendarDay(): CalendarDay = CalendarDay.from(this)

fun Date.toDateDayString(showYear:Boolean=false): String
    = toDateString(showYear) + " " + dayOfWeek()
fun Date.toDateTimeString(showYear: Boolean = false): String
    = toDateString(showYear) + " " + toAmPmString() + " " + toTimeString(hour12 = true)
fun Date.toDateString(showYear: Boolean):String
    = if(showYear) String.format("%d년 %d월 %d일",year,month,date) else String.format("%d월 %d일",month,date)
fun Date.toAmPmString():String = if(hours<12) "오전" else "오후"
fun Date.toTimeString(hour12:Boolean = false, showSeconds:Boolean=false):String
{
    val h = if(hour12)
                if(hours<=12) hours else hours-12
            else
                hours
    return String.format("%d:%02d",h,minutes)
}
fun Date.equalDay(date:Date): Boolean = year==date.year && month == date.month && this.date == date.date
fun Date.afterDays(amount: Int):Date{
    val c = Calendar.getInstance()
    c.time=this
    c.add(Calendar.DATE,amount)
    return c.time
}
@Suppress("deprecation")
fun Date.lastDayOfMonth(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    val lastDay = calendar
        .getActualMaximum(Calendar.DAY_OF_MONTH)
    val lastDate = calendar.time
    lastDate.date = lastDay
    return lastDate
}