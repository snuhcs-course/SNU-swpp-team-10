package com.example.calendy.data.plan.schedule

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.calendy.data.CalendyDatabase
import com.example.calendy.data.plan.Schedule
import com.example.calendy.utils.DateHelper
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScheduleRepositoryTest {
    private lateinit var scheduleDatabase: CalendyDatabase
    private lateinit var scheduleRepository: ScheduleRepository

    @Before
    fun setUp() {
        fun createDb() {
            val context: Context = ApplicationProvider.getApplicationContext()
            scheduleDatabase = Room.inMemoryDatabaseBuilder(context, CalendyDatabase::class.java)
                .allowMainThreadQueries()
                .build()
            val scheduleDao = scheduleDatabase.scheduleDao()
            val scheduleLocalDataSource = ScheduleLocalDataSource(scheduleDao)
            scheduleRepository = ScheduleRepository(scheduleLocalDataSource)
        }
        createDb()
    }

    @After
    fun tearDown() {
        // Database close
        scheduleDatabase.close()
    }

    @Test
    fun insertSchedule() {
    }

    @Test
    fun deleteSchedule() {
    }

    @Test
    fun updateSchedule() {
    }

    @Test
    fun getSchedulesStream() {
    }

    @Test
    fun getScheduleById() {
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
        endTime = DateHelper.getDate(2023, 11, 1),
        memo = "",
        priority = 2,
        showInMonthlyView = false,
        isOverridden = false
    )

    private suspend fun addTwoItemToDb() {
        scheduleRepository.insertSchedule(schedule1)
        scheduleRepository.insertSchedule(schedule2)
    }


    @Test
    @Throws(Exception::class)
    fun daoInsert_test() = runBlocking {
        addTwoItemToDb()
        var allSchedules = scheduleRepository.getSchedulesStream(DateHelper.getDate(
            2023,
            10,
            9,
            12
        ), DateHelper.getDate(2023, 10, 10)).first()
        TestCase.assertEquals(allSchedules[0], schedule1)
        var scheduleId1 = scheduleRepository.getScheduleById(1)
        TestCase.assertEquals(scheduleId1.first(), schedule1)
    }
}