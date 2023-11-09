package com.example.calendy.data.plan.todo

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.plan.Todo
import kotlinx.coroutines.flow.Flow
import java.util.Date

// TODO: TodoRemoteDataSource 코드 추가
class TodoRepository(private val todoLocalDataSource: TodoLocalDataSource) : ITodoRepository {
    override suspend fun insertTodo(todo: Todo) = todoLocalDataSource.insertTodo(todo)

    override suspend fun deleteTodo(todo: Todo) = todoLocalDataSource.deleteTodo(todo)

    override suspend fun updateTodo(todo: Todo) = todoLocalDataSource.updateTodo(todo)

    override fun getTodosStream(startTime: Date, endTime: Date): Flow<List<Todo>> = todoLocalDataSource.getTodosStream(startTime, endTime)
    override fun getYearlyTodosStream(year: Int): Flow<List<Todo>> = todoLocalDataSource.getYearlyTodosStream(year)
    override fun getMonthlyTodosStream(year: Int, month: Int): Flow<List<Todo>> = todoLocalDataSource.getMonthlyTodosStream(year, month)
    override fun getDailyTodosStream(year: Int, month: Int, day: Int): Flow<List<Todo>> = todoLocalDataSource.getDailyTodosStream(year, month, day)
    override fun getTodoById(id: Int): Flow<Todo> = todoLocalDataSource.getTodoById(id)

    override fun getAllTodo() : Flow<List<Todo>> = todoLocalDataSource.getAllTodo()
    override fun getTodosViaQuery(query: SupportSQLiteQuery): List<Todo> =
        todoLocalDataSource.getTodosViaQuery(query)

    override suspend fun getTodosByIds(iDs: List<Int>): List<Todo> {
        return todoLocalDataSource.getTodosByIds(iDs)
    }
}