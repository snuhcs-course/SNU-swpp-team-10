package com.example.calendy.view.weeklyview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendy.data.maindb.plan.IPlanRepository
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.data.maindb.plan.schedule.IScheduleRepository
import com.example.calendy.data.maindb.plan.todo.ITodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class WeeklyViewModel(private val scheduleRepository: IScheduleRepository, private val todoRepository: ITodoRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(WeeklyUiState())
    val uiState: StateFlow<WeeklyUiState> = _uiState.asStateFlow()

    fun updatePosition(pos : Int) {
        _uiState.value = _uiState.value.copy(currentPosition = pos)
    }
    fun increaseCurrentWeek() {
        val newStart = Calendar.getInstance().apply {
            time = uiState.value.currentWeek.first
            add(Calendar.DAY_OF_MONTH, 7)
        }.time

        val newEnd= Calendar.getInstance().apply {
            time = uiState.value.currentWeek.second
            add(Calendar.DAY_OF_MONTH, 7)
        }.time
        _uiState.value = _uiState.value.copy(currentWeek = Pair(newStart,newEnd))
    }
    fun decreaseCurrentWeek() {
        val newStart = Calendar.getInstance().apply {
            time = uiState.value.currentWeek.first
            add(Calendar.DAY_OF_MONTH, -7)
        }.time

        val newEnd= Calendar.getInstance().apply {
            time = uiState.value.currentWeek.second
            add(Calendar.DAY_OF_MONTH, -7)
        }.time
        _uiState.value = _uiState.value.copy(currentWeek = Pair(newStart,newEnd))
    }
    fun updateWeekPlans() {
        viewModelScope.launch {
            val weekSchedules = scheduleRepository.getSchedulesStream(uiState.value.currentWeek.first, uiState.value.currentWeek.second)
            val weekTodos = todoRepository.getTodosStream(uiState.value.currentWeek.first, uiState.value.currentWeek.second)
            val multipleDaySchedules = weekSchedules.first().filterNot { schedule ->
                isSameDay(schedule.startTime, schedule.endTime)
            }
            _uiState.update { currentState ->
                currentState.copy(
                    weekSchedules = weekSchedules.first(),
                    weekTodos = weekTodos.first(),
                    multipleDaySchedules = multipleDaySchedules
                )
            }
        }
    }
    fun updateCompletionOfTodo(todo: Todo) {
        viewModelScope.launch {
            val updatedTodo = todo.copy(complete = !todo.complete)
            todoRepository.update(updatedTodo)
            val updatedTodos = _uiState.value.weekTodos.map {
                if (it.id == todo.id) updatedTodo else it
            }
            _uiState.value = _uiState.value.copy(weekTodos = updatedTodos)
        }
    }


}