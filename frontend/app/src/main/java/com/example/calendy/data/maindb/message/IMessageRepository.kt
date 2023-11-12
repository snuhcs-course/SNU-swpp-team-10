package com.example.calendy.data.maindb.message

import com.example.calendy.data.BaseRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface IMessageRepository : BaseRepository<Message> {
    fun getAllMessagesStream(): Flow<List<Message>>

    fun getMessagesStream(startTime: Date, endTime: Date): Flow<List<Message>>
}