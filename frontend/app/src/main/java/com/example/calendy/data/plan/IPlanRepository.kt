package com.example.calendy.data.plan

import kotlinx.coroutines.flow.Flow
import java.util.Date

interface IPlanRepository {
    fun getPlansStream(startTime: Date, endTime: Date): Flow<List<Plan>>
    fun getPlanById(id: Int, type: Plan.PlanType): Flow<Plan>

    fun getAllPlans():Flow<List<Plan>>

    suspend fun updatePlan(plan: Plan)
}