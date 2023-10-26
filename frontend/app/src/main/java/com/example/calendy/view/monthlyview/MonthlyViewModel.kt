package com.example.calendy.view.monthlyview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendy.data.category.ICategoryRepository
import com.example.calendy.data.plan.IPlanRepository
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.Todo
import com.example.calendy.data.plan.schedule.IScheduleRepository
import com.example.calendy.data.plan.todo.ITodoRepository
import com.example.calendy.data.repeatgroup.IRepeatGroupRepository
import com.example.calendy.utils.toDate
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.flow.StateFlow
import java.util.Hashtable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import java.util.Date

class MonthlyViewModel(
    private val planRepository: IPlanRepository,
    private val scheduleRepository: IScheduleRepository,
    private val todoRepository: ITodoRepository,
    private val categoryRepository: ICategoryRepository,
    private val repeatGroupRepository: IRepeatGroupRepository
) : ViewModel() {
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
//    var planList : StateFlow<List<Plan>> = scheduleRepository.getAllSchedule().stateIn(
//        scope = viewModelScope,
//        initialValue = emptyList<Plan>(),
//        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
//    )

    fun getPlanListState(startTime:CalendarDay,endTime:CalendarDay): StateFlow<List<Plan>> {
        val stream = (todoRepository.getAllTodo())
        val result = stream.stateIn(
            scope = viewModelScope,
            initialValue = emptyList<Plan>(),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
        )
        return result
    }

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