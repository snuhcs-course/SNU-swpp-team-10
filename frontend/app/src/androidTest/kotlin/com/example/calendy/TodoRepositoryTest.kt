package com.example.calendy

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.calendy.data.CalendyDatabase
import com.example.calendy.data.todo.Todo
import com.example.calendy.data.todo.TodoLocalDataSource
import com.example.calendy.data.todo.TodoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Calendar
import java.util.Date

@RunWith(AndroidJUnit4::class)
class TodoRepositoryTest {
    private lateinit var todoRepository: TodoRepository
    private lateinit var todoDatabase: CalendyDatabase

    @Before
    fun createRepository() {
        Log.d("GUN", "CreateRepository")
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        Log.d("GUN", "After Context")

        todoDatabase = Room.inMemoryDatabaseBuilder(context, CalendyDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        Log.d("GUN", "DB Builder")
        Log.d("GUN", todoDatabase.isOpen.toString())
        val todoDao = todoDatabase.todoDao()
        val todoLocalDataSource = TodoLocalDataSource(todoDao)
        todoRepository = TodoRepository(todoLocalDataSource)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        Log.d("GUN", "CloseDB")
        todoDatabase.close()
    }

    fun makeDate(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, second: Int = 0, millisecond: Int = 0): Date = with(Calendar.getInstance()) {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DATE, day)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, second)
        set(Calendar.MILLISECOND, millisecond)
        time
    }

    private var todo1 = Todo(
            id= 1, userId = 0, title = "Be happy",
            dueTime = makeDate(2023, 10, 9, 20, 30),
            yearly = false,
            monthly = false,
            daily = false,
            complete = false,
            memo = "Realy",
            repeatGroupId = 0,
            categoryId = 0,
            priority = 0,
            showInMonthlyView = false,
            isOverridden = false,
    )

    private suspend fun addOneTodo() {
        todoRepository.insertTodo(todo1)
    }

    @Test
    @Throws(Exception::class)
    fun repositoryQuery_emptyDB() = runBlocking {
        val todoList: List<Todo> = todoRepository.getTodosStream(makeDate(2023, 10, 5), makeDate(2023, 10, 12))
                .first()
        assertEquals(todoList.size, 0)
    }

    @Test
    @Throws(Exception::class)
    fun repositoryInsert_insertTodoAndFind() = runBlocking {
        addOneTodo()
        val todoList: List<Todo> = todoRepository.getTodosStream(makeDate(2023, 10, 5), makeDate(2023, 10, 12))
                .first()
        assertEquals(todoList.size, 1)
        assertEquals(todoList.first(), todo1)
    }
}