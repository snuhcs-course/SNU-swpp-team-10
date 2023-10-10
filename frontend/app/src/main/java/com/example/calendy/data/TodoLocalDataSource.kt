package com.example.calendy.data

import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.Date

class TodoLocalDataSource(private val todoDao: TodoDao) {
    suspend fun insertTodo(todo: Todo) = todoDao.insert(todo)
    suspend fun deleteTodo(todo: Todo) = todoDao.delete(todo)
    suspend fun updateTodo(todo: Todo) = todoDao.update(todo)

    fun getTodosStream(startTime: Date, endTime: Date): Flow<List<Todo>> = todoDao.getTodosStream(startTime, endTime)
    
    // TODO: 일단 Date의 constructor( ) 같은 extension function을 만들어놓자
    // TODO: 이 로직은 Todo 생성 페이지에서도 사용될 것이다. 그리고 Weekly View나 Monthly View에서도.
    private fun getYearlyDueTime(year: Int): Date =
            with(Calendar.getInstance()) {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, 11)
                set(Calendar.DATE, 31)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
                time
            }

    // month: 0 ~ 11 based.
    private fun getMonthlyDueTime(year: Int, month: Int): Date =
            with(Calendar.getInstance()) {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DATE, this.getActualMaximum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
                time
            }

    // month: 0 ~ 11 based.
    private fun getDailyDueTime(year: Int, month: Int, day: Int): Date =
            with(Calendar.getInstance()) {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DATE, day)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
                time
            }


    // date == Date(year, 12, 31, 23(HOUR_OF_DAY), 59) && isYearly
    fun getYearlyTodosStream(year: Int): Flow<List<Todo>> =
            todoDao.getSpecialTodosStream(getYearlyDueTime(year), yearly = true)

    // date == Date(year, month, endDay, 23, 59) && isMonthly
    fun getMonthlyTodosStream(year: Int, month: Int): Flow<List<Todo>> = todoDao.getSpecialTodosStream(getMonthlyDueTime(year, month), monthly = true)

    // date == Date(year, month, day, 23, 59) && isDaily
    fun getDailyTodosStream(year: Int, month: Int, day: Int): Flow<List<Todo>> = todoDao.getSpecialTodosStream(getDailyDueTime(year, month, day), daily = true)
}