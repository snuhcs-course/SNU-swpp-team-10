package com.example.calendy.data.log

import com.example.calendy.data.plan.todo.ITodoRepository
import kotlinx.coroutines.flow.combine

class LogPlanRepository(
    private val logScheduleRepository: ILogScheduleRepository, private val logTodoRepository: ILogTodoRepository
) : ILogPlanRepository {
    override suspend fun insertLogPlan(logPlan: LogPlan) {
        when (logPlan) {
            is LogSchedule -> logScheduleRepository.insertLogSchedule(logPlan)
            is LogTodo -> logTodoRepository.insertLogTodo(logPlan)
        }
    }

    override suspend fun updateLogPlan(logPlan: LogPlan) {
        when (logPlan) {
            is LogSchedule -> logScheduleRepository.updateLogSchedule(logPlan)
            is LogTodo -> logTodoRepository.updateLogTodo(logPlan)
        }
    }

    override suspend fun deleteLogPlan(logPlan: LogPlan) {
        when (logPlan) {
            is LogSchedule -> logScheduleRepository.deleteLogSchedule(logPlan)
            is LogTodo -> logTodoRepository.deleteLogTodo(logPlan)
        }
    }

    // TODO: Flow를 써야 할 수도?
    override fun getLogPlansByMessageId(messageId: Int): List<LogPlan> {
        val logScheduleList = logScheduleRepository.getLogSchedulesByMessageId(messageId)
        val logTodoList = logTodoRepository.getLogTodosByMessageId(messageId)
        return logScheduleList + logTodoList
    }

    override fun getAllLogPlans(): List<LogPlan> {
        val logScheduleList = logScheduleRepository.getAllLogSchedules()
        val logTodoList = logTodoRepository.getAllLogTodos()
        return logScheduleList + logTodoList
    }
}