package com.example.calendy.data.plan.todo

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.BaseDao
import com.example.calendy.data.plan.Todo
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TodoDao : BaseDao<Todo> {
    // startTime, endTime inclusive
    @Query("SELECT * FROM todo WHERE due_time BETWEEN :startTime AND :endTime")
    fun getTodosStream(startTime: Date, endTime: Date): Flow<List<Todo>>

    // end_time > query_start && start_time < query_end
    @Query("SELECT * FROM todo WHERE due_time == :dueTime AND yearly == :yearly AND monthly == :monthly AND daily == :daily")
    fun getSpecialTodosStream(dueTime: Date, yearly: Boolean = false, monthly: Boolean = false, daily: Boolean = false): Flow<List<Todo>>

    @Query("SELECT * FROM todo WHERE id = :id")
    fun getTodoById(id: Int): Flow<Todo>

    @Query("SELECT * FROM todo")
    fun getAllTodo() : Flow<List<Todo>>

    @RawQuery
    fun getTodosViaQuery(query: SupportSQLiteQuery): List<Todo>
}