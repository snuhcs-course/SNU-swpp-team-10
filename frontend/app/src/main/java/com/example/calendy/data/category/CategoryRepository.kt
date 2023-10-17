package com.example.calendy.data.category
import com.example.calendy.data.BaseRepository
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryLocalDataSource: CategoryLocalDataSource) : BaseRepository<Category> {
    override suspend fun insert(category: Category) {
        categoryLocalDataSource.insert(category)
    }
    override suspend fun delete(category: Category) {
        categoryLocalDataSource.delete(category)
    }
    override suspend fun update(category: Category) {
        categoryLocalDataSource.update(category)
    }
    fun getCategoriesStream(): Flow<List<Category>> {
        return categoryLocalDataSource.getCategoriesStream()
    }
    fun getCategoryById(id: Int): Flow<Category> {
        return categoryLocalDataSource.getCategoryById(id)
    }
}