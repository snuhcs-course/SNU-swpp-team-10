package com.example.calendy.data.plan.todo

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.calendy.data.maindb.CalendyDatabase
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.data.maindb.plan.todo.TodoRepository
import com.example.calendy.utils.DateHelper.getDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoRepositoryTest {
    private lateinit var todoRepository: TodoRepository
    private lateinit var todoDatabase: CalendyDatabase


    @Before
    fun setUp() {
        fun createRepository() {
            val context: Context = ApplicationProvider.getApplicationContext()
            // Using an in-memory database because the information stored here disappears when the
            // process is killed.

            todoDatabase = Room.inMemoryDatabaseBuilder(context, CalendyDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries().build()
            val todoDao = todoDatabase.todoDao()
            val todoLocalDataSource = TodoLocalDataSource(todoDao)
            todoRepository = TodoRepository(todoLocalDataSource)
        }

        createRepository()
    }

    @After
    fun tearDown() {
        // Close DB
        todoDatabase.close()
    }


    private var todo1 = Todo(
        id = 1, title = "Be happy",
        dueTime = getDate(year = 2023, monthZeroIndexed = 10, day = 9, hourOfDay = 20, minute = 30),
        yearly = false,
        monthly = false,
        daily = false,
        complete = false,
        memo = "Realy",
        priority = 2,
        showInMonthlyView = false,
        isOverridden = false,
    )

    private suspend fun addOneTodo() {
        todoRepository.insert(todo1)
    }

    @Test
    fun insertTodo() = runBlocking {
        addOneTodo()
        val todoList: List<Todo> = todoRepository.getTodosStream(
            getDate(year = 2023, monthZeroIndexed = 10, day = 5),
            getDate(year = 2023, monthZeroIndexed = 10, day = 12)
        ).first()
        assertEquals(todoList.size, 1)
        assertEquals(todoList.first(), todo1)
    }

    @Test
    fun deleteTodo() {
    }

    @Test
    fun updateTodo() {
    }


    @Test
    fun repositoryQuery_emptyDB() = runBlocking {
        val todoList: List<Todo> = todoRepository.getTodosStream(
            getDate(year = 2023, monthZeroIndexed = 10, day = 5),
            getDate(year = 2023, monthZeroIndexed = 10, day = 12)
        ).first()
        assertEquals(todoList.size, 0)
    }

    @Test
    fun getTodosStream() {
    }

    @Test
    fun getYearlyTodosStream() {
    }

    @Test
    fun getMonthlyTodosStream() {
    }

    @Test
    fun getDailyTodosStream() {
    }

    @Test
    fun getTodoById() {
    }
}