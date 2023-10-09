package com.example.calendy.data

import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val scheduleLocalDataSource: ScheduleLocalDataSource) : IScheduleRepository {
    override fun getSchedulesStream(): Flow<List<Schedule>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertSchedule(schedule: Schedule){}

    override suspend fun deleteSchedule(schedule: Schedule){}

    override suspend fun updateSchedule(schedule: Schedule){}
}