package com.example.calendy.data.maindb.repeatgroup

import com.example.calendy.data.BaseRepository
import kotlinx.coroutines.flow.Flow

interface IRepeatGroupRepository : BaseRepository<RepeatGroup> {
    suspend fun deleteRepeatGroupById(id: Int)
    fun getRepeatGroupById(id: Int): RepeatGroup
    fun getAllRepeatGroupsStream(): Flow<List<RepeatGroup>>
}
