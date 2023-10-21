package com.example.calendy.data.repeatgroup

import com.example.calendy.data.message.Message
import kotlinx.coroutines.flow.Flow
import java.util.Date

class RepeatGroupRepository(private val repeatGroupLocalDataSource: RepeatGroupLocalDataSource): IRepeatGroupRepository {
    override suspend fun insert(repeatGroup: RepeatGroup) {
        repeatGroupLocalDataSource.insert(repeatGroup)
    }
    override suspend fun delete(repeatGroup: RepeatGroup) {
        repeatGroupLocalDataSource.delete(repeatGroup)
    }
    override suspend fun update(repeatGroup: RepeatGroup) {
        repeatGroupLocalDataSource.update(repeatGroup)
    }
    override fun getRepeatGroupById(id: Int): Flow<RepeatGroup> {
       return repeatGroupLocalDataSource.getRepeatGroupById(id)
    }
}