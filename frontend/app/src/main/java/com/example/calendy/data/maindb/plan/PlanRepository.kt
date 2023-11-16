package com.example.calendy.data.maindb.plan

import androidx.room.rxjava3.EmptyResultSetException
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.calendy.data.maindb.category.Category
import com.example.calendy.data.maindb.category.ICategoryRepository
import com.example.calendy.data.maindb.plan.Plan.Companion.PRIORITY_DEFAULT
import com.example.calendy.data.maindb.plan.schedule.IScheduleRepository
import com.example.calendy.data.maindb.plan.todo.ITodoRepository
import com.example.calendy.data.maindb.repeatgroup.IRepeatGroupRepository
import com.example.calendy.data.maindb.repeatgroup.RepeatGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Date

class PlanRepository(
    private val scheduleRepository: IScheduleRepository,
    private val todoRepository: ITodoRepository,
    private val categoryRepository: ICategoryRepository,
    private val repeatGroupRepository: IRepeatGroupRepository
) : IPlanRepository {
    // We need each of them because copy function is not valid in interface Plan.
    private fun validatePlan(plan: Plan): Plan {

        fun validateSchedule(schedule: Schedule): Schedule {
            val category: Category? = if (schedule.categoryId!=null) {
                try {
                    categoryRepository.getCategoryById(schedule.categoryId)
                } catch (e: EmptyResultSetException) {
                    null
                }
            } else {
                null
            }

            val repeatGroup: RepeatGroup? = if (schedule.repeatGroupId!=null) {
                try {
                    repeatGroupRepository.getRepeatGroupById(schedule.repeatGroupId)
                } catch (e: EmptyResultSetException) {
                    null
                }
            } else {
                null
            }

            return schedule.let {
                // Check if categoryId satisfies foreign key constraint
                if (category==null) {
                    it.copy(categoryId = null)
                } else {
                    it.copy(categoryId = category.id)
                }
            }.let {
                // Check if repeatGroupId satisfies foreign key constraint
                if (repeatGroup==null) {
                    it.copy(repeatGroupId = null)
                } else {
                    it.copy(repeatGroupId = repeatGroup.id)
                }
            }.let {
                // Check if priority satisfies 1..5
                if (it.priority !in 1..5) {
                    it.copy(priority = category?.defaultPriority ?: PRIORITY_DEFAULT)
                } else {
                    it
                }
            }
        }

        fun validateTodo(todo: Todo): Todo {
            val category: Category? = if (todo.categoryId!=null) {
                try {
                    categoryRepository.getCategoryById(todo.categoryId)
                } catch (e: EmptyResultSetException) {
                    null
                }
            } else {
                null
            }

            val repeatGroup: RepeatGroup? = if (todo.repeatGroupId!=null) {
                try {
                    repeatGroupRepository.getRepeatGroupById(todo.repeatGroupId)
                } catch (e: EmptyResultSetException) {
                    null
                }
            } else {
                null
            }

            return todo.let {
                // Check if categoryId satisfies foreign key constraint
                if (category==null) {
                    it.copy(categoryId = null)
                } else {
                    it.copy(categoryId = category.id)
                }
            }.let {
                // Check if repeatGroupId satisfies foreign key constraint
                if (repeatGroup==null) {
                    it.copy(repeatGroupId = null)
                } else {
                    it.copy(repeatGroupId = repeatGroup.id)
                }
            }.let {
                // Check if priority satisfies 1..5
                if (it.priority !in 1..5) {
                    it.copy(priority = category?.defaultPriority ?: PRIORITY_DEFAULT)
                } else {
                    it
                }
            }
        }

        return when (plan) {
            is Schedule -> validateSchedule(plan)
            is Todo     -> validateTodo(plan)
        }
    }

    // setting priority value & foreign key validation included
    override suspend fun insert(plan: Plan): Long {
        return validatePlan(plan).let {
            when (it) {
                is Schedule -> scheduleRepository.insert(it.copy(id = 0))
                is Todo     -> todoRepository.insert(it.copy(id = 0))
            }
        }
    }

    override suspend fun update(plan: Plan) {
        return validatePlan(plan).let {
            when (it) {
                is Schedule -> scheduleRepository.update(it)
                is Todo     -> todoRepository.update(it)
            }
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