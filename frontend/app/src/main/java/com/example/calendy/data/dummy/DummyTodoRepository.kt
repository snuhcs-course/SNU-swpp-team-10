package com.example.calendy.data.dummy

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.data.maindb.plan.todo.ITodoRepository
import com.example.calendy.data.maindb.plan.todo.TodoDao
import kotlinx.coroutines.flow.Flow
import java.util.Date

class DummyTodoDao : TodoDao {
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

    override fun getMonthlyTodosStream(startTime: Date, endTime: Date): Flow<List<Todo>> {
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

class DummyTodoRepository : ITodoRepository(DummyTodoDao()) {
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

    override fun getMonthlyTodosStream(startTime: Date, endTime: Date): Flow<List<Todo>> {
        TODO("Not yet implemented")
    }
}