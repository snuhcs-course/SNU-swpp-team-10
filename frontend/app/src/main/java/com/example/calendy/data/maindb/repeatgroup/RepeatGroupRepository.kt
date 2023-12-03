package com.example.calendy.data.maindb.repeatgroup

import kotlinx.coroutines.flow.Flow

class RepeatGroupRepository(private val repeatGroupDao: RepeatGroupDao) : IRepeatGroupRepository(repeatGroupDao) {
    override suspend fun deleteRepeatGroupById(id: Int) {
        return repeatGroupDao.deleteRepeatGroupById(id)
    }

    override fun getRepeatGroupById(id: Int): RepeatGroup {
        return repeatGroupDao.getRepeatGroupById(id)
    }

    override fun getAllRepeatGroupsStream(): Flow<List<RepeatGroup>> {
        return repeatGroupDao.getAllRepeatGroups()
    }
}