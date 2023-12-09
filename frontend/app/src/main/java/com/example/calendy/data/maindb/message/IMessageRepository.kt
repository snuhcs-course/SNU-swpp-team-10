package com.example.calendy.data.maindb.message

import com.example.calendy.data.BaseRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

abstract class IMessageRepository(messageDao: MessageDao) : BaseRepository<Message>(messageDao) {
    abstract fun getAllMessagesStream(): Flow<List<Message>>
    abstract fun getMessagesStream(startTime: Date, endTime: Date): Flow<List<Message>>
    abstract suspend fun getResponseMessageGroup(userMessageId: Int): List<Message>
}