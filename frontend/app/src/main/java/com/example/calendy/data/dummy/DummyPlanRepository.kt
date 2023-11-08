package com.example.calendy.data.dummy

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.category.Category
import com.example.calendy.data.category.ICategoryRepository
import com.example.calendy.data.plan.IPlanRepository
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.util.Date

class DummyPlanRepository : IPlanRepository {
    override fun getPlansStream(startTime: Date, endTime: Date): Flow<List<Plan>> {
        TODO("Not yet implemented")
    }

    override fun getPlanById(id: Int, type: Plan.PlanType): Flow<Plan> {
        TODO("Not yet implemented")
    }

    override fun getAllPlans(): Flow<List<Plan>> {
        TODO("Not yet implemented")
    }

    override suspend fun updatePlan(plan: Plan) {
        TODO("Not yet implemented")
    }

    override suspend fun insertPlan(plan: Plan) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlan(plan: Plan) {
        TODO("Not yet implemented")
    }

    override fun getSchedulesViaQuery(query: SupportSQLiteQuery): List<Schedule> {
        TODO("Not yet implemented")
    }

    override fun getTodosViaQuery(query: SupportSQLiteQuery): List<Todo> {
        TODO("Not yet implemented")
    }


}