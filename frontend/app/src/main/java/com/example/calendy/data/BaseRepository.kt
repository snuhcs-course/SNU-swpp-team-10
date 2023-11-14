package com.example.calendy.data

interface BaseRepository<T> {
    suspend fun insert(entity: T): Long

    suspend fun update(entity: T)

    suspend fun delete(entity: T)
}