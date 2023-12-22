package com.swpp10.calendy.data.maindb.history

import androidx.room.Dao
import androidx.room.Query
import com.swpp10.calendy.data.BaseDao

@Dao
interface SavedScheduleDao : BaseDao<SavedSchedule> {
    @Query("SELECT * FROM saved_schedule WHERE id = :id")
    fun getSavedScheduleById(id: Int): SavedSchedule
}