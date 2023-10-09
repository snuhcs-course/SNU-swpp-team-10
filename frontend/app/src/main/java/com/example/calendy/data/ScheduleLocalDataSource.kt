package com.example.calendy.data

class ScheduleLocalDataSource(private val scheduleDao: ScheduleDao) {
    suspend fun insertSchedule(schedule: Schedule){
        scheduleDao.insert(schedule)
    }
    suspend fun updateSchedule(schedule: Schedule){
       scheduleDao.update(schedule)
    }
    suspend fun deleteSchedule(schedule: Schedule){
        scheduleDao.delete(schedule)
    }
}