package com.example.calendy.data

class TodoLocalDataSource(private val todoDao: TodoDao) {
    suspend fun insertTodo(todo: Todo) = todoDao.insert(todo)
    suspend fun deleteTodo(todo: Todo) = todoDao.delete(todo)
    suspend fun updateTodo(todo: Todo) = todoDao.update(todo)
    
    // TODO: Query 추가하기
}