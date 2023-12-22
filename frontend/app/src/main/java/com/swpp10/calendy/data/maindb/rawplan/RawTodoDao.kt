package com.swpp10.calendy.data.maindb.rawplan

import androidx.room.Dao
import androidx.room.Query
import com.swpp10.calendy.data.BaseDao

@Dao
interface RawTodoDao : BaseDao<RawTodo> {
    @Query("DELETE FROM raw_todo")
    fun deleteAllRawTodos()

    @Query("SELECT * FROM raw_todo")
    fun getAllRawTodos(): List<RawTodo>
}