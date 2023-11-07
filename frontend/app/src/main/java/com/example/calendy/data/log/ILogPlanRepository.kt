package com.example.calendy.data.log

interface ILogPlanRepository {
    suspend fun insertLogPlan(logPlan: LogPlan)
    suspend fun updateLogPlan(logPlan: LogPlan)
    suspend fun deleteLogPlan(logPlan: LogPlan)
    fun getLogPlansByMessageId(messageId: Int): List<LogPlan>
    fun getAllLogPlans(): List<LogPlan>
}