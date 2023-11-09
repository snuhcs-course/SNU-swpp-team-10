package com.example.calendy.view.todolistview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendy.data.category.ICategoryRepository
import com.example.calendy.data.plan.Todo
import com.example.calendy.data.plan.todo.ITodoRepository
import com.example.calendy.data.repeatgroup.IRepeatGroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class TodoListViewModel(
    private val todoRepository: ITodoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoListUiState())
    val uiState: StateFlow<TodoListUiState> = _uiState.asStateFlow()

    fun getTodosForDate(date: Date): Flow<List<Todo>> {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start: Date = calendar.time // beginning of the day

        // Move the calendar to the end of the day
        calendar.add(Calendar.DATE, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val end: Date = calendar.time // This represents 23:59:59 of the current day

        return todoRepository.getTodosStream(start, end)
    }

    fun updateCompletionOfTodo(todo: Todo) {
        viewModelScope.launch{
            todoRepository.updateTodo(todo.copy(complete = !todo.complete))
        }
    }


}