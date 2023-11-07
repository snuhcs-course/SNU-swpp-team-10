package com.example.calendy.data.log

import androidx.room.Dao
import androidx.room.Query
import com.example.calendy.data.BaseDao

@Dao
interface LogTodoDao : BaseDao<LogTodo> {
    @Query("SELECT * FROM log_todo WHERE message_id = :messageId")
    fun getLogTodosByMessageId(messageId: Int): List<LogTodo>

    @Query("SELECT * FROM log_todo")
    fun getAllLogTodos(): List<LogTodo>
}