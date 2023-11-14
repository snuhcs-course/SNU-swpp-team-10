package com.example.calendy.view.todolistview

import com.example.calendy.data.maindb.plan.Todo

data class TodoListUiState(
    val monthTodos: List<Todo> = emptyList(),
    val hidedStatus: Boolean = false,
    val month: Int = Calendar.getInstance().get(Calendar.MONTH)+1,
    val year: Int = Calendar.getInstance().get(Calendar.YEAR)
)