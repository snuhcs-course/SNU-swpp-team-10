package com.example.calendy.data.dummy

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.maindb.plan.IPlanRepository
import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.PlanType
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
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

    override suspend fun insert(entity: Plan): Long {
        TODO("Not yet implemented")
    }

    override suspend fun update(entity: Plan) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(entity: Plan) {
        TODO("Not yet implemented")
    }
}