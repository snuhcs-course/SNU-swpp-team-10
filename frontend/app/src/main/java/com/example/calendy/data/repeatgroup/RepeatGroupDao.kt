package com.example.calendy.data.repeatgroup

import androidx.room.Dao
import androidx.room.Query
import com.example.calendy.data.BaseDao
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface RepeatGroupDao : BaseDao<RepeatGroup> {
    @Query("SELECT * FROM repeat_group WHERE id = :id")
    fun getRepeatGroupById(id: Int): Flow<RepeatGroup>

    @Query("DELETE FROM repeat_group WHERE id = :id")
    suspend fun deleteRepeatGroupById(id: Int)
}