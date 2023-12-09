package com.swpp10.calendy.data.maindb.category

import com.swpp10.calendy.data.BaseRepository
import kotlinx.coroutines.flow.Flow

abstract class ICategoryRepository(categoryDao: CategoryDao) : BaseRepository<Category>(categoryDao) {
    abstract fun getCategoriesStream(): Flow<List<Category>>
    abstract fun getCategoryById(id: Int): Category
    abstract fun getCategoryStreamById(id: Int): Flow<Category?>
}