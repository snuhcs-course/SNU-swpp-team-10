package com.example.calendy.view.monthlyview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendy.data.category.ICategoryRepository
import com.example.calendy.data.message.Message
import com.example.calendy.data.plan.IPlanRepository
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.Todo
import com.example.calendy.data.plan.schedule.IScheduleRepository
import com.example.calendy.data.plan.todo.ITodoRepository
import com.example.calendy.data.repeatgroup.IRepeatGroupRepository
import com.example.calendy.utils.toDate
import com.example.calendy.utils.toEndTime
import com.example.calendy.utils.toFirstDateOfMonth
import com.example.calendy.utils.toLastDateOfMonth
import com.example.calendy.utils.toStartTime
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import java.util.Hashtable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date

class MonthlyViewModel(
    private val planRepository: IPlanRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonthlyPageUIState())
    val uiState :StateFlow<MonthlyPageUIState> = _uiState.asStateFlow()
    var job : Job?=null

    init{
        getPlansOfMonth(CalendarDay.today(),CalendarDay.today())
    }

    fun setCurrentMonth(month : CalendarDay)
    {
        _uiState.update { current -> current.copy(currentMonth = month) }
    }
    fun setSelectedDate(day :CalendarDay){
        _uiState.update { current -> current.copy(selectedDate = day) }
    }
    fun getAllPlans():StateFlow<List<Plan>>
    {
        return planRepository.getAllPlans().stateIn(
            scope = viewModelScope,
            initialValue = emptyList<Plan>(),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
        )
    }
    fun getPlansOfMonth(startDate:CalendarDay,endDate:CalendarDay)
    {
        val flow = planRepository.getPlansStream(startDate.toFirstDateOfMonth(),endDate.toLastDateOfMonth())
        job?.cancel()
        job = viewModelScope.launch {
            flow.collect{
                updatePlanList(it)
            }
        }
    }

    private fun updatePlanList(planList:List<Plan>)
    {
        _uiState.update { current -> current.copy(plansOfMonth = planList) }
    }
}