package com.example.calendy.data.maindb.repeatgroup

class RepeatGroupRepository(private val repeatGroupDao: RepeatGroupDao) : IRepeatGroupRepository {
    override suspend fun insert(repeatGroup: RepeatGroup): Long = repeatGroupDao.insert(repeatGroup)

    override suspend fun update(repeatGroup: RepeatGroup) {
        repeatGroupDao.update(repeatGroup)
    }

    override suspend fun delete(repeatGroup: RepeatGroup) {
        repeatGroupDao.delete(repeatGroup)
    }

    override suspend fun deleteRepeatGroupById(id: Int) {
        return repeatGroupDao.deleteRepeatGroupById(id)
    }

    override fun getRepeatGroupById(id: Int): RepeatGroup {
        return repeatGroupDao.getRepeatGroupById(id)
    }
}