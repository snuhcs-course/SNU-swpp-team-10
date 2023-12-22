package com.swpp10.calendy.data.maindb.plan.schedule

import androidx.sqlite.db.SupportSQLiteQuery
import com.swpp10.calendy.data.BaseRepository
import com.swpp10.calendy.data.maindb.plan.Schedule
import kotlinx.coroutines.flow.Flow
import java.util.Date

abstract class IScheduleRepository(scheduleDao: ScheduleDao) : BaseRepository<Schedule>(scheduleDao) {
    abstract fun getAllSchedulesStream(): Flow<List<Schedule>>
    abstract fun getSchedulesStream(startTime: Date, endTime: Date): Flow<List<Schedule>>
    abstract fun getScheduleById(id: Int): Schedule
    abstract fun getSchedulesViaQuery(query: SupportSQLiteQuery): List<Schedule>
    abstract fun getSchedulesByIds(ids: List<Int>): List<Schedule>
    abstract fun getMonthlySchedulesStream(startTime: Date, endTime: Date): Flow<List<Schedule>>
}