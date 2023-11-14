package com.example.calendy.data.dummy

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.data.maindb.plan.todo.ITodoRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

class DummyTodoRepository : ITodoRepository {
    override fun getAllTodosStream(): Flow<List<Todo>> {
        TODO("Not yet implemented")
    }

    override fun getTodosStream(startTime: Date, endTime: Date): Flow<List<Todo>> {
        TODO("Not yet implemented")
    }

    override fun getTodoById(id: Int): Todo {
        TODO("Not yet implemented")
    }

    override fun getTodosViaQuery(query: SupportSQLiteQuery): List<Todo> {
        TODO("Not yet implemented")
    }

    override fun getTodosByIds(iDs: List<Int>): List<Todo> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(entity: Todo): Long {
        TODO("Not yet implemented")
    }

    override suspend fun update(entity: Todo) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(entity: Todo) {
        TODO("Not yet implemented")
    }
}