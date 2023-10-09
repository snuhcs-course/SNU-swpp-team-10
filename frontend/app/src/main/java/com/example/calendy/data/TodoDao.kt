package com.example.calendy.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    // startTime, endTime inclusive
    @Query("SELECT * FROM todo WHERE due_time BETWEEN :startTime AND :endTime")
    fun getTodosStream(startTime: Date, endTime: Date): Flow<List<Todo>>

    // end_time > query_start && start_time < query_end
    @Query("SELECT * FROM todo WHERE due_time == :dueTime AND yearly == :yearly AND monthly == :monthly AND daily == :daily")
    fun getSpecialTodosStream(dueTime: Date, yearly: Boolean = false, monthly: Boolean = false, daily: Boolean = false): Flow<List<Todo>>
}