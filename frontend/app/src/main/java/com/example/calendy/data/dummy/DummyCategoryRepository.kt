package com.example.calendy.data.dummy

import com.example.calendy.data.maindb.category.Category
import com.example.calendy.data.maindb.category.ICategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class DummyCategoryRepository : ICategoryRepository {
    override suspend fun insert(category: Category): Long {
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
            Category(title = "One", defaultPriority = 3),
            Category(title = "Two", defaultPriority = 3),
            Category(title = "Three", defaultPriority = 3),
        )
    ).asFlow()

    override fun getCategoryById(id: Int): Category {
        TODO("Not yet implemented")
    }
}