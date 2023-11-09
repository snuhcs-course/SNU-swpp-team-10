package com.example.calendy.data.category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryLocalDataSource: CategoryLocalDataSource) : ICategoryRepository {
    override suspend fun insert(category: Category) {
        categoryLocalDataSource.insert(category)
    }
    override suspend fun delete(category: Category) {
        categoryLocalDataSource.delete(category)
    }
    override suspend fun update(category: Category) {
        categoryLocalDataSource.update(category)
    }
    override fun getCategoriesStream(): Flow<List<Category>> {
        return categoryLocalDataSource.getCategoriesStream()
    }
    override fun getCategoryById(id: Int): Flow<Category> {
        return categoryLocalDataSource.getCategoryById(id)
    }


}