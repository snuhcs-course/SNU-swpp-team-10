package com.example.calendy.view.todolistview

import com.example.calendy.data.plan.Todo

data class TodoListUiState(
    val dayTodos: List<Todo> = emptyList(),
    val isLoading: Boolean = false
)