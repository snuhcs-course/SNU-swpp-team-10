package com.example.calendy.data.repeatgroup

import com.example.calendy.data.message.Message
import kotlinx.coroutines.flow.Flow

interface IRepeatGroupRepository {
    suspend fun insert(repeatGroup: RepeatGroup)
    suspend fun delete(repeatGroup: RepeatGroup)
    suspend fun update(repeatGroup: RepeatGroup)
    fun getRepeatGroupById(int: Int): Flow<RepeatGroup>

}
