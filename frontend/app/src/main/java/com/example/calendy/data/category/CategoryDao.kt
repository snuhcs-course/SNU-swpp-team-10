package com.example.calendy.data.category
import androidx.room.Dao
import androidx.room.Query
import com.example.calendy.data.BaseDao
import kotlinx.coroutines.flow.Flow
@Dao
interface CategoryDao : BaseDao<Category> {
    @Query("SELECT * FROM category")
    fun getCategoriesStream(): Flow<List<Category>>
    @Query("SELECT * FROM category WHERE id = :id")
    fun getCategoryById(id: Int): Flow<Category>
}