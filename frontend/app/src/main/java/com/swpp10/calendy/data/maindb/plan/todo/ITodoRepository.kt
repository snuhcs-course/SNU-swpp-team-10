package com.swpp10.calendy.data.maindb.plan.todo

import androidx.sqlite.db.SupportSQLiteQuery
import com.swpp10.calendy.data.BaseRepository
import com.swpp10.calendy.data.maindb.plan.Todo
import kotlinx.coroutines.flow.Flow
import java.util.Date

abstract class ITodoRepository(todoDao: TodoDao) : BaseRepository<Todo>(todoDao) {
    abstract fun getAllTodosStream(): Flow<List<Todo>>
    abstract fun getTodosStream(startTime: Date, endTime: Date): Flow<List<Todo>>
    abstract fun getTodoById(id: Int): Todo
    abstract fun getTodosViaQuery(query: SupportSQLiteQuery): List<Todo>
    abstract fun getTodosByIds(iDs: List<Int>): List<Todo>
    abstract fun getMonthlyTodosStream(startTime: Date, endTime: Date): Flow<List<Todo>>
}