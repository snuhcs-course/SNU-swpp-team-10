package com.example.calendy.data.rawsqldb

import androidx.room.Dao
import androidx.room.Query
import com.example.calendy.data.BaseDao

@Dao
interface RawSqlScheduleDao : BaseDao<RawSqlSchedule> {
    @Query("DELETE FROM schedule")
    fun deleteAllSchedules()

    @Query("SELECT * FROM schedule")
    fun getAllSchedules(): List<RawSqlSchedule>
}