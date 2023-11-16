package com.example.calendy.data.plan

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.core.app.ApplicationProvider
import com.example.calendy.data.maindb.CalendyDatabase
import com.example.calendy.data.maindb.category.CategoryRepository
import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.PlanRepository
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.data.maindb.plan.schedule.ScheduleRepository
import com.example.calendy.data.maindb.plan.todo.TodoRepository
import com.example.calendy.data.maindb.repeatgroup.RepeatGroupRepository
import com.example.calendy.utils.DateHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class PlanRepositoryTest {
    private lateinit var planRepository: PlanRepository
    private lateinit var calendyDatabase: CalendyDatabase


    // Schedule: 10.9 - 10.9, 10.13 - 10.15
    // Tod0: 10.9, 10.12 (Actually November)
    //region TestData
    private val schedule1 = Schedule(
        id = 1, title = "first", startTime = DateHelper.getDate(
            year = 2023, monthZeroIndexed = 10, day = 9, hourOfDay = 16, minute = 0
        ), endTime = DateHelper.getDate(
            year = 2023, monthZeroIndexed = 10, day = 9, hourOfDay = 20, minute = 30
        ), memo = "", priority = 1, showInMonthlyView = false, isOverridden = false
    )
    private val schedule2 = Schedule(
        id = 2,
        title = "second",
        startTime = DateHelper.getDate(
            year = 2023, monthZeroIndexed = 10, day = 13, hourOfDay = 12, minute = 30
        ),
        endTime = DateHelper.getDate(year = 2023, monthZeroIndexed = 10, day = 15),
        memo = "",
        priority = 2,
        showInMonthlyView = false,
        isOverridden = false
    )


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
    private val todo2 = Todo(
        id = 2, title = "Test This",
        dueTime = DateHelper.getDate(
            year = 2023, monthZeroIndexed = 10, day = 12, hourOfDay = 20, minute = 30
        ),
        complete = true,
        memo = "Second",
        priority = 4,
        showInMonthlyView = false,
        isOverridden = false,
    )

    private suspend fun addTwoSchedule() {
        planRepository.insert(schedule1)
        planRepository.insert(schedule2)
    }


    private suspend fun addTwoTodo() {
        planRepository.insert(todo1)
        planRepository.insert(todo2)
    }
    //endregion

    // TODO: Should be general for other tests
    fun <T> assertEqualsWithoutOrder(expected: Collection<T>, actual: Collection<T>) {
        val shouldNotBeInActual = actual - expected.toSet()
        val missingInActual = expected - actual.toSet()

        if (shouldNotBeInActual.isNotEmpty() || missingInActual.isNotEmpty()) {
            val errorMessage = buildString {
                appendLine("Assertion failed!")
                if (missingInActual.isNotEmpty()) {
                    appendLine("Missing from actual: $missingInActual")
                }
                if (shouldNotBeInActual.isNotEmpty()) {
                    appendLine("Should not be present: $shouldNotBeInActual")
                }
            }
            fail(errorMessage)
        }
    }

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
            val todoRepository = TodoRepository(todoDao)

            val scheduleDao = calendyDatabase.scheduleDao()
            val scheduleRepository = ScheduleRepository(scheduleDao)

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

        runBlocking {
            addTwoSchedule()
            addTwoTodo()
        }
    }

    @After
    fun tearDown() {
        // Close DB
        calendyDatabase.close()
    }

    @Test
    fun update() = runBlocking {
        val updatedSchedule = schedule1.copy(title = "updated", priority = 5, memo = "updated memo")
        planRepository.update(updatedSchedule)

        // Then
        val actual = planRepository.getPlanById(id = 1, PlanType.SCHEDULE)
        assertEquals(updatedSchedule, actual)


        val updatedTodo = todo1.copy(title = "updated", priority = 5, memo = "updated memo")
        planRepository.update(updatedTodo)

        // Then
        val actual2 = planRepository.getPlanById(id = 1, PlanType.TODO)
        assertEquals(updatedTodo, actual2)
    }

    @Test
    fun delete() = runBlocking {
        planRepository.delete(schedule1)
        planRepository.delete(todo1)

        // Then
        val actual = planRepository.getAllPlansStream().first()
        val expected = listOf(schedule2, todo2)
        assertEqualsWithoutOrder(expected = expected, actual = actual)
    }

    //region GET
    @Test
    fun getAllPlansStream() = runBlocking {
        val planList: List<Plan> = planRepository.getAllPlansStream().first()

        val expectedList = listOf(schedule1, schedule2, todo1, todo2)
        assertEqualsWithoutOrder(expected = expectedList, actual = planList)
    }

    @Test
    fun getPlansStream() = runBlocking {
//        val planList: List<Plan> = planRepository.getPlansStream(
//            DateHelper.getDate(year = 2023, monthZeroIndexed = 10, day = 5),
//            DateHelper.getDate(year = 2023, monthZeroIndexed = 10, day = 11)
//        ).first()
//
//        val expectedList = listOf(schedule1, todo1)
//        assertEqualsWithoutOrder(expected = expectedList, actual = planList)
    }

    @Test
    fun getPlanById() = runBlocking {
        val plan1: Plan = planRepository.getPlanById(id = 1, type = PlanType.SCHEDULE)
        assertEquals(schedule1, plan1)

        val plan2: Plan = planRepository.getPlanById(id = 2, type = PlanType.SCHEDULE)
        assertEquals(schedule2, plan2)

        val plan3: Plan = planRepository.getPlanById(id = 1, type = PlanType.TODO)
        assertEquals(todo1, plan3)
    }

    @Test
    fun getPlanByIds() = runBlocking {
        val planList: List<Plan> = planRepository.getPlansByIds(
            scheduleIDs = listOf(1), todoIDs = listOf(1)
        )

        val expectedList = listOf(schedule1, todo1)
        assertEqualsWithoutOrder(expected = expectedList, actual = planList)
    }

    @Test
    fun getSchedulesViaQuery() = runBlocking {
        val calendySelectQuery = SimpleSQLiteQuery(
            "SELECT * FROM schedule WHERE id=1",
        )
        val planList: List<Schedule> = planRepository.getSchedulesViaQuery(calendySelectQuery)

        val expected = listOf(schedule1)
        assertEqualsWithoutOrder(expected = expected, actual = planList)
    }

    @Test
    fun getTodosViaQuery() = runBlocking {
        val calendySelectQuery = SimpleSQLiteQuery(
            "SELECT * FROM todo WHERE id=2",
        )
        val planList: List<Todo> = planRepository.getTodosViaQuery(calendySelectQuery)

        val expected = listOf(todo2)
        assertEqualsWithoutOrder(expected = expected, actual = planList)
    }
    //endregion
}