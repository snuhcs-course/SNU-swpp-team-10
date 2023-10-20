package com.example.calendy.view.editplanview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendy.data.category.Category
import com.example.calendy.data.category.ICategoryRepository
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Plan.PlanType
import com.example.calendy.data.plan.schedule.IScheduleRepository
import com.example.calendy.data.plan.todo.ITodoRepository
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
    private val categoryRepository: ICategoryRepository
) : ViewModel() {

    // ViewModel 내에서만 uiState 수정 가능하도록 설정
    private val _uiState = MutableStateFlow(EditPlanUiState())
    val uiState: StateFlow<EditPlanUiState> = _uiState.asStateFlow()
    val categoryListState = (categoryRepository.getCategoriesStream()).stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
    )

    // TODO: EntryType 대신 PlanType 사용하기
    fun setType(selectedType: PlanType) {
        _uiState.update { currentState -> currentState.copy(entryType = selectedType) }
    }

    fun setTitle(userInput: String) {
        _uiState.update { currentState -> currentState.copy(titleField = userInput) }
    }

    fun setMemo(userInput: String) {
        _uiState.update { currentState -> currentState.copy(memoField = userInput) }
    }

    fun setStartTime(inputDate: Date) {
        _uiState.update { currentState -> currentState.copy(startTime = inputDate) }
    }

    fun setEndTime(inputDate: Date) {
        _uiState.update { currentState -> currentState.copy(endTime = inputDate) }
    }

    fun setCategory(category: Category?) {
        _uiState.update { currentState -> currentState.copy(category = category) }
    }

    fun setPriority(input: Int) {
        val priority = max(1, min(5, input))
        _uiState.update { currentState -> currentState.copy(priority = priority) }
    }

    fun addCategory(title: String, defaultPriority: Int) {
        Log.d("GUN", "$title $defaultPriority");
        viewModelScope.launch {
            categoryRepository.insert(Category(title = title, defaultPriority = defaultPriority))
        }
    }

    fun setIsComplete(isComplete: Boolean) {
        _uiState.update {
            currentState -> currentState.copy(isComplete = isComplete)
        }
    }

    fun setShowInMonthlyView(showInMonthlyView: Boolean) {
        _uiState.update {
                currentState -> currentState.copy(showInMonthlyView = showInMonthlyView)
        }
    }


    fun deletePlan() {
        when (_uiState.value.entryType) {
            is PlanType.Schedule -> {
                // scheduleRepository.deleteSchedule()
            }

            is PlanType.Todo     -> {
                // todoRepository.deleteTodo()
            }

            else                  -> {}
        }
    }

    fun editPlan() {
        val currentState = _uiState.value
        when (currentState.entryType) {
            is PlanType.Schedule -> {
//                val newSchedule: Schedule = Schedule(
//                        title = currentState.titleField,
//                        startTime = currentState.startTime ?: Date(),
//                        endTime = currentState.endTime ?: Date(),
//                        memo = currentState.memoField,
//                        repeatGroupId = 0,  // You might need a way to set this from the UI or some logic
//                        categoryId = currentState.categoryID,
//                        priority = currentState.priority,
//                )
//                viewModelScope.launch { scheduleRepository.insertSchedule(newSchedule) }


            }

            is PlanType.Todo     -> {
//                val newTodo: Todo = Todo(
//                        title = currentState.titleField,
//                        dueTime = currentState.endTime,
//                        yearly = false,
//                        monthly = false,
//                        daily = false,
//                        memo = currentState.memoField,
//                        complete = false,
//                        repeatGroupId = 0,
//                        categoryId = currentState.categoryID,
//                        priority = currentState.priority,
//                )
//                viewModelScope.launch { todoRepository.insertTodo(newTodo) }

            }

            else                  -> {}
        }

    }


}