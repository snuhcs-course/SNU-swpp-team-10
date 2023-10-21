package com.example.calendy.data

import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.schedule.IScheduleRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

class DummyScheduleRepository: IScheduleRepository {
    override suspend fun insertSchedule(schedule: Schedule) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSchedule(schedule: Schedule) {
        TODO("Not yet implemented")
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        TODO("Not yet implemented")
    }

    override fun getSchedulesStream(startTime: Date, endTime: Date): Flow<List<Schedule>> {
        TODO("Not yet implemented")
    }

    override fun getScheduleById(id: Int): Flow<Schedule> {
        TODO("Not yet implemented")
    }

}