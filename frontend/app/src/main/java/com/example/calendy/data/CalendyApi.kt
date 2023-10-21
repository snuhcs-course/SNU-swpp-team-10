package com.example.calendy.data

import retrofit2.http.GET
import retrofit2.http.POST

interface CalendyApi {
    @POST("schedules")
    suspend fun postSchedule()
    @GET("schedules")
    suspend fun getSchedules()
}