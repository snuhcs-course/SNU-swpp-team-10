package com.example.calendy.data.plan

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.calendy.data.CalendyDatabase
import com.example.calendy.data.plan.schedule.ScheduleLocalDataSource
import com.example.calendy.data.plan.schedule.ScheduleRepository
import com.example.calendy.data.plan.todo.TodoLocalDataSource
import com.example.calendy.data.plan.todo.TodoRepository
import com.example.calendy.utils.DateHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before

import org.junit.Test

class PlanRepositoryTest {
    private lateinit var planRepository: PlanRepository
    private lateinit var todoRepository: TodoRepository
    private lateinit var scheduleRepository: ScheduleRepository
    private lateinit var calendyDatabase: CalendyDatabase


    @Before
    fun setUp() {
        fun createRepository() {
            val context: Context = ApplicationProvider.getApplicationContext()
            // Using an in-memory database because the information stored here disappears when the
            // process is killed.

            calendyDatabase = Room.inMemoryDatabaseBuilder(context, CalendyDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries().build()

            val todoDao = calendyDatabase.todoDao()
            val todoLocalDataSource = TodoLocalDataSource(todoDao)
            todoRepository = TodoRepository(todoLocalDataSource)

            val scheduleDao = calendyDatabase.scheduleDao()
            val scheduleLocalDataSource = ScheduleLocalDataSource(scheduleDao)
            scheduleRepository = ScheduleRepository(scheduleLocalDataSource)

            planRepository = PlanRepository(scheduleRepository, todoRepository)
        }

        createRepository()
    }

    @After
    fun tearDown() {
        // Close DB
        calendyDatabase.close()
    }


    private var schedule1 = Schedule(
        id = 1,
        title = "first",
        startTime = DateHelper.getDate(2023, 10, 9),
        endTime = DateHelper.getDate(2023, 10, 11),
        memo = "",
        priority = 1,
        showInMonthlyView = false,
        isOverridden = false
    )
    private var schedule2 = Schedule(
        id = 2,
        title = "second",
        startTime = DateHelper.getDate(2023, 10, 13, 12, 30),
        endTime = DateHelper.getDate(2023,11,1),
        memo = "",
        priority = 2,
        showInMonthlyView = false,
        isOverridden = false
    )

    private suspend fun addTwoSchedule() {
        scheduleRepository.insertSchedule(schedule1)
        scheduleRepository.insertSchedule(schedule2)
    }


    private var todo1 = Todo(
        id = 1, title = "Be happy",
        dueTime = DateHelper.getDate(
            year = 2023,
            monthZeroIndexed = 10,
            day = 9,
            hourOfDay = 20,
            minute = 30
        ),
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
        todoRepository.insertTodo(todo1)
    }

    @Test
    fun getPlansStream() = runBlocking {
        addTwoSchedule()
        addOneTodo()

        val planList: List<Plan> = planRepository.getPlansStream(
            DateHelper.getDate(year = 2023, monthZeroIndexed = 10, day = 5),
            DateHelper.getDate(year = 2023, monthZeroIndexed = 10, day = 12)
        ).first()
        Assert.assertEquals(planList.size, 3)
    }
}