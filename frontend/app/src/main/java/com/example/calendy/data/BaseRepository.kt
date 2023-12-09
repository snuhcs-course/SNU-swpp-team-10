package com.example.calendy.data

abstract class BaseRepository<T>(private val dao: BaseDao<T>) {
    suspend fun insert(entity: T): Int {
        return dao.insert(entity).toInt()
    }

    suspend fun update(entity: T) {
        dao.update(entity)
    }

    suspend fun delete(entity: T) {
        dao.delete(entity)
    }
}