package com.example.calendy.data

import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ITodoRepository {
    suspend fun insertTodo(todo: Todo)
    suspend fun deleteTodo(todo: Todo)
    suspend fun updateTodo(todo: Todo)

    fun getTodosStream(startTime: Date, endTime: Date): Flow<List<Todo>>
    fun getYearlyTodosStream(year: Int): Flow<List<Todo>>
    fun getMonthlyTodosStream(year: Int, month: Int): Flow<List<Todo>>
    fun getDailyTodosStream(year: Int, month: Int, day: Int): Flow<List<Todo>>
}