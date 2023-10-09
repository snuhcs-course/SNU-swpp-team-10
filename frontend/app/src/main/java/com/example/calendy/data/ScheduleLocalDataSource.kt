package com.example.calendy.data

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

    fun getSchedulesByTime(startTime: Date, endTime: Date): Flow<List<Schedule>> {
        return scheduleDao.getScheduleByTime(startTime, endTime)
    }



}