package com.example.calendy.data.plan

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.plan.Plan.PlanType
import com.example.calendy.data.plan.schedule.IScheduleRepository
import com.example.calendy.data.plan.todo.ITodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Date

class PlanRepository(
    private val scheduleRepository: IScheduleRepository, private val todoRepository: ITodoRepository
) : IPlanRepository {
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
    override fun getPlanById(id: Int, type: PlanType): Flow<Plan> = when (type) {
        PlanType.Schedule -> scheduleRepository.getScheduleById(id)
        PlanType.Todo -> todoRepository.getTodoById(id)
    }

    override fun getAllPlans(): Flow<List<Plan>> {
        val schedulesStream = scheduleRepository.getAllSchedule()
        val todosStream = todoRepository.getAllTodo()
        return combine(schedulesStream, todosStream) { scheduleList, todoList ->
            val result = scheduleList + todoList
            result
        }
    }

    override fun getTodosViaQuery(query: SupportSQLiteQuery): List<Todo> =
        todoRepository.getTodosViaQuery(query)

    override fun getSchedulesViaQuery(query: SupportSQLiteQuery): List<Schedule> =
        scheduleRepository.getSchedulesViaQuery(query)

    override suspend fun insertPlan(plan: Plan) {
        when (plan) {
            is Schedule -> scheduleRepository.insertSchedule(plan)
            is Todo -> todoRepository.insertTodo(plan)
        }
    }
    override suspend fun updatePlan(plan: Plan) {
        when (plan) {
            is Schedule -> scheduleRepository.updateSchedule(plan)
            is Todo -> todoRepository.updateTodo(plan)
        }
    }

    override suspend fun deletePlan(plan: Plan) {
        when (plan) {
            is Schedule -> scheduleRepository.deleteSchedule(plan)
            is Todo -> todoRepository.deleteTodo(plan)
        }
    }

    override suspend fun getPlansByIds(scheduelIDs: List<Int>, todoIDs:List<Int>): List<Plan> {
        val schedules = scheduleRepository.getSchedulesByIds(scheduelIDs)
        val todos = todoRepository.getTodosByIds(todoIDs)
        return schedules + todos
    }

}