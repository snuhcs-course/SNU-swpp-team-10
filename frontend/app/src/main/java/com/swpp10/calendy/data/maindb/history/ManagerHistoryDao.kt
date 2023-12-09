package com.swpp10.calendy.data.maindb.history

import androidx.room.Dao
import androidx.room.Query
import com.swpp10.calendy.data.BaseDao

@Dao
interface ManagerHistoryDao : BaseDao<ManagerHistory> {
    @Query("SELECT * FROM MANAGER_HISTORY WHERE message_id=:messageId")
    fun getHistoriesByMessageId(messageId: Int): List<ManagerHistory>
    @Query("DELETE FROM MANAGER_HISTORY WHERE id=:historyId")
    fun deleteHistoryById(historyId: Int)
}