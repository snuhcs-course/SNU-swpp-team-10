package com.example.calendy.data

import com.example.calendy.data.schedule.Schedule
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CalendyApi {
    @POST("schedules")
    suspend fun postSchedule()
    @GET("schedules")
    suspend fun getSchedules()
}