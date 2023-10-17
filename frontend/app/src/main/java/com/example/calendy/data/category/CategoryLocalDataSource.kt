package com.example.calendy.data.category
import kotlinx.coroutines.flow.Flow

class CategoryLocalDataSource(private val categoryDao: CategoryDao) {
    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }
    suspend fun update(category: Category) {
        categoryDao.update(category)
    }
    suspend fun delete(category: Category) {
        categoryDao.delete(category)
    }
    fun getCategoriesStream(): Flow<List<Category>> {
        return categoryDao.getCategoriesStream()
    }
    fun getCategoryById(id: Int): Flow<Category> {
        return categoryDao.getCategoryById(id)
    }
}