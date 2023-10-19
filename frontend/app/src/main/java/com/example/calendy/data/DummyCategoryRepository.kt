package com.example.calendy.data

import com.example.calendy.data.category.Category
import com.example.calendy.data.category.ICategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class DummyCategoryRepository : ICategoryRepository {
    override suspend fun insert(category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun update(category: Category) {
        TODO("Not yet implemented")
    }

    override fun getCategoriesStream(): Flow<List<Category>> = listOf(
            listOf(
                    Category(userId = 2, title = "One", defaultPriority = 3),
                    Category(userId = 2, title = "Two", defaultPriority = 3),
                    Category(userId = 2, title = "Three", defaultPriority = 3),
            )
    ).asFlow()

    override fun getCategoryById(id: Int): Flow<Category> {
        TODO("Not yet implemented")
    }
}