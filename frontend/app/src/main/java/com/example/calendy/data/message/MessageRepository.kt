package com.example.calendy.data.message
import kotlinx.coroutines.flow.Flow
import java.util.Date
class MessageRepository(private val messageLocalDataSource: MessageLocalDataSource) : IMessageRepository {
    override suspend fun insert(message: Message) {
        messageLocalDataSource.insert(message)
    }
    override suspend fun delete(message: Message) {
        messageLocalDataSource.delete(message)
    }
    override suspend fun update(message: Message) {
        messageLocalDataSource.update(message)
    }
    override fun getMessagesStream(startTime: Date, endTime: Date): Flow<List<Message>> {
        return messageLocalDataSource.getMessagesStream(startTime, endTime)
    }

    override fun getAllMessages(): Flow<List<Message>> {
        return messageLocalDataSource.getAllMessages()
    }
}