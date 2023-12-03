package com.example.calendy.data.network

import retrofit2.http.*

interface CalendyServerApi {
    @Headers("Accept-Encoding: identity") // TODO: Test for Network Inspector Korean Encoding...
    @POST("manager/send")
    suspend fun sendMessageToServer(
        @Body
        body: MessageBody
    ): String

    @POST("manager/briefing")
    suspend fun sendBriefingRequestToServer(
        @Body
        allPlans: String
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
