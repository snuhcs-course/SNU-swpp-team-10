package com.example.calendy.data

import okhttp3.ResponseBody
import retrofit2.Call
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

// TODO: time 자동으로 생성해주는 함수
data class MessageBody(
    val message: String,
    val time: String,
    val category: String,
    val todo: String,
    val schedule: String
)
