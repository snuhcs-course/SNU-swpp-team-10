package com.swpp10.calendy.data.dummy

import androidx.sqlite.db.SupportSQLiteQuery
import com.swpp10.calendy.data.maindb.plan.IPlanRepository
import com.swpp10.calendy.data.maindb.plan.Plan
import com.swpp10.calendy.data.maindb.plan.PlanType
import com.swpp10.calendy.data.maindb.plan.Schedule
import com.swpp10.calendy.data.maindb.plan.Todo
import kotlinx.coroutines.flow.Flow
import java.util.Date

class DummyPlanRepository : IPlanRepository {
    override fun getAllPlansStream(): Flow<List<Plan>> {
        TODO("Not yet implemented")
    }

    override fun getPlansStream(startTime: Date, endTime: Date): Flow<List<Plan>> {
        TODO("Not yet implemented")
    }

    override fun getPlanById(id: Int, type: PlanType): Plan {
        TODO("Not yet implemented")
    }

    override fun getSchedulesViaQuery(query: SupportSQLiteQuery): List<Schedule> {
        TODO("Not yet implemented")
    }

    override fun getTodosViaQuery(query: SupportSQLiteQuery): List<Todo> {
        TODO("Not yet implemented")
    }

    override fun getPlansByIds(scheduleIDs: List<Int>, todoIDs: List<Int>): List<Plan> {
        TODO("Not yet implemented")
    }

    override fun getMonthlyPlansStream(startTime: Date, endTime: Date): Flow<List<Plan>> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(entity: Plan): Int {
        TODO("Not yet implemented")
    }

    override suspend fun update(entity: Plan) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(entity: Plan) {
        TODO("Not yet implemented")
    }
}