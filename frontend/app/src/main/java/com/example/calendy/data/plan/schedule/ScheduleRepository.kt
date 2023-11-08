package com.example.calendy.data.plan.schedule

import androidx.navigation.FloatingWindow
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.plan.Schedule
import kotlinx.coroutines.flow.Flow
import java.util.Date

class ScheduleRepository(private val scheduleLocalDataSource: ScheduleLocalDataSource) :
    IScheduleRepository {
    override suspend fun insertSchedule(schedule: Schedule) {
        scheduleLocalDataSource.insertSchedule(schedule)
    }

    override suspend fun deleteSchedule(schedule: Schedule) {
        scheduleLocalDataSource.deleteSchedule(schedule)
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        scheduleLocalDataSource.updateSchedule(schedule)
    }

    override fun getSchedulesStream(startTime: Date, endTime: Date): Flow<List<Schedule>> {
        return scheduleLocalDataSource.getSchedulesStream(startTime, endTime)
    }

    override fun getScheduleById(id: Int): Flow<Schedule> {
        return scheduleLocalDataSource.getScheduleById(id)
    }

    override fun getAllSchedule(): Flow<List<Schedule>> = scheduleLocalDataSource.getAllSchedule()

    override fun getSchedulesViaQuery(query: SupportSQLiteQuery): List<Schedule> =
        scheduleLocalDataSource.getSchedulesViaQuery(query)


//    suspend fun postSchedule(schedule: Schedule) {
//        scheduleRemoteDataSource.postSchedule()
//    }
//
//    suspend fun getSchedule() {
//        return scheduleRemoteDataSource.getSchedule()
//    }
}