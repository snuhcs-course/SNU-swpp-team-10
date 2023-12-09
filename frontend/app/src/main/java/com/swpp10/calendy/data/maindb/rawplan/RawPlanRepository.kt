package com.swpp10.calendy.data.maindb.rawplan

import com.swpp10.calendy.data.maindb.plan.Plan
import com.swpp10.calendy.data.maindb.plan.Schedule
import com.swpp10.calendy.data.maindb.plan.Todo

class RawPlanRepository (private val rawScheduleDao: RawScheduleDao,
    private val rawTodoDao: RawTodoDao
) {
        fun getAllPlans(): List<Plan> =
            rawScheduleDao.getAllRawSchedules().map { it.toSchedule() } +
                    rawTodoDao.getAllRawTodos().map { it.toTodo() }


        fun deleteAll() {
            rawScheduleDao.deleteAllRawSchedules()
            rawTodoDao.deleteAllRawTodos()
        }

    }
