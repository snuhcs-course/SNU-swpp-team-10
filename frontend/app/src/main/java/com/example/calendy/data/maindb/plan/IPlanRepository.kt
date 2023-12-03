package com.example.calendy.data.maindb.plan

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.BaseRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface IPlanRepository {
    suspend fun insert(plan: Plan): Int
    suspend fun update(plan: Plan)
    suspend fun delete(plan: Plan)

    fun getAllPlansStream(): Flow<List<Plan>>
    fun getPlansStream(startTime: Date, endTime: Date): Flow<List<Plan>>
    fun getMonthlyPlansStream(startTime: Date, endTime: Date): Flow<List<Plan>>
    fun getPlanById(id: Int, type: PlanType): Plan
    fun getPlansByIds(scheduleIDs: List<Int>, todoIDs: List<Int>): List<Plan>
    fun getSchedulesViaQuery(query: SupportSQLiteQuery): List<Schedule>
    fun getTodosViaQuery(query: SupportSQLiteQuery): List<Todo>
}