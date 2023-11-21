package com.example.calendy.data.maindb.history

import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.PlanType

interface IHistoryRepository {
    suspend fun insertHistory(managerHistory: ManagerHistory): Long

    suspend fun insertSavedPlanFromPlan(plan: Plan): Long
    fun getSavedPlansByMessageId(messageId: Int): List<ManagerHistory> // Will be deleted
    fun getRevisionHistoriesByMessageId(messageId: Int): List<Pair<PlanType, Pair<Int?, Int?>>>
    fun getSavedPlanById(savedPlanId: Int, planType: PlanType): Plan
}