package com.example.calendy.view.monthlyview
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calendy.CalendyApplication
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.PlanRepository
import com.example.calendy.utils.toEndTime
import com.example.calendy.utils.toStartTime
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MonthlyDayPlanListViewModel(
    private var planRepository: PlanRepository,
    private var selectedDate: CalendarDay
) : ViewModel() {

    var plans = fetchPlans()

    private val _uiState = MutableStateFlow(DayPlanListUiState())
    val uiState: StateFlow<DayPlanListUiState> = _uiState.asStateFlow()

    fun changeDate(date : CalendarDay){
        selectedDate = date
        plans = fetchPlans()
        _uiState.update { currentState-> currentState.copy(day = selectedDate.day) }
    }

    private  fun fetchPlans():StateFlow<List<Plan>>{
        return planRepository.getPlansStream(selectedDate.toStartTime(),selectedDate.toEndTime()).stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
        )
    }

    class Factory(val application: Application, val date:CalendarDay) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val calendyContainer = (application as CalendyApplication).container
            val planRepository = PlanRepository(calendyContainer.scheduleRepository,calendyContainer.todoRepository)
            return MonthlyDayPlanListViewModel(planRepository,date) as T
        }
    }
}