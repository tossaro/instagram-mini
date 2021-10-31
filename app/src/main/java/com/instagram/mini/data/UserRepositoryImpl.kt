package com.instagram.mini.data

import com.instagram.mini.data.storage.dao.UserDao
import com.instagram.mini.domain.entities.User
import com.instagram.mini.domain.repositories.UserRepository

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun getUserLocal(): User = userDao.load()
    override suspend fun setUserLocal(user: User): Long = userDao.save(user)
    override suspend fun deleteAllUserLocal() = userDao.deleteAll()
}