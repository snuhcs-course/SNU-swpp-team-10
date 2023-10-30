package com.example.calendy.data.plan

import com.example.calendy.data.plan.Plan.PlanType
import com.example.calendy.data.plan.schedule.IScheduleRepository
import com.example.calendy.data.plan.todo.ITodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Date

class PlanRepository(
    private val scheduleRepository: IScheduleRepository, private val todoRepository: ITodoRepository
) : IPlanRepository {
    // not tested. should test both stream update
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
        PlanType.Todo     -> todoRepository.getTodoById(id)
    }

    override fun getAllPlans(): Flow<List<Plan>> {
        val schedulesStream = scheduleRepository.getAllSchedule()
        val todosStream = todoRepository.getAllTodo()
        return combine(schedulesStream, todosStream) { scheduleList, todoList ->
            val result = scheduleList + todoList
            result
        }
    }
}