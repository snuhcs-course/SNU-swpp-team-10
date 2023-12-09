package com.example.calendy.data.maindb.category

import androidx.room.Dao
import androidx.room.Query
import com.example.calendy.data.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao : BaseDao<Category> {
    @Query("SELECT * FROM category")
    fun getAllCategoriesStream(): Flow<List<Category>>

    @Query("SELECT * FROM category WHERE id = :id")
    fun getCategoryById(id: Int): Category

    @Query("SELECT * FROM category WHERE id = :id")
    fun getCategoryStreamById(id: Int): Flow<Category?>
}