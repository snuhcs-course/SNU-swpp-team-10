package com.example.calendy.data.schedule

import androidx.room.Dao
import androidx.room.Query
import com.example.calendy.data.BaseDao
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ScheduleDao : BaseDao<Schedule> {
    @Query("SELECT * FROM schedule WHERE start_time < :endTime OR end_time > :startTime")
    fun getSchedulesStream(startTime: Date, endTime: Date): Flow<List<Schedule>>
    @Query("SELECT * FROM schedule WHERE id = :id")
    fun getScheduleById(id: Int): Flow<Schedule>
}