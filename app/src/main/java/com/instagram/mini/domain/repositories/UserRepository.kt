package com.instagram.mini.domain.repositories

import com.instagram.mini.domain.entities.User

interface UserRepository {
    suspend fun getUserLocal(): User
    suspend fun setUserLocal(user: User): Long
    suspend fun deleteAllUserLocal()
}