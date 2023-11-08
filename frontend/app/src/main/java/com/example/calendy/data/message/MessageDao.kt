package com.example.calendy.data.message
import androidx.room.Dao
import androidx.room.Query
import com.example.calendy.data.BaseDao
import kotlinx.coroutines.flow.Flow
import java.util.Date
@Dao
interface MessageDao : BaseDao<Message> {
    @Query("SELECT * FROM message WHERE sent_time BETWEEN :startTime AND :endTime")
    fun getMessagesStream(startTime: Date, endTime: Date): Flow<List<Message>>
    @Query("SELECT * FROM message ORDER BY id DESC")
    fun getAllMessages(): Flow<List<Message>>
}