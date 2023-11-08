package com.example.calendy.data.repeatgroup

import kotlinx.coroutines.flow.Flow
import java.util.Date

class RepeatGroupLocalDataSource(private val repeatGroupDao: RepeatGroupDao) {
    suspend fun insert(repeatGroup: RepeatGroup) : Long = repeatGroupDao.insert(repeatGroup)
    suspend fun update(repeatGroup: RepeatGroup) {
        repeatGroupDao.update(repeatGroup)
    }
    suspend fun delete(repeatGroup: RepeatGroup) {
        repeatGroupDao.delete(repeatGroup)
    }
    fun getRepeatGroupById(id: Int): Flow<RepeatGroup> {
        return repeatGroupDao.getRepeatGroupById(id)
    }
    suspend fun deleteRepeatGroupById(id: Int) {
        return repeatGroupDao.deleteRepeatGroupById(id)
    }
}