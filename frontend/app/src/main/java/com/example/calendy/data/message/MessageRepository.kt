package com.example.calendy.data.message
import com.example.calendy.data.BaseRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
class MessageRepository(private val messageLocalDataSource: MessageLocalDataSource) : BaseRepository<Message> {
    override suspend fun insert(message: Message) {
        messageLocalDataSource.insert(message)
    }
    override suspend fun delete(message: Message) {
        messageLocalDataSource.delete(message)
    }
    override suspend fun update(message: Message) {
        messageLocalDataSource.update(message)
    }
    fun getSchedulesStream(startTime: Date, endTime: Date): Flow<List<Message>> {
        return messageLocalDataSource.getMessagesStream(startTime, endTime)
    }
}