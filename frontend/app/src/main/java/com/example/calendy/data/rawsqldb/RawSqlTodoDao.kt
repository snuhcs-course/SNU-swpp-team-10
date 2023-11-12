package com.example.calendy.data.rawsqldb

import androidx.room.Dao
import androidx.room.Query
import com.example.calendy.data.BaseDao

@Dao
interface RawSqlTodoDao : BaseDao<RawSqlTodo> {
    @Query("DELETE FROM todo")
    fun deleteAllTodos()

    @Query("SELECT * FROM todo")
    fun getAllTodos(): List<RawSqlTodo>
}