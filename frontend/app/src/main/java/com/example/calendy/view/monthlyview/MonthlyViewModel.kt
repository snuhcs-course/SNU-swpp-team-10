package com.example.calendy.view.monthlyview

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.Todo
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.flow.StateFlow
import java.util.Hashtable
import kotlinx.coroutines.flow.Flow

class MonthlyViewModel : ViewModel() {
    /*
    * TODO:
    * get all plan data (schedule and todo) within designated period (startTime~endTime)
    * should provide plan data which has been separated by each dates
    *
    */

    // rearrange schedules/todos by calendar day
    // calendar day use year, month, day for equal operation
    // !! need to check if hash key exists after deleting all plans in a day
    val scheduleListByDay : Hashtable<CalendarDay, StateFlow<List<Schedule>>> = Hashtable()
    val todoListByDay : Hashtable<CalendarDay,StateFlow<List<Todo>>> = Hashtable()

    fun getScheduleOfDay(day:CalendarDay): StateFlow<List<Schedule>>? {
        return scheduleListByDay.getOrDefault(day,null)
    }
    fun getTodoOfDay(day:CalendarDay): StateFlow<List<Todo>>?{
        return todoListByDay.getOrDefault(day,null)
    }
    fun hasPlan(day:CalendarDay): Boolean {
        return scheduleListByDay.contains(day) || todoListByDay.contains(day)
    }


}