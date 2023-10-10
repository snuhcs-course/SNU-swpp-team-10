package com.example.calendy.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ScheduleDao : BaseDao<Schedule> {

    //todo add queries
    @Query("SELECT * FROM schedule WHERE start_time < :endTime OR end_time > :startTime")
    fun getScheduleByTime(startTime: Date, endTime: Date): Flow<List<Schedule>>

}