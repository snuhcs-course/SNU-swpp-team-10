package com.example.calendy.data.plan.schedule

import com.example.calendy.data.CalendyApi


class ScheduleRemoteDataSource(private val calendyApi: CalendyApi) {
    suspend fun postSchedule() = calendyApi.postSchedule()
    suspend fun getSchedule() = calendyApi.getSchedules()
}