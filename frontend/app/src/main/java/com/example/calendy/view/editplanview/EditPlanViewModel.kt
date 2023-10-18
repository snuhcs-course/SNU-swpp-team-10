package com.example.calendy.view.editplanview

import java.util.Date

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.calendy.data.schedule.IScheduleRepository
import com.example.calendy.data.todo.ITodoRepository
import kotlinx.coroutines.flow.update


class EditPlanViewModel(private val scheduleRepository: IScheduleRepository, private val todoRepository: ITodoRepository) : ViewModel() {

    // ViewModel 내에서만 uiState 수정 가능하도록 설정
    private val _uiState = MutableStateFlow(EditPlanUiState())
    val uiState: StateFlow<EditPlanUiState> = _uiState.asStateFlow()

    fun setType(selectedType: EntryType) {
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
    fun setCategory() {
        _uiState.update { currentState -> currentState.copy() }
    }
    fun setPriority() {
        _uiState.update { currentState -> currentState.copy() }
    }


    fun deletePlan() {
        when (_uiState.value.entryType) {
            is EntryType.Schedule -> {
               // scheduleRepository.deleteSchedule()
            }

            is EntryType.Todo -> {
               // todoRepository.deleteTodo()
            }
        }
    }

    fun editPlan() {
        val currentState = _uiState.value
        when (currentState.entryType) {
            is EntryType.Schedule -> {
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

            is EntryType.Todo -> {
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
        }

    }


}