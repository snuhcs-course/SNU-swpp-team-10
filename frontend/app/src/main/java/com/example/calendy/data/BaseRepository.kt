package com.example.calendy.data


interface BaseRepository<T> {
    suspend fun insert(entity: T)

    suspend fun delete(entity: T)

    suspend fun update(entity: T)
}