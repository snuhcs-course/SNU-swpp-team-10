package com.example.calendy.view.editplanview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendy.data.category.Category
import com.example.calendy.data.category.ICategoryRepository
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Plan.PlanType
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.Todo
import com.example.calendy.data.plan.schedule.IScheduleRepository
import com.example.calendy.data.plan.todo.ITodoRepository
import com.example.calendy.data.repeatgroup.RepeatGroup
import com.example.calendy.data.repeatgroup.IRepeatGroupRepository
import com.example.calendy.utils.DateHelper.extract
import com.example.calendy.utils.DateHelper.getDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.max
import kotlin.math.min


class EditPlanViewModel(
    private val scheduleRepository: IScheduleRepository,
    private val todoRepository: ITodoRepository,
    private val categoryRepository: ICategoryRepository,
    private val repeatGroupRepository: IRepeatGroupRepository
) : ViewModel() {

    // ViewModel 내에서만 uiState 수정 가능하도록 설정
    private val _uiState = MutableStateFlow(EditPlanUiState())
    val uiState: StateFlow<EditPlanUiState> = _uiState.asStateFlow()
    val categoryListState = (categoryRepository.getCategoriesStream()).stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
    )

    // Style: functions' order is aligned with UI
    fun setType(selectedType: PlanType) {
        _uiState.update { currentState -> currentState.copy(entryType = selectedType) }
    }

    fun setTitle(userInput: String) {
        _uiState.update { currentState -> currentState.copy(titleField = userInput) }
    }

    fun setIsComplete(isComplete: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isComplete = isComplete)
        }
    }

    fun setStartTime(inputDate: Date) {
        _uiState.update { currentState -> currentState.copy(startTime = inputDate) }
    }

    fun setEndTime(inputDate: Date) {
        _uiState.update { currentState -> currentState.copy(endTime = inputDate) }
    }

    fun toggleIsYearly() {
        if (uiState.value.isYearly) {
            _uiState.update { currentState ->
                currentState.copy(
                    isYearly = false,
                    isMonthly = false,
                    isDaily = false
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isYearly = true,
                    isMonthly = false,
                    isDaily = false
                )
            }
        }
    }

    fun toggleIsMonthly() {
        if (uiState.value.isMonthly) {
            _uiState.update { currentState ->
                currentState.copy(
                    isYearly = false,
                    isMonthly = false,
                    isDaily = false
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isYearly = false,
                    isMonthly = true,
                    isDaily = false
                )
            }
        }
    }

    fun toggleIsDaily() {
        if (uiState.value.isDaily) {
            _uiState.update { currentState ->
                currentState.copy(
                    isYearly = false,
                    isMonthly = false,
                    isDaily = false
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isYearly = false,
                    isMonthly = false,
                    isDaily = true
                )
            }
        }
    }

    fun setDueYear(newYear: Int) {
        val (_, monthZeroIndexed, day, hour, minute) = uiState.value.dueTime.extract()
        setDueTime(
            getDate(
                year = newYear,
                monthZeroIndexed = monthZeroIndexed,
                day = day,
                hourOfDay = hour,
                minute = minute,
                assertValueIsValid = false
            )
        )
    }

    fun setDueMonth(newYear: Int, newMonthZeroIndexed: Int) {
        val (_, _, day, hour, minute) = uiState.value.dueTime.extract()
        setDueTime(
            getDate(
                year = newYear,
                monthZeroIndexed = newMonthZeroIndexed,
                day = day,
                hourOfDay = hour,
                minute = minute,
                assertValueIsValid = false
            )
        )
    }

    fun setDueTime(inputDate: Date) {
        Log.d("GUN", inputDate.toString())
        _uiState.update { currentState -> currentState.copy(dueTime = inputDate) }
    }

    fun setCategory(category: Category?) {
        _uiState.update { currentState -> currentState.copy(category = category) }
    }

    fun addCategory(title: String, defaultPriority: Int) {
        viewModelScope.launch {
            categoryRepository.insert(Category(title = title, defaultPriority = defaultPriority))
        }
    }

    fun setRepeatGroup(repeatGroup: RepeatGroup) {
        _uiState.update { currentState -> currentState.copy(repeatGroup = repeatGroup)  }
    }

    fun deleteRepeatGroup(repeatGroup: RepeatGroup) {
        viewModelScope.launch {
            repeatGroupRepository.delete(repeatGroup)
        }
    }

    fun addRepeatGroup(repeatGroup: RepeatGroup) {
        viewModelScope.launch {
            repeatGroupRepository.insert(repeatGroup)
        }
    }

    fun updateRepeatGroup(repeatGroup: RepeatGroup) {
        viewModelScope.launch {
            repeatGroupRepository.update(repeatGroup)
        }
    }

    fun setPriority(input: Int) {
        val priority = max(1, min(5, input))
        _uiState.update { currentState -> currentState.copy(priority = priority) }
    }

    fun setMemo(userInput: String) {
        _uiState.update { currentState -> currentState.copy(memoField = userInput) }
    }

    fun setShowInMonthlyView(showInMonthlyView: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(showInMonthlyView = showInMonthlyView)
        }
    }


    fun deletePlan(plan: Plan) {
        when (_uiState.value.entryType) {
            is PlanType.Schedule -> {
//                viewModelScope.launch { scheduleRepository.deleteSchedule()}
            }

            is PlanType.Todo  -> {
//                viewModelScope.launch { todoRepository.deleteTodo()}
            }
        }
    }

    fun addPlan() {
        val currentState = _uiState.value
        when (currentState.entryType) {
            is PlanType.Schedule -> {
                val newSchedule: Schedule = Schedule(
                        title = currentState.titleField,
                        startTime = currentState.startTime,
                        endTime = currentState.endTime,
                        memo = currentState.memoField,
                        repeatGroupId = currentState.repeatGroup?.id ?:null,
                        categoryId = currentState.category?.id ?:null,
                        priority = currentState.priority,
                        showInMonthlyView = currentState.showInMonthlyView,
                        isOverridden = false
                )
                viewModelScope.launch { scheduleRepository.insertSchedule(newSchedule) }
            }

            is PlanType.Todo-> {
                val newTodo: Todo = Todo(
                        title = currentState.titleField,
                        dueTime = currentState.dueTime,
                        yearly = currentState.isYearly,
                        monthly = currentState.isMonthly,
                        daily = currentState.isDaily,
                        memo = currentState.memoField,
                        complete = currentState.isComplete,
                        repeatGroupId = currentState.repeatGroup?.id ?:null,
                        categoryId = currentState.category?.id ?:null,
                        priority = currentState.priority,
                        showInMonthlyView = currentState.showInMonthlyView,
                        isOverridden = false
                )
                viewModelScope.launch { todoRepository.insertTodo(newTodo) }

            }
        }
    }

//    fun updatePlan() {
//        val currentState = _uiState.value
//        when (currentState.entryType) {
//            is PlanType.Schedule -> {
//
//                viewModelScope.launch { scheduleRepository.updateSchedule() }
//            }
//
//            is PlanType.Todo-> {
//
//                viewModelScope.launch { todoRepository.updateTodo() }
//
//            }
//        }
//    }


}