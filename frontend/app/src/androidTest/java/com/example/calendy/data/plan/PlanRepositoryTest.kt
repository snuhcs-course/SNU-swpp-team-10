package com.example.calendy.data.plan

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.calendy.data.maindb.CalendyDatabase
import com.example.calendy.data.maindb.category.CategoryRepository
import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.PlanRepository
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.data.maindb.plan.schedule.ScheduleRepository
import com.example.calendy.data.maindb.plan.todo.TodoRepository
import com.example.calendy.data.maindb.repeatgroup.RepeatGroupRepository
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
            todoRepository = TodoRepository(todoDao)

            val scheduleDao = calendyDatabase.scheduleDao()
            scheduleRepository = ScheduleRepository(scheduleDao)

            val categoryDao = calendyDatabase.categoryDao()
            val categoryRepository = CategoryRepository(categoryDao)

            val repeatGroupDao = calendyDatabase.repeatGroupDao()
            val repeatGroupRepository = RepeatGroupRepository(repeatGroupDao)

            planRepository = PlanRepository(
                scheduleRepository = scheduleRepository,
                todoRepository = todoRepository,
                categoryRepository = categoryRepository,
                repeatGroupRepository = repeatGroupRepository
            )
        }

        createRepository()
    }

    @After
    fun tearDown() {
        // Close DB
        calendyDatabase.close()
    }


    private val schedule1 = Schedule(
        id = 1,
        title = "first",
        startTime = DateHelper.getDate(2023, 10, 9),
        endTime = DateHelper.getDate(2023, 10, 11),
        memo = "",
        priority = 1,
        showInMonthlyView = false,
        isOverridden = false
    )
    private val schedule2 = Schedule(
        id = 2,
        title = "second",
        startTime = DateHelper.getDate(2023, 10, 13, 12, 30),
        endTime = DateHelper.getDate(2023, 11, 1),
        memo = "",
        priority = 2,
        showInMonthlyView = false,
        isOverridden = false
    )

    private suspend fun addTwoSchedule() {
        scheduleRepository.insert(schedule1)
        scheduleRepository.insert(schedule2)
    }


    private val todo1 = Todo(
        id = 1, title = "Be happy",
        dueTime = DateHelper.getDate(
            year = 2023, monthZeroIndexed = 10, day = 9, hourOfDay = 20, minute = 30
        ),
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