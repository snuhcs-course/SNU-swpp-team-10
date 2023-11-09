package com.example.calendy.data.log

class LogTodoRepository(private val logTodoDao: LogTodoDao): ILogTodoRepository {
    override suspend fun insertLogTodo(logSchedule: LogTodo) {
        logTodoDao.insert(logSchedule)
    }

    override suspend fun updateLogTodo(logSchedule: LogTodo) {
        logTodoDao.update(logSchedule)
    }

    override suspend fun deleteLogTodo(logSchedule: LogTodo) {
        logTodoDao.delete(logSchedule)
    }

    override suspend fun getLogTodosByMessageId(messageId: Int): List<LogTodo> {
        return logTodoDao.getLogTodosByMessageId(messageId)
    }

    override suspend fun getAllLogTodos(): List<LogTodo> = logTodoDao.getAllLogTodos()
}