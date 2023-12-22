package com.swpp10.calendy.data.maindb.history

import androidx.room.Dao
import androidx.room.Query
import com.swpp10.calendy.data.BaseDao

@Dao
interface SavedTodoDao : BaseDao<SavedTodo> {
    @Query("SELECT * FROM saved_todo WHERE id = :id")
    fun getSavedTodoById(id: Int): SavedTodo
}