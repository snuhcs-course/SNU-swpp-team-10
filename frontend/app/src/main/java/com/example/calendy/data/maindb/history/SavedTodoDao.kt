package com.example.calendy.data.maindb.history

import androidx.room.Dao
import androidx.room.Query
import com.example.calendy.data.BaseDao

@Dao
interface SavedTodoDao : BaseDao<SavedTodo> {
    @Query("SELECT * FROM saved_todo WHERE id = :id")
    fun getSavedTodoById(id: Int): SavedTodo
}