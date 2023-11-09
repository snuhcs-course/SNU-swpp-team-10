package com.example.calendy.data.plan

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.category.Category
import com.example.calendy.data.category.ICategoryRepository
import com.example.calendy.data.plan.Plan.PlanType
import com.example.calendy.data.plan.schedule.IScheduleRepository
import com.example.calendy.data.plan.todo.ITodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.Date

class PlanRepository(
    private val scheduleRepository: IScheduleRepository, private val todoRepository: ITodoRepository,
    private val categoryRepository: ICategoryRepository
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
        val newPriority = plan.priority ?: plan.categoryId?.let { categoryId ->
            categoryRepository.getCategoryById(categoryId)
                .firstOrNull()
                ?.defaultPriority
        } ?:3

        val newPlan = when (plan) {
            is Schedule -> plan.copy(priority = newPriority)
            is Todo -> plan.copy(priority = newPriority)
            // Add other implementations if there are any
        }

        when (newPlan) {
            is Schedule -> scheduleRepository.insertSchedule(newPlan)
            is Todo -> todoRepository.insertTodo(newPlan)
            // Handle other implementations similarly
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


}