package com.example.calendy.view.monthlyview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendy.data.maindb.plan.IPlanRepository
import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.utils.afterDays
import com.example.calendy.utils.toFirstDateOfMonth
import com.example.calendy.utils.toLastDateOfMonth
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
        getPlansOfMonth(month, month) // update plans
    }
    fun setSelectedDate(day :CalendarDay){
        _uiState.update { current -> current.copy(selectedDate = day) }
    }
    fun getAllPlans():StateFlow<List<Plan>>
    {
        return planRepository.getAllPlansStream().stateIn(
            scope = viewModelScope,
            initialValue = emptyList<Plan>(),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
        )
    }
    private fun getPlansOfMonth(startDate:CalendarDay, endDate:CalendarDay)
    {
        val flow = planRepository.getPlansStream(startDate.toFirstDateOfMonth().afterDays(-14),
                                                 endDate.toLastDateOfMonth().afterDays(14))
        job?.cancel()
        job = viewModelScope.launch {
            flow.collect{
                updatePlanList(it)
            }
        }
    }

    //to toggle tod0's isComplete
    fun updatePlan(plan: Plan){
        viewModelScope.launch { planRepository.update(plan) }
    }

    private fun updatePlanList(planList:List<Plan>)
    {
        _uiState.update { current -> current.copy(plansOfMonth = planList) }
    }
}