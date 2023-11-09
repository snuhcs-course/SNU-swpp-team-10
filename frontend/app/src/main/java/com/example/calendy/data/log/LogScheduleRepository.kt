package com.example.calendy.data.log

class LogScheduleRepository(private val logScheduleDao: LogScheduleDao) : ILogScheduleRepository {
    override suspend fun insertLogSchedule(logSchedule: LogSchedule) {
        logScheduleDao.insert(logSchedule)
    }

    override suspend fun updateLogSchedule(logSchedule: LogSchedule) {
        logScheduleDao.update(logSchedule)
    }

    override suspend fun deleteLogSchedule(logSchedule: LogSchedule) {
        logScheduleDao.delete(logSchedule)
    }

    override suspend fun getLogSchedulesByMessageId(messageId: Int): List<LogSchedule> {
        return logScheduleDao.getLogSchedulesByMessageId(messageId)
    }

    override suspend fun getAllLogSchedules(): List<LogSchedule> = logScheduleDao.getAllLogSchedules()
}