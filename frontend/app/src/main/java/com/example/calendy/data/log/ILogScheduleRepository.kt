package com.example.calendy.data.log

interface ILogScheduleRepository {
    suspend fun insertLogSchedule(logSchedule: LogSchedule)
    suspend fun updateLogSchedule(logSchedule: LogSchedule)
    suspend fun deleteLogSchedule(logSchedule: LogSchedule)
    fun getLogSchedulesByMessageId(messageId: Int): List<LogSchedule>
    fun getAllLogSchedules(): List<LogSchedule>
}