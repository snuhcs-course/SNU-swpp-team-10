package com.example.calendy.data

import kotlinx.coroutines.flow.Flow

// TODO: TodoRemoteDataSource 코드 추가
class TodoRepository(private val todoLocalDataSource: TodoLocalDataSource) : ITodoRepository {
    override suspend fun insertTodo(todo: Todo) {
        todoLocalDataSource.insertTodo(todo)
    }

    override suspend fun deleteTodo(todo: Todo) {
        todoLocalDataSource.deleteTodo(todo)
    }

    override suspend fun updateTodo(todo: Todo) {
        todoLocalDataSource.updateTodo(todo)
    }

    override fun getTodosStream(): Flow<List<Todo>> {
        TODO("Not yet implemented")
    }
}