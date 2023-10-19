package com.example.calendy.data.schedule

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

//    suspend fun postSchedule(schedule: Schedule) {
//        scheduleRemoteDataSource.postSchedule()
//    }
//
//    suspend fun getSchedule() {
//        return scheduleRemoteDataSource.getSchedule()
//    }
}