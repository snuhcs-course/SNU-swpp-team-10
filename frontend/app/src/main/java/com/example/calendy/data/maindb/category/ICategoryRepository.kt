package com.example.calendy.data.maindb.category

import com.example.calendy.data.BaseRepository
import kotlinx.coroutines.flow.Flow

interface ICategoryRepository : BaseRepository<Category> {
    fun getCategoriesStream(): Flow<List<Category>>
    fun getCategoryById(id: Int): Category
}