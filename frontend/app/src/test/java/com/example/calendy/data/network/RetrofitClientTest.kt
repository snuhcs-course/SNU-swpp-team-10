package com.example.calendy.data.network

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RetrofitClientTest {
    @Test
    fun sendMessageToServer() = runBlocking {
        val queryString = "내일 3시에 컴퓨터구조 시험 일정 추가해줘"
        println("send to server: $queryString")

        val result = RetrofitClient.instance.sendMessageToServer(
            MessageBody(
                message = queryString,
                time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                category = "",
                schedule = "",
                todo = ""
            )
        )

        val queries = result.split(";")

        println("Queries: $queries")
        assertEquals(1,queries.size)
        assertEquals(true, queries.first().startsWith("INSERT INTO schedule", ignoreCase = true))
        assertEquals(true, queries.first().contains("컴퓨터구조 시험"))
    }

    @Test(expected = AssertionError::class)
    fun sendMessageToServer_Wrong() = runBlocking {
        val queryString = "내일 3시에 자료구조 시험 일정 추가해줘"
        println("send to server: $queryString")

        val result = RetrofitClient.instance.sendMessageToServer(
            MessageBody(
                message = queryString,
                time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                category = "",
                schedule = "",
                todo = ""
            )
        )

        val queries = result.split(";")

        println("Queries: $queries")
        assertEquals(1,queries.size)
        assertEquals(true, queries.first().startsWith("INSERT INTO schedule", ignoreCase = true))
        assertEquals(true, queries.first().contains("컴퓨터구조 시험")) // Should Fail
    }
}