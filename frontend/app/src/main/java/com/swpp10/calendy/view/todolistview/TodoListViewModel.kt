package com.swpp10.calendy.view.todolistview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swpp10.calendy.data.maindb.category.ICategoryRepository
import com.swpp10.calendy.data.maindb.plan.Todo
import com.swpp10.calendy.data.maindb.plan.todo.ITodoRepository
import com.swpp10.calendy.data.maindb.repeatgroup.IRepeatGroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class TodoListViewModel(
    private val todoRepository: ITodoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoListUiState())
    val uiState: StateFlow<TodoListUiState> = _uiState.asStateFlow()

    fun updateYear(newYear: Int) {
        _uiState.value = _uiState.value.copy(year = newYear)
    }

    fun updateMonth(newMonth: Int) {
        _uiState.value = _uiState.value.copy(month = newMonth)
    }
    fun updateCompletionOfTodo(todo: Todo, complete: Boolean) {
        viewModelScope.launch {
            val updatedTodo = todo.copy(complete = complete)
            todoRepository.update(updatedTodo)
            val updatedTodos = _uiState.value.monthTodos.map {
                if (it.id == todo.id) updatedTodo else it
            }
            _uiState.value = _uiState.value.copy(monthTodos = updatedTodos)
        }
    }


    fun updateMonthTodos() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, uiState.value.year)
                set(Calendar.MONTH, uiState.value.month - 1) // Calendar.MONTH는 0부터 시작
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val start: Date = calendar.time // 월의 시작

            // 월의 마지막 날로 이동
            calendar.add(Calendar.MONTH, 1)
            calendar.add(Calendar.MILLISECOND, -1)
            val end: Date = calendar.time // 월의 마지막 날 23:59:59
            val monthTodos = todoRepository.getTodosStream(start, end)
            _uiState.update { currentState ->
                val sortedTodos = monthTodos.first().sortedBy { it.dueTime }
                currentState.copy(monthTodos = sortedTodos)
            }
        }
    }

    fun setHidedStatus(value: Boolean) {
        _uiState.value = _uiState.value.copy(hidedStatus = value)
    }

}