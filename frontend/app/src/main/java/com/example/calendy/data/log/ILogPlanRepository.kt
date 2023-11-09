package com.example.calendy.data.log

interface ILogPlanRepository {
    suspend fun insertLogPlan(logPlan: LogPlan)
    suspend fun updateLogPlan(logPlan: LogPlan)
    suspend fun deleteLogPlan(logPlan: LogPlan)
    suspend fun getLogPlansByMessageId(messageId: Int): List<LogPlan>
    suspend fun getAllLogPlans(): List<LogPlan>
}