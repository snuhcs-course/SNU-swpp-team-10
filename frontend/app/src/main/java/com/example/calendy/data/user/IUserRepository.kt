package com.example.calendy.data.user

interface IUserRepository {
    suspend fun insert(user: User)
    suspend fun delete(user: User)
    suspend fun update(user: User)
}