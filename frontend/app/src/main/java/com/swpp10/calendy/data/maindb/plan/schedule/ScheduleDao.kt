package com.swpp10.calendy.data.maindb.plan.schedule

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.swpp10.calendy.data.BaseDao
import com.swpp10.calendy.data.maindb.plan.Schedule
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ScheduleDao : BaseDao<Schedule> {
    @Query("SELECT * FROM schedule")
    fun getAllSchedulesStream(): Flow<List<Schedule>>

    @Query("SELECT * FROM schedule WHERE end_time >= :startTime AND start_time <= :endTime")
    fun getSchedulesStream(startTime: Date, endTime: Date): Flow<List<Schedule>>

    @Query("SELECT * FROM schedule WHERE id = :id")
    fun getScheduleById(id: Int): Schedule

    @RawQuery
    fun getSchedulesViaQuery(query: SupportSQLiteQuery): List<Schedule>

    @Query("SELECT * FROM schedule WHERE id IN (:id)")
    fun getSchedulesByIds(id: List<Int>): List<Schedule>
    @Query("SELECT * FROM schedule WHERE show_in_monthly_view > 0 AND end_time >= :startTime AND start_time <= :endTime")
    fun getMonthlySchedulesStream(startTime: Date, endTime: Date): Flow<List<Schedule>>
}