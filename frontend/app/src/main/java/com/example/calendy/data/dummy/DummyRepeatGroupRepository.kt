package com.example.calendy.data.dummy

import com.example.calendy.data.maindb.repeatgroup.IRepeatGroupRepository
import com.example.calendy.data.maindb.repeatgroup.RepeatGroup
import com.example.calendy.data.maindb.repeatgroup.RepeatGroupDao
import kotlinx.coroutines.flow.Flow

class DummyRepeatGroupDao : RepeatGroupDao {
    override suspend fun deleteRepeatGroupById(id: Int) {
        TODO("Not yet implemented")
    }

    override fun getRepeatGroupById(id: Int): RepeatGroup {
        TODO("Not yet implemented")
    }

    override fun getAllRepeatGroups(): Flow<List<RepeatGroup>> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(entity: RepeatGroup): Long {
        TODO("Not yet implemented")
    }

    override suspend fun update(entity: RepeatGroup) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(entity: RepeatGroup) {
        TODO("Not yet implemented")
    }

}

class DummyRepeatGroupRepository : IRepeatGroupRepository(repeatGroupDao = DummyRepeatGroupDao()) {
    override fun getRepeatGroupById(id: Int): RepeatGroup {
        TODO("Not yet implemented")
    }

    override fun getAllRepeatGroupsStream(): Flow<List<RepeatGroup>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRepeatGroupById(id: Int) {
        TODO("Not yet implemented")
    }
}