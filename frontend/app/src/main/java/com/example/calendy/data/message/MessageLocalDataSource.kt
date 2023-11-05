package com.example.calendy.data.message
import kotlinx.coroutines.flow.Flow
import java.util.Date
class MessageLocalDataSource(private val messageDao: MessageDao) {
    suspend fun insert(message: Message) {
        messageDao.insert(message)
    }
    suspend fun update(message: Message) {
        messageDao.update(message)
    }
    suspend fun delete(message: Message) {
        messageDao.delete(message)
    }
    fun getMessagesStream(startTime: Date, endTime: Date): Flow<List<Message>> {
        return messageDao.getMessagesStream(startTime, endTime)
    }
    fun getAllMessages():Flow<List<Message>>{
        return messageDao.getAllMessages()
    }
}