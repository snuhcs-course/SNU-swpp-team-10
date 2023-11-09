package com.example.calendy.data.log

import androidx.room.Dao
import androidx.room.Query
import com.example.calendy.data.BaseDao

@Dao
interface LogScheduleDao : BaseDao<LogSchedule> {
    @Query("SELECT * FROM log_schedule WHERE message_id = :messageId")
    suspend fun getLogSchedulesByMessageId(messageId: Int): List<LogSchedule>

    @Query("SELECT * FROM log_schedule")
    suspend fun getAllLogSchedules(): List<LogSchedule>
}