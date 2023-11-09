package com.example.calendy.data.log

interface ILogTodoRepository {
    suspend fun insertLogTodo(logSchedule: LogTodo)
    suspend fun updateLogTodo(logSchedule: LogTodo)
    suspend fun deleteLogTodo(logSchedule: LogTodo)
    suspend fun getLogTodosByMessageId(messageId: Int): List<LogTodo>
    suspend fun getAllLogTodos(): List<LogTodo>
}