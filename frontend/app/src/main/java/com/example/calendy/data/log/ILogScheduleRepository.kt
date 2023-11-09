package com.example.calendy.data.log

interface ILogScheduleRepository {
    suspend fun insertLogSchedule(logSchedule: LogSchedule)
    suspend fun updateLogSchedule(logSchedule: LogSchedule)
    suspend fun deleteLogSchedule(logSchedule: LogSchedule)
    suspend fun getLogSchedulesByMessageId(messageId: Int): List<LogSchedule>
    suspend fun getAllLogSchedules(): List<LogSchedule>
}