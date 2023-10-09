package com.example.calendy.data

import kotlinx.coroutines.flow.Flow

interface IScheduleRepository {

    fun getSchedulesStream(): Flow<List<Schedule>>

    suspend fun insertSchedule(schedule: Schedule)

    suspend fun deleteSchedule(schedule: Schedule)

    suspend fun updateSchedule(schedule: Schedule)
}