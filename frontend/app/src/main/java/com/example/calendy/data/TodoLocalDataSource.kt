package com.example.calendy.data

import com.example.calendy.utils.DateHelper
import kotlinx.coroutines.flow.Flow
import java.util.Date

class TodoLocalDataSource(private val todoDao: TodoDao) {
    suspend fun insertTodo(todo: Todo) = todoDao.insert(todo)
    suspend fun deleteTodo(todo: Todo) = todoDao.delete(todo)
    suspend fun updateTodo(todo: Todo) = todoDao.update(todo)

    fun getTodosStream(startTime: Date, endTime: Date): Flow<List<Todo>> = todoDao.getTodosStream(startTime, endTime)

    // date == Date(year, 12, 31, 23(HOUR_OF_DAY), 59) && isYearly
    fun getYearlyTodosStream(year: Int): Flow<List<Todo>> =
            todoDao.getSpecialTodosStream(DateHelper.getYearlyDueTime(year), yearly = true)

    // date == Date(year, month, endDay, 23, 59) && isMonthly
    fun getMonthlyTodosStream(year: Int, month: Int): Flow<List<Todo>> =
            todoDao.getSpecialTodosStream(DateHelper.getMonthlyDueTime(year, month), monthly = true)

    // date == Date(year, month, day, 23, 59) && isDaily
    fun getDailyTodosStream(year: Int, month: Int, day: Int): Flow<List<Todo>> =
            todoDao.getSpecialTodosStream(DateHelper.getDailyDueTime(year, month, day), daily = true)
}