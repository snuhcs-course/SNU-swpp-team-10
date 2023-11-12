package com.example.calendy.data.dummy

import com.example.calendy.data.maindb.repeatgroup.IRepeatGroupRepository
import com.example.calendy.data.maindb.repeatgroup.RepeatGroup

class DummyRepeatGroupRepository : IRepeatGroupRepository {
    override suspend fun insert(repeatGroup: RepeatGroup): Long = TODO("Not yet implemented")


    override suspend fun delete(repeatGroup: RepeatGroup) {
        TODO("Not yet implemented")
    }

    override suspend fun update(repeatGroup: RepeatGroup) {
        TODO("Not yet implemented")
    }

    override fun getRepeatGroupById(int: Int): RepeatGroup {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRepeatGroupById(id: Int) {
        TODO("Not yet implemented")
    }
}