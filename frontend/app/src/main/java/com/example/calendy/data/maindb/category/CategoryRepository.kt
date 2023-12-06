package com.example.calendy.data.maindb.category

import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) : ICategoryRepository(categoryDao) {
    override fun getCategoriesStream(): Flow<List<Category>> {
        return categoryDao.getAllCategoriesStream()
    }

    override fun getCategoryById(id: Int): Category {
        return categoryDao.getCategoryById(id)
    }

    override fun getCategoryStreamById(id: Int): Flow<Category?> {
        return categoryDao.getCategoryStreamById(id)
    }
}