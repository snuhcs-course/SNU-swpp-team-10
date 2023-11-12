package com.example.calendy.data.maindb.plan.todo

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.BaseRepository
import com.example.calendy.data.maindb.plan.Todo
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ITodoRepository : BaseRepository<Todo> {
    fun getAllTodosStream(): Flow<List<Todo>>
    fun getTodosStream(startTime: Date, endTime: Date): Flow<List<Todo>>
    fun getTodoById(id: Int): Todo
    fun getTodosViaQuery(query: SupportSQLiteQuery): List<Todo>
    fun getTodosByIds(iDs: List<Int>): List<Todo>
}