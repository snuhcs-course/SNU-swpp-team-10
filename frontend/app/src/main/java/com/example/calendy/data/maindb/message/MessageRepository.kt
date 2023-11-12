package com.example.calendy.data.maindb.message

import kotlinx.coroutines.flow.Flow
import java.util.Date

class MessageRepository(private val messageDao: MessageDao) : IMessageRepository {
    override suspend fun insert(message: Message): Long = messageDao.insert(message)
    override suspend fun update(message: Message) = messageDao.update(message)
    override suspend fun delete(message: Message) = messageDao.delete(message)

    override fun getAllMessagesStream(): Flow<List<Message>> {
        return messageDao.getAllMessagesStream()
    }

    override fun getMessagesStream(startTime: Date, endTime: Date): Flow<List<Message>> {
        return messageDao.getMessagesStream(startTime, endTime)
    }
}