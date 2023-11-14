package com.example.calendy.data.maindb.plan

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.maindb.plan.schedule.IScheduleRepository
import com.example.calendy.data.maindb.plan.todo.ITodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Date

class PlanRepository(
    private val scheduleRepository: IScheduleRepository, private val todoRepository: ITodoRepository
) : IPlanRepository {
    override suspend fun insert(plan: Plan): Long {
        // TODO: VALIDATE
        // plan.categoryId
        // plan.repeatGroupId
        return when (plan) {
            is Schedule -> scheduleRepository.insert(plan.copy(id = 0))
            is Todo     -> todoRepository.insert(plan.copy(id = 0))
        }
    }

    override suspend fun update(plan: Plan) {
        when (plan) {
            is Schedule -> scheduleRepository.update(plan)
            is Todo     -> todoRepository.update(plan)
        }
    }

    override suspend fun delete(plan: Plan) {
        when (plan) {
            is Schedule -> scheduleRepository.delete(plan)
            is Todo     -> todoRepository.delete(plan)
        }
    }

    override fun getAllPlansStream(): Flow<List<Plan>> {
        val schedulesStream = scheduleRepository.getAllSchedulesStream()
        val todosStream = todoRepository.getAllTodosStream()
        return combine(schedulesStream, todosStream) { scheduleList, todoList ->
            val result = scheduleList + todoList
            result
        }
    }

    // TODO: not tested. should test both stream update
    override fun getPlansStream(startTime: Date, endTime: Date): Flow<List<Plan>> {
        val schedulesStream = scheduleRepository.getSchedulesStream(startTime, endTime)
        val todosStream = todoRepository.getTodosStream(startTime, endTime)
        return combine(schedulesStream, todosStream) { scheduleList, todoList ->
            val result = scheduleList + todoList
            result
        }
    }

    // Usage: getPlanById(id = 3, type = Plan.PlanType.Schedule)
    override fun getPlanById(id: Int, type: PlanType): Plan = when (type) {
        PlanType.SCHEDULE -> scheduleRepository.getScheduleById(id)
        PlanType.TODO     -> todoRepository.getTodoById(id)
    }

    override fun getPlansByIds(scheduleIDs: List<Int>, todoIDs: List<Int>): List<Plan> {
        val schedules = scheduleRepository.getSchedulesByIds(scheduleIDs)
        val todos = todoRepository.getTodosByIds(todoIDs)
        return schedules + todos
    }

    override fun getSchedulesViaQuery(query: SupportSQLiteQuery): List<Schedule> =
        scheduleRepository.getSchedulesViaQuery(query)

    override fun getTodosViaQuery(query: SupportSQLiteQuery): List<Todo> =
        todoRepository.getTodosViaQuery(query)

}