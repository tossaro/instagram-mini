package com.instagram.mini.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.instagram.mini.data.storage.dao.UserDao
import com.instagram.mini.domain.entities.User

@Database(entities = [User::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}