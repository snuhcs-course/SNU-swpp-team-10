package com.swpp10.calendy.view.todolistview

import com.swpp10.calendy.data.maindb.plan.Todo
import java.util.Calendar

data class TodoListUiState(
    val monthTodos: List<Todo> = emptyList(),
    val hidedStatus: Boolean = false,
    val month: Int = Calendar.getInstance().get(Calendar.MONTH)+1,
    val year: Int = Calendar.getInstance().get(Calendar.YEAR)
)