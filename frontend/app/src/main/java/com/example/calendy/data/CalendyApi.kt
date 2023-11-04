package com.example.calendy.data

import retrofit2.Call
import retrofit2.http.*

interface CalendyApi {
    @POST("schedules")
    suspend fun postSchedule()
    @GET("schedules")
    suspend fun getSchedules()
    @Headers("Accept-Encoding: identity") // TODO: Test for Network Inspector Korean Encoding...
    @POST("manager/send")
    suspend fun sendMessageToServer(@Body body: MessageBody): ServerResponse
}

data class MessageBody(val message: String)
data class ServerResponse(val queries: List<String>)