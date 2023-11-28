package com.example.calendy.data.maindb.history

import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo

class HistoryRepository(
    private val managerHistoryDao: ManagerHistoryDao,
    private val savedScheduleDao: SavedScheduleDao,
    private val savedTodoDao: SavedTodoDao
) : IHistoryRepository {
    override suspend fun insertHistory(managerHistory: ManagerHistory): Long =
        managerHistoryDao.insert(managerHistory)

    override suspend fun insertSavedPlanFromPlan(plan: Plan): Long = when (plan) {
        is Schedule -> savedScheduleDao.insert(plan.toSavedSchedule())
        is Todo     -> savedTodoDao.insert(plan.toSavedTodo())
    }

    override fun getSavedPlansByMessageId(messageId: Int): List<ManagerHistory> =
        managerHistoryDao.getHistoriesByMessageId(messageId)

    // return (isSchedule: Boolean, (savedPlanId: Int?, currentPlanId: Int?))
    override fun getRevisionPlanListsByMessageId(messageId: Int): List<Pair<PlanType, Pair<Int?, Int?>>> {
        val historyList = managerHistoryDao.getHistoriesByMessageId(messageId)

        return historyList.map {
            when (it.isSchedule) {
                true  -> Pair(PlanType.SCHEDULE, Pair(it.savedScheduleId, it.currentScheduleId))
                false -> Pair(PlanType.TODO, Pair(it.savedTodoId, it.currentTodoId))
            }
        }
    }

    override fun getRevisionHistoriesByMessageId(messageId: Int): List<ManagerHistory> =
        managerHistoryDao.getHistoriesByMessageId(messageId)

    override suspend fun deleteHistoryById(historyId: Int) {
        managerHistoryDao.deleteHistoryById(historyId)
    }

    override suspend fun deleteSavedPlan(savedPlan: Plan) = when (savedPlan) {
        is Schedule -> savedScheduleDao.delete(savedPlan.toSavedSchedule())
        is Todo     -> savedTodoDao.delete(savedPlan.toSavedTodo())
    }

    override fun getSavedPlanById(savedPlanId: Int, planType: PlanType): Plan = when (planType) {
        PlanType.SCHEDULE -> savedScheduleDao.getSavedScheduleById(savedPlanId).toSchedule()
        PlanType.TODO     -> savedTodoDao.getSavedTodoById(savedPlanId).toTodo()
    }

}