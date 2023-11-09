package com.example.calendy.data.plan

import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface IPlanRepository {
    fun getPlansStream(startTime: Date, endTime: Date): Flow<List<Plan>>
    fun getPlanById(id: Int, type: Plan.PlanType): Flow<Plan>

    fun getAllPlans():Flow<List<Plan>>

    suspend fun updatePlan(plan: Plan)
    suspend fun insertPlan(plan: Plan)
    suspend fun deletePlan(plan: Plan)
    fun getSchedulesViaQuery(query: SupportSQLiteQuery): List<Schedule>
    fun getTodosViaQuery(query: SupportSQLiteQuery): List<Todo>
    //TODO : check if this suspend is validn
    suspend fun getPlansByIds(scheduelIDs: List<Int>, todoIDs: List<Int>): List<Plan>
}