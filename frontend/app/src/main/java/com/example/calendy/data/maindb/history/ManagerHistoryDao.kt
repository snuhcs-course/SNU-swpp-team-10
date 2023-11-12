package com.example.calendy.data.maindb.history

import androidx.room.Dao
import androidx.room.Query
import com.example.calendy.data.BaseDao

@Dao
interface ManagerHistoryDao : BaseDao<ManagerHistory> {
    @Query("SELECT * FROM MANAGER_HISTORY WHERE message_id=:messageId")
    fun getHistoriesByMessageId(messageId: Int): List<ManagerHistory>
}