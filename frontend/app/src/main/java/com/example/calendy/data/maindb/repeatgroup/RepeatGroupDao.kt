package com.example.calendy.data.maindb.repeatgroup

import androidx.room.Dao
import androidx.room.Query
import com.example.calendy.data.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface RepeatGroupDao : BaseDao<RepeatGroup> {
    @Query("DELETE FROM repeat_group WHERE id = :id")
    suspend fun deleteRepeatGroupById(id: Int)

    @Query("SELECT * FROM repeat_group WHERE id = :id")
    fun getRepeatGroupById(id: Int): RepeatGroup

    @Query("SELECT * FROM repeat_group")
    fun getAllRepeatGroups(): Flow<List<RepeatGroup>>
}