package com.swpp10.calendy.data.maindb.plan.todo

import androidx.sqlite.db.SupportSQLiteQuery
import com.swpp10.calendy.data.maindb.plan.Todo
import kotlinx.coroutines.flow.Flow
import java.util.Date

class TodoRepository(private val todoDao: TodoDao) : ITodoRepository(todoDao) {
    override fun getAllTodosStream(): Flow<List<Todo>> = todoDao.getAllTodosStream()

    override fun getTodosStream(startTime: Date, endTime: Date): Flow<List<Todo>> =
        todoDao.getTodosStream(startTime, endTime)

    override fun getMonthlyTodosStream(startTime: Date, endTime: Date): Flow<List<Todo>> =
        todoDao.getMonthlyTodosStream(startTime, endTime)

    override fun getTodoById(id: Int): Todo = todoDao.getTodoById(id)

    override fun getTodosViaQuery(query: SupportSQLiteQuery): List<Todo> =
        todoDao.getTodosViaQuery(query)

    override fun getTodosByIds(iDs: List<Int>): List<Todo> {
        return todoDao.getTodosByIds(iDs)
    }
}