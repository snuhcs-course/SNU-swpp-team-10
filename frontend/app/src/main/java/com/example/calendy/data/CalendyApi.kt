package com.example.calendy.data

import retrofit2.http.*

interface CalendyApi {
    @POST("schedules")
    suspend fun postSchedule()

    @GET("schedules")
    suspend fun getSchedules()

    @Headers("Accept-Encoding: identity") // TODO: Test for Network Inspector Korean Encoding...
    @POST("manager/send")
    suspend fun sendMessageToServer(
        @Body
        body: MessageBody
    ): String
}

data class MessageBody(
    val message: String,
    val category: String,
    val todo: String,
    val schedule: String
)
