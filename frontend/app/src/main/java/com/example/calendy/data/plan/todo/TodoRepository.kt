package com.example.calendy.data.plan.todo

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
}