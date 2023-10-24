package com.example.calendy.data.dummy

import com.example.calendy.data.repeatgroup.IRepeatGroupRepository
import com.example.calendy.data.repeatgroup.RepeatGroup
import kotlinx.coroutines.flow.Flow

class DummyRepeatGroupRepository : IRepeatGroupRepository {
    override suspend fun insert(repeatGroup: RepeatGroup) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(repeatGroup: RepeatGroup) {
        TODO("Not yet implemented")
    }

    override suspend fun update(repeatGroup: RepeatGroup) {
        TODO("Not yet implemented")
    }

    override fun getRepeatGroupById(int: Int): Flow<RepeatGroup> {
        TODO("Not yet implemented")
    }
}