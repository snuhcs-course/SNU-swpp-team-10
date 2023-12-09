package com.swpp10.calendy.data.dummy

import com.swpp10.calendy.data.maindb.category.Category
import com.swpp10.calendy.data.maindb.category.CategoryDao
import com.swpp10.calendy.data.maindb.category.ICategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class DummyCategoryDao : CategoryDao {
    override fun getAllCategoriesStream(): Flow<List<Category>> {
        TODO("Not yet implemented")
    }

    override fun getCategoryById(id: Int): Category {
        TODO("Not yet implemented")
    }

    override fun getCategoryStreamById(id: Int): Flow<Category> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(entity: Category): Long {
        TODO("Not yet implemented")
    }

    override suspend fun update(entity: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(entity: Category) {
        TODO("Not yet implemented")
    }

}

class DummyCategoryRepository : ICategoryRepository(DummyCategoryDao()) {
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

    override fun getCategoryStreamById(id: Int): Flow<Category?> {
        TODO("Not yet implemented")
    }
}