package com.example.calendy.data.category

import kotlinx.coroutines.flow.Flow

interface ICategoryRepository {
    suspend fun insert(category: Category)
    suspend fun delete(category: Category)
    suspend fun update(category: Category)
    fun getCategoriesStream(): Flow<List<Category>>
    fun getCategoryById(id: Int): Flow<Category>

}