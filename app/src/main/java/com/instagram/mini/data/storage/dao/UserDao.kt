package com.instagram.mini.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.instagram.mini.domain.entities.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    suspend fun load(): User

    @Insert(onConflict = REPLACE)
    suspend fun save(user: User): Long

    @Query("DELETE FROM user")
    suspend fun deleteAll()
}