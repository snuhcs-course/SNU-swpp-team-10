package com.example.calendy.data

import kotlinx.coroutines.flow.Flow

interface ITodoRepository {
    suspend fun insertTodo(todo: Todo)
    suspend fun deleteTodo(todo: Todo)
    suspend fun updateTodo(todo: Todo)

    // TODO: filter
    fun getTodosStream(): Flow<List<Todo>>
}