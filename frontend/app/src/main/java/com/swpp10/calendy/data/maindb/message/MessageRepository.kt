package com.swpp10.calendy.data.maindb.message

import kotlinx.coroutines.flow.Flow
import java.util.Date

class MessageRepository(private val messageDao: MessageDao) : IMessageRepository(messageDao) {
    override fun getAllMessagesStream(): Flow<List<Message>> {
        return messageDao.getAllMessagesStream()
    }

    override fun getMessagesStream(startTime: Date, endTime: Date): Flow<List<Message>> {
        return messageDao.getMessagesStream(startTime, endTime)
    }

    override suspend fun getResponseMessageGroup(userMessageId: Int): List<Message> {
        return messageDao.getResponseMessageGroup(userMessageId)
    }
}