package com.example.calendy.data.maindb.category

import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) : ICategoryRepository {
    override suspend fun insert(category: Category) = categoryDao.insert(category)

    override suspend fun update(category: Category) = categoryDao.update(category)

    override suspend fun delete(category: Category) = categoryDao.delete(category)

    override fun getCategoriesStream(): Flow<List<Category>> {
        return categoryDao.getAllCategoriesStream()
    }

    override fun getCategoryById(id: Int): Category {
        return categoryDao.getCategoryById(id)
    }
}