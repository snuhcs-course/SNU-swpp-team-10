package com.example.calendy.data.maindb.history

import com.example.calendy.data.maindb.plan.Plan

interface IHistoryRepository {
    suspend fun insertHistory(managerHistory: ManagerHistory): Long

    suspend fun insertSavedPlanFromPlan(plan: Plan): Long
    fun getSavedPlansByMessageId(messageId: Int): List<ManagerHistory>
}