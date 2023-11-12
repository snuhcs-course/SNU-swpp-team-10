package com.example.calendy.data.dummy

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.schedule.IScheduleRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

class DummyScheduleRepository : IScheduleRepository {
    override fun getAllSchedulesStream(): Flow<List<Schedule>> {
        TODO("Not yet implemented")
    }

    override fun getSchedulesStream(startTime: Date, endTime: Date): Flow<List<Schedule>> {
        TODO("Not yet implemented")
    }

    override fun getScheduleById(id: Int): Schedule {
        TODO("Not yet implemented")
    }

    override fun getSchedulesViaQuery(query: SupportSQLiteQuery): List<Schedule> {
        TODO("Not yet implemented")
    }

    override fun getSchedulesByIds(ids: List<Int>): List<Schedule> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(entity: Schedule): Long {
        TODO("Not yet implemented")
    }

    override suspend fun update(entity: Schedule) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(entity: Schedule) {
        TODO("Not yet implemented")
    }

}