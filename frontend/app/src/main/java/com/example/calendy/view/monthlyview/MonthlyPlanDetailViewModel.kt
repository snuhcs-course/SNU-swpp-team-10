package com.example.calendy.view.monthlyview
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calendy.CalendyApplication
import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.PlanRepository
import com.example.calendy.data.maindb.plan.PlanType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class MonthlyDayPlanDetailViewModel(
    private var planRepository: PlanRepository,
    private var selectedId : Int,
    private var planType: PlanType
) : ViewModel() {


    private val _uiState = MutableStateFlow(DayPlanDetailUiState())
    val uiState: StateFlow<DayPlanDetailUiState> = _uiState.asStateFlow()


    // NOTE From GUN: 에러가 나서 일단 주석 처리했습니다.
//    private suspend fun fetchPlan(): StateFlow<Plan> {
//        return planRepository.getPlanById(selectedId,planType).stateIn(scope = viewModelScope)
//    }

    class Factory(val application: Application, val selectedId: Int, val planType: PlanType) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val calendyContainer = (application as CalendyApplication).container
            val planRepository = PlanRepository(calendyContainer.scheduleRepository,calendyContainer.todoRepository)
            return MonthlyDayPlanDetailViewModel(planRepository,selectedId, planType) as T
        }
    }
}