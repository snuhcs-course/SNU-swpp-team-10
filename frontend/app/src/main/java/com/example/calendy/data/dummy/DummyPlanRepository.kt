package com.example.calendy.data.dummy

import com.example.calendy.data.category.Category
import com.example.calendy.data.category.ICategoryRepository
import com.example.calendy.data.plan.IPlanRepository
import com.example.calendy.data.plan.Plan
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

}