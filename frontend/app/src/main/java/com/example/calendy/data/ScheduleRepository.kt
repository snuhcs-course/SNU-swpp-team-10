package com.example.calendy.data

import kotlinx.coroutines.flow.Flow
import java.util.Date

class ScheduleRepository(private val scheduleLocalDataSource: ScheduleLocalDataSource) : IScheduleRepository {
    override fun getSchedulesStream(startTime: Date, endTime: Date): Flow<List<Schedule>> {
        return scheduleLocalDataSource.getSchedulesByTime(startTime, endTime)
    }

    override suspend fun insertSchedule(schedule: Schedule) {
        scheduleLocalDataSource.insertSchedule(schedule)
    }

    override suspend fun deleteSchedule(schedule: Schedule) {
        scheduleLocalDataSource.deleteSchedule(schedule)
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        scheduleLocalDataSource.updateSchedule(schedule)
    }
}