package com.example.calendy.view.weeklyview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendy.data.maindb.plan.IPlanRepository
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.data.maindb.plan.schedule.IScheduleRepository
import com.example.calendy.data.maindb.plan.todo.ITodoRepository
import com.example.calendy.utils.afterDays
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class WeeklyViewModel(private val scheduleRepository: IScheduleRepository, private val todoRepository: ITodoRepository) : ViewModel() {
    private val _uiStateCurr:MutableStateFlow<WeeklyUiState>
    private val _uiStatePrev:MutableStateFlow<WeeklyUiState>
    private val _uiStateNext:MutableStateFlow<WeeklyUiState>
    val uiStatePrev: StateFlow<WeeklyUiState>
    val uiStateCurr: StateFlow<WeeklyUiState>
    val uiStateNext: StateFlow<WeeklyUiState>

    init {
        val (startOfWeek,endOfWeek) = getCurrentWeek()
        _uiStatePrev=MutableStateFlow(WeeklyUiState(
            currentPosition = Int.MAX_VALUE/2-1,
            currentWeek = Pair(startOfWeek.afterDays(-7),endOfWeek.afterDays(-7))
        ))
        _uiStateCurr=MutableStateFlow(WeeklyUiState(
            currentPosition = Int.MAX_VALUE/2,
            currentWeek = Pair(startOfWeek,endOfWeek)
        ))
        _uiStateNext=MutableStateFlow(WeeklyUiState(
            currentPosition = Int.MAX_VALUE/2+1,
            currentWeek = Pair(startOfWeek.afterDays(7),endOfWeek.afterDays(7))
        ))
        uiStatePrev = _uiStatePrev.asStateFlow()
        uiStateCurr = _uiStateCurr.asStateFlow()
        uiStateNext = _uiStateNext.asStateFlow()
    }

    fun updatePosition(pos : Int) {
        _uiStatePrev.value = _uiStatePrev.value.copy(currentPosition = pos-1)
        _uiStateCurr.value = _uiStateCurr.value.copy(currentPosition = pos)
        _uiStateNext.value = _uiStateNext.value.copy(currentPosition = pos+1)

    }
    fun increaseCurrentWeek() {
        val newStart = Calendar.getInstance().apply {
            time = uiStateCurr.value.currentWeek.first
            add(Calendar.DAY_OF_MONTH, 7)
        }.time

        val newEnd= Calendar.getInstance().apply {
            time = uiStateCurr.value.currentWeek.second
            add(Calendar.DAY_OF_MONTH, 7)
        }.time
        _uiStatePrev.value = _uiStatePrev.value.copy(currentWeek = Pair(newStart.afterDays(-7),newEnd.afterDays(-7)))
        _uiStateCurr.value = _uiStateCurr.value.copy(currentWeek = Pair(newStart,newEnd))
        _uiStateNext.value = _uiStateNext.value.copy(currentWeek = Pair(newStart.afterDays(7),newEnd.afterDays(7)))
    }
    fun decreaseCurrentWeek() {
        val newStart = Calendar.getInstance().apply {
            time = uiStateCurr.value.currentWeek.first
            add(Calendar.DAY_OF_MONTH, -7)
        }.time

        val newEnd= Calendar.getInstance().apply {
            time = uiStateCurr.value.currentWeek.second
            add(Calendar.DAY_OF_MONTH, -7)
        }.time
        _uiStatePrev.value = _uiStatePrev.value.copy(currentWeek = Pair(newStart.afterDays(-7),newEnd.afterDays(-7)))
        _uiStateCurr.value = _uiStateCurr.value.copy(currentWeek = Pair(newStart,newEnd))
        _uiStateNext.value = _uiStateNext.value.copy(currentWeek = Pair(newStart.afterDays(7),newEnd.afterDays(7)))
    }
    fun updateWeekPlans() {
        viewModelScope.launch {
            val weekSchedules = scheduleRepository.getSchedulesStream(uiStatePrev.value.currentWeek.first, uiStateNext.value.currentWeek.second)
            val weekTodos = todoRepository.getTodosStream(uiStatePrev.value.currentWeek.first, uiStateNext.value.currentWeek.second)
            val multipleDaySchedules = weekSchedules.first().filterNot { schedule ->
                isSameDay(schedule.startTime, schedule.endTime)
            }
            _uiStatePrev.update { currentState ->
                currentState.copy(
                    weekSchedules = weekSchedules.first(),
                    weekTodos = weekTodos.first(),
                    multipleDaySchedules = multipleDaySchedules
                )
            }
            _uiStateCurr.update { currentState ->
                currentState.copy(
                    weekSchedules = weekSchedules.first(),
                    weekTodos = weekTodos.first(),
                    multipleDaySchedules = multipleDaySchedules
                )
            }
            _uiStateNext.update { currentState ->
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
            val updatedTodos = _uiStateCurr.value.weekTodos.map {
                if (it.id == todo.id) updatedTodo else it
            }
            _uiStatePrev.value = _uiStatePrev.value.copy(weekTodos = updatedTodos)
            _uiStateCurr.value = _uiStateCurr.value.copy(weekTodos = updatedTodos)
            _uiStateNext.value = _uiStateNext.value.copy(weekTodos = updatedTodos)
        }
    }


}