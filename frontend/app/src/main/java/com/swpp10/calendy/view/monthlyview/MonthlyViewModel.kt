package com.swpp10.calendy.view.monthlyview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swpp10.calendy.data.maindb.plan.IPlanRepository
import com.swpp10.calendy.data.maindb.plan.Plan
import com.swpp10.calendy.utils.afterDays
import com.swpp10.calendy.utils.afterMonths
import com.swpp10.calendy.utils.toCalendarDay
import com.swpp10.calendy.utils.toFirstDateOfMonth
import com.swpp10.calendy.utils.toLastDateOfMonth
import com.swpp10.calendy.view.monthlyview.decorator.TitleDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MonthlyViewModel(
    private val planRepository: IPlanRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonthlyPageUIState())
    val uiState :StateFlow<MonthlyPageUIState> = _uiState.asStateFlow()
    val titleDecorators = MutableStateFlow(emptyList<com.swpp10.calendy.view.monthlyview.decorator.TitleDecorator>())
    private var job : Job?=null

    init{
        getPlansOfMonth(CalendarDay.today())
    }

    fun setCurrentMonth(month : CalendarDay)
    {
        _uiState.update { current -> current.copy(currentMonth = month) }
        getPlansOfMonth(month) // update plans
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
    private fun getPlansOfMonth(month: CalendarDay)
    {
        val flow = planRepository.getMonthlyPlansStream(month.afterMonths(-1).toFirstDateOfMonth().afterDays(-14),
                                                        month.afterMonths(1).toLastDateOfMonth().afterDays(14))

        job?.cancel()
        job = viewModelScope.launch {
            flow.collect{
                updatePlanList(it)
            }
        }
    }

    //to toggle tod0's isComplete
    fun updatePlan(plan: Plan){
        viewModelScope.launch(context = Dispatchers.IO) { planRepository.update(plan) }
    }

    private fun updatePlanList(planList:List<Plan>)
    {
//        _uiState.update { current -> current.copy(planLabelContainer = PlanLabelContainer().setPlans(planList)) }
        val container = PlanLabelContainer().setPlans(
            planList,
            _uiState.value.currentMonth.afterMonths(-1).toFirstDateOfMonth().afterDays(-14),
            _uiState.value.currentMonth.afterMonths(1).toLastDateOfMonth().afterDays(14)
        )
        _uiState.update { current -> current.copy(planLabelContainer = container) }
        val decos= mutableListOf<com.swpp10.calendy.view.monthlyview.decorator.TitleDecorator>()
        for((date,slot) in container)
        {
            decos.add(
                com.swpp10.calendy.view.monthlyview.decorator.TitleDecorator(
                    date.toCalendarDay(),
                    slot
                )
            )
        }

        titleDecorators.update { decos }
    }

    fun deletePlan(plan: Plan) {
        viewModelScope.launch { planRepository.delete(plan) }
    }
}