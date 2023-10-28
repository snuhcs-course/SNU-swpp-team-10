package com.example.calendy.data

import retrofit2.Call
import retrofit2.http.*

interface CalendyApi {
    @POST("schedules")
    suspend fun postSchedule()
    @GET("schedules")
    suspend fun getSchedules()
    @POST("manager/send")
    fun sendMessageToServer(@Body body: MessageBody): Call<ServerResponse>
}

data class MessageBody(val message: String)
data class ServerResponse(val queries: List<String>)