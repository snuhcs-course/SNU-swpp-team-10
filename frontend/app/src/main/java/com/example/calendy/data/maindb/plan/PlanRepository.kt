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
import kotlinx.coroutines.flow.map
import java.util.Date

class PlanRepository(
    private val scheduleRepository: IScheduleRepository,
    private val todoRepository: ITodoRepository,
    private val categoryRepository: ICategoryRepository,
    private val repeatGroupRepository: IRepeatGroupRepository
) : IPlanRepository {
    private fun validatePlan(plan: Plan): Plan {
        // We need both validateSchedule and tod0 because copy function is not valid in interface Plan.
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
                // Check if startTime <= endTime
                if (it.startTime > it.endTime) {
                    it.copy(endTime = it.startTime)
                } else {
                    it
                }
            }.run {
                // Check if categoryId satisfies foreign key constraint (if not, set categoryId to null)
                copy(categoryId = category?.id)
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
    override suspend fun insert(plan: Plan): Int {
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

    override fun getPlansStream(startTime: Date, endTime: Date): Flow<List<Plan>> {
        val schedulesStream = scheduleRepository.getSchedulesStream(startTime, endTime)
        val todosStream = todoRepository.getTodosStream(startTime, endTime)

        // Generate Repeated Plan Dynamically
        val repeatGroupsStream = repeatGroupRepository.getAllRepeatGroupsStream()
        val repeatedPlansStream: Flow<List<Plan>> = repeatGroupsStream.map { repeatGroupList ->
            repeatGroupList.map {
                // TODO: RepeatGroup Feature Give up
                // savedPlan Entity 에 저장하거나 Schedule table에 not visible flag를 세워서 추가해둬야 하네...
//                val x = scheduleRepository.getScheduleById(it.originalPlanId)
//                it.generatePlanList(
//                    from = startTime.dateOnly(), until = endTime.dateOnly(), plan = x
//                )
                emptyList<Plan>()
            }.flatten() // flatten List<List<Plan>> to List<Plan>
        }

        return combine(
            schedulesStream, todosStream, repeatedPlansStream
        ) { scheduleList, todoList, repeatedPlanList ->
            val result = scheduleList + todoList + repeatedPlanList
            result
        }
    }

    // get Plans where showInMonthlyView = 1
    override fun getMonthlyPlansStream(startTime: Date, endTime: Date): Flow<List<Plan>> {
        val schedulesStream = scheduleRepository.getMonthlySchedulesStream(startTime, endTime)
        val todosStream = todoRepository.getMonthlyTodosStream(startTime, endTime)
        return combine(schedulesStream, todosStream) { scheduleList, todoList ->
            val result = scheduleList + todoList
            result
        }
    }

    // Usage: getPlanById(id = 3, type = PlanType.SCHEDULE)
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