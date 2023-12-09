package com.swpp10.calendy.data.maindb.rawplan

import androidx.room.Dao
import androidx.room.Query
import com.swpp10.calendy.data.BaseDao

@Dao
interface RawScheduleDao : BaseDao<RawSchedule> {
    @Query("DELETE FROM raw_schedule")
    fun deleteAllRawSchedules()

    @Query("SELECT * FROM raw_schedule")
    fun getAllRawSchedules(): List<RawSchedule>
}