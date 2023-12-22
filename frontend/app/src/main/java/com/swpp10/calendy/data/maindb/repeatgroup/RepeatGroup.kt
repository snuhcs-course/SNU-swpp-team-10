package com.swpp10.calendy.data.maindb.repeatgroup

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.swpp10.calendy.data.maindb.plan.Plan
import com.swpp10.calendy.data.maindb.plan.Schedule
import com.swpp10.calendy.data.maindb.plan.Todo
import com.swpp10.calendy.utils.DateHelper
import com.swpp10.calendy.utils.DateHelper.extract
import com.swpp10.calendy.utils.afterDays
import com.swpp10.calendy.utils.afterMonths
import com.swpp10.calendy.utils.afterYears
import com.swpp10.calendy.utils.applyTime
import com.swpp10.calendy.utils.dateOnly
import java.util.Calendar
import java.util.Date

@Entity(tableName = "repeat_group")
data class RepeatGroup(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "day")
    var day: Boolean = false,
    @ColumnInfo(name = "week")
    var week: Boolean = false,
    @ColumnInfo(name = "month")
    var month: Boolean = false,
    @ColumnInfo(name = "year")
    var year: Boolean = false,
    @ColumnInfo(name = "repeat_interval")
    var repeatInterval: Int = 0,
    @ColumnInfo(name = "repeat_rule")
    var repeatRule: String? = null,
    @ColumnInfo(name = "end_date")
    var endDate: Date?, // This should be date only
) {
    private fun parseWeeklyRepeatRule(repeatRule: String): List<Int> {
        // return SUN: 1 ~ SAT: 7
        val daysOfWeek = listOf("XXX", "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

        return daysOfWeek.mapIndexedNotNull { index, day ->
            if (repeatRule.contains(day)) {
                index
            } else null
        }
    }

    private fun parseMonthlyRepeatRule(repeatRule: String): List<Int> {
        return repeatRule.chunked(2).map { it.toInt() }
    }

    private fun nonNullEndDate(): Date {
        return endDate ?: DateHelper.worldEnd()
    }

    /**
     * Returns Iterable<Date> from repeat group. Date Only without time information.
     * @param startDate start date of repeat group. This need to be date only.
     */
    fun toIterable(startDate: Date): Iterable<Date> = object : Iterable<Date> {
        override fun iterator(): Iterator<Date> {
            // Internal Final Date
            val endDate = nonNullEndDate()
            return when {
                day   -> dayIterator(startDate, endDate)
                week  -> weekIterator(startDate, endDate).iterator()
                month -> monthIterator(startDate, endDate).iterator()
                year  -> yearIterator(startDate, endDate).iterator()
                else  -> throw Exception("No repeat rule")
            }
        }
    }

    //region iterators
    private fun dayIterator(startDate: Date, endDate: Date) = object : Iterator<Date> {
        private var currentDate = startDate

        override fun hasNext(): Boolean {
            return currentDate <= endDate
        }

        override fun next(): Date {
            val date = currentDate
            currentDate = currentDate.afterDays(repeatInterval)
            return date
        }
    }

    private fun weekIterator(startDate: Date, endDate: Date) = sequence<Date> {
        val repeatDays = if (repeatRule!=null) {
            parseWeeklyRepeatRule(repeatRule!!)
        } else {
            listOf()
        }

        val calendar = Calendar.getInstance()
        calendar.time = startDate.dateOnly()
        calendar.set(Calendar.DAY_OF_WEEK, 1)
        var currentDate = calendar.time

        while (currentDate <= endDate) {
            for (repeatDay in repeatDays) {
                calendar.time = currentDate
                calendar.set(Calendar.DAY_OF_WEEK, repeatDay)
                if ((calendar.time < startDate) || (endDate < calendar.time)) continue
                yield(calendar.time)
            }
            currentDate = currentDate.afterDays(repeatInterval * 7)
        }
    }

    private fun monthIterator(startDate: Date, endDate: Date) = sequence {
        val repeatDates = if (repeatRule!=null) {
            parseMonthlyRepeatRule(repeatRule!!)
        } else {
            listOf()
        }

        val calendar = Calendar.getInstance()
        calendar.time = startDate.dateOnly()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        var currentDate = calendar.time

        while (currentDate <= endDate) {
            val (year, monthZeroIndexed, _, _, _) = currentDate.extract()
            for (repeatDate in repeatDates) {
                try {
                    val date =
                        DateHelper.getDate(year, monthZeroIndexed, repeatDate, softWrap = false)
                    if ((date < startDate) || (endDate < date)) continue
                    yield(date)
                } catch (e: Exception) {
                    // Ignore errors like February 31th
                }
            }
            currentDate = currentDate.afterMonths(repeatInterval)
        }
    }

    private fun yearIterator(startDate: Date, endDate: Date) = sequence {
        // TODO: Not Complete! there is no yearRule
        val (_, monthZeroIndexed, day, _, _) = startDate.extract()

        val calendar = Calendar.getInstance()
        calendar.time = startDate.dateOnly()
        calendar.set(Calendar.MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        var currentDate = calendar.time

        while (currentDate <= endDate) {
            val (year, _, _, _, _) = currentDate.extract()
            try {
                val date = DateHelper.getDate(
                    year = year, monthZeroIndexed = monthZeroIndexed, day = day, softWrap = false
                )
                if ((date < startDate) || (endDate < date)) continue
                yield(date)
            } catch (e: Exception) {
                // Ignore errors like February 29th
            }
            currentDate = currentDate.afterYears(repeatInterval)
        }
    }
    //endregion

    fun generatePlanList(from: Date, until: Date, plan: Plan): List<Plan> {
        // TODO: ineffective because of filter.
        //  repeatGroup.toIterable should take from, until and be effective
        when (plan) {
            is Schedule -> {
                val duration = plan.endTime.time - plan.startTime.time
                val (_, _, _, hour, minute) = plan.startTime.extract()
                return this.toIterable(from).mapNotNull { repeatDate ->
                    val startTime = repeatDate.applyTime(hour, minute)
                    val endTime = Date(startTime.time + duration)

                    // [from, until] and [startTime, endTime] should overlap
                    // endTime < from -> [startTime, endTime] ,,, [from, until]
                    // until < startTime -> [from, until] ... [startTime, endTime]
                    if (endTime < from || until < startTime) null
                    else {
                        plan.copy(
                            id = 0,
                            startTime = repeatDate.applyTime(hour, minute),
                            endTime = endTime
                        )
                    }
                }
            }

            is Todo     -> {
                val (_, _, _, hour, minute) = plan.dueTime.extract()
                return this.toIterable(from).mapNotNull { repeatDate ->
                    val dueTime = repeatDate.applyTime(hour, minute)

                    if (dueTime < from || until < dueTime) null
                    else {
                        plan.copy(
                            id = 0, dueTime = repeatDate.applyTime(hour, minute)
                        )
                    }
                }
            }
        }
    }

}
