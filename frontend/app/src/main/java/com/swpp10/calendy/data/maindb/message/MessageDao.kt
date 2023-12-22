package com.swpp10.calendy.data.maindb.message

import androidx.room.Dao
import androidx.room.Query
import com.swpp10.calendy.data.BaseDao
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface MessageDao : BaseDao<Message> {
    @Query("SELECT * FROM message WHERE sent_time BETWEEN :startTime AND :endTime")
    fun getMessagesStream(startTime: Date, endTime: Date): Flow<List<Message>>

    @Query("SELECT * FROM message ORDER BY user_message_id DESC, id DESC")
    fun getAllMessagesStream(): Flow<List<Message>>

    @Query("SELECT * FROM message WHERE user_message_id = :userMessageId AND message_from_manager = 1")
    suspend fun getResponseMessageGroup(userMessageId: Int): List<Message>
}