package com.example.calendy.data.plan.schedule

import com.example.calendy.data.plan.Schedule
import kotlinx.coroutines.flow.Flow
import java.util.Date

class ScheduleLocalDataSource(private val scheduleDao: ScheduleDao) {
    suspend fun insertSchedule(schedule: Schedule) {
        scheduleDao.insert(schedule)
    }
    suspend fun updateSchedule(schedule: Schedule) {
        scheduleDao.update(schedule)
    }
    suspend fun deleteSchedule(schedule: Schedule) {
        scheduleDao.delete(schedule)
    }
    fun getSchedulesStream(startTime: Date, endTime: Date): Flow<List<Schedule>> {
        return scheduleDao.getSchedulesStream(startTime, endTime)
    }
    fun getScheduleById(id: Int): Flow<Schedule> {
        return scheduleDao.getScheduleById(id)
    }

    fun getAllSchedule():Flow<List<Schedule>> = scheduleDao.getAllSchedule()
}