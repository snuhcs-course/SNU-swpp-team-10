package com.example.calendy.data.maindb.plan.schedule

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.maindb.plan.Schedule
import kotlinx.coroutines.flow.Flow
import java.util.Date

class ScheduleRepository(private val scheduleDao: ScheduleDao) : IScheduleRepository(scheduleDao) {
    override fun getAllSchedulesStream(): Flow<List<Schedule>> = scheduleDao.getAllSchedulesStream()

    override fun getSchedulesStream(startTime: Date, endTime: Date): Flow<List<Schedule>> =
        scheduleDao.getSchedulesStream(startTime, endTime)
    override fun getMonthlySchedulesStream(startTime: Date, endTime: Date): Flow<List<Schedule>> =
        scheduleDao.getMonthlySchedulesStream(startTime, endTime)
    override fun getScheduleById(id: Int): Schedule = scheduleDao.getScheduleById(id)


    override fun getSchedulesViaQuery(query: SupportSQLiteQuery): List<Schedule> =
        scheduleDao.getSchedulesViaQuery(query)

    override fun getSchedulesByIds(ids: List<Int>): List<Schedule> {
        return scheduleDao.getSchedulesByIds(ids)
    }
}