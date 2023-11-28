package com.example.calendy.data.maindb.plan.schedule

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.BaseRepository
import com.example.calendy.data.maindb.plan.Schedule
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface IScheduleRepository : BaseRepository<Schedule> {
    fun getAllSchedulesStream(): Flow<List<Schedule>>
    fun getSchedulesStream(startTime: Date, endTime: Date): Flow<List<Schedule>>
    fun getScheduleById(id: Int): Schedule
    fun getSchedulesViaQuery(query: SupportSQLiteQuery): List<Schedule>
    fun getSchedulesByIds(ids: List<Int>): List<Schedule>
    fun getMonthlySchedulesStream(startTime: Date, endTime: Date): Flow<List<Schedule>>
}