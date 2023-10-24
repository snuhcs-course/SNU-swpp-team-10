package com.example.calendy.view.monthlyview
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calendy.CalendyApplication
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.PlanRepository
import com.example.calendy.data.plan.Schedule
import com.example.calendy.utils.toEndTime
import com.example.calendy.utils.toStartTime
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MonthlyDayPlanDetailViewModel(
    private var planRepository: PlanRepository,
    private var selectedId : Int,
    private var planType: Plan.PlanType
) : ViewModel() {


    private val _uiState = MutableStateFlow(DayPlanDetailUiState())
    val uiState: StateFlow<DayPlanDetailUiState> = _uiState.asStateFlow()


    private suspend fun fetchPlan(): StateFlow<Plan> {
        return planRepository.getPlanById(selectedId,planType).stateIn(scope = viewModelScope)
    }

    class Factory(val application: Application, val selectedId: Int, val planType: Plan.PlanType) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val calendyContainer = (application as CalendyApplication).container
            val planRepository = PlanRepository(calendyContainer.scheduleRepository,calendyContainer.todoRepository)
            return MonthlyDayPlanDetailViewModel(planRepository,selectedId, planType) as T
        }
    }
}