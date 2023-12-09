package com.swpp10.calendy.data.maindb.repeatgroup

import com.swpp10.calendy.data.BaseRepository
import kotlinx.coroutines.flow.Flow

abstract class IRepeatGroupRepository(repeatGroupDao: RepeatGroupDao) : BaseRepository<RepeatGroup>(repeatGroupDao) {
    abstract suspend fun deleteRepeatGroupById(id: Int)
    abstract fun getRepeatGroupById(id: Int): RepeatGroup
    abstract fun getAllRepeatGroupsStream(): Flow<List<RepeatGroup>>
}
