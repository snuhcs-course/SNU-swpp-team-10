package com.example.calendy.data.maindb.history

import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo

class HistoryRepository(
    private val managerHistoryDao: ManagerHistoryDao,
    private val savedScheduleDao: SavedScheduleDao,
    private val savedTodoDao: SavedTodoDao
) : IHistoryRepository {
    override suspend fun insertHistory(managerHistory: ManagerHistory): Long =
        managerHistoryDao.insert(managerHistory)

    override suspend fun insertSavedPlanFromPlan(plan: Plan): Long = when(plan) {
        is Schedule -> savedScheduleDao.insert(plan.toSavedSchedule())
        is Todo     -> savedTodoDao.insert(plan.toSavedTodo())
    }

    override fun getSavedPlansByMessageId(messageId: Int): List<ManagerHistory> = managerHistoryDao.getHistoriesByMessageId(messageId)
}