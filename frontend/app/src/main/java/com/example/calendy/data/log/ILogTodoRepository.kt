package com.example.calendy.data.log

interface ILogTodoRepository {
    suspend fun insertLogTodo(logSchedule: LogTodo)
    suspend fun updateLogTodo(logSchedule: LogTodo)
    suspend fun deleteLogTodo(logSchedule: LogTodo)
    fun getLogTodosByMessageId(messageId: Int): List<LogTodo>
    fun getAllLogTodos(): List<LogTodo>
}