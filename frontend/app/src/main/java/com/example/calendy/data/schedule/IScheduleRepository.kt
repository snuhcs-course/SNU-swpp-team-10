package com.example.calendy.data.schedule

import com.example.calendy.data.schedule.Schedule
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface IScheduleRepository {
    suspend fun insertSchedule(schedule: Schedule)
    suspend fun deleteSchedule(schedule: Schedule)
    suspend fun updateSchedule(schedule: Schedule)
    fun getSchedulesStream(startTime: Date, endTime: Date): Flow<List<Schedule>>
    fun getScheduleById(id: Int): Flow<Schedule>
}