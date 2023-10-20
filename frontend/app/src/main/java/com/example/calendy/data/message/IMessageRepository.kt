package com.example.calendy.data.message

import kotlinx.coroutines.flow.Flow
import java.util.Date

interface IMessageRepository {
    suspend fun insert(message: Message)
    suspend fun delete(message: Message)
    suspend fun update(message: Message)
    fun getMessagesStream(startTime: Date, endTime: Date): Flow<List<Message>>
}