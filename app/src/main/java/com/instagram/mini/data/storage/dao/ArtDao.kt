package com.instagram.mini.data.storage.dao

import androidx.room.*
import com.instagram.mini.domain.entities.Art

@Dao
interface ArtDao {
    @Query("SELECT * FROM art WHERE id =:id")
    suspend fun getById(id: Int): Art

    @Query("SELECT * FROM art LIMIT :limit OFFSET :offset")
    suspend fun load(offset: Int, limit: Int): List<Art>

    @Query("SELECT * FROM art WHERE title like :query")
    suspend fun search(query: String): List<Art>

    @Query("SELECT * FROM art WHERE favorite = 1 LIMIT :limit OFFSET :offset")
    suspend fun favorites(offset: Int, limit: Int): List<Art>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun save(arts: List<Art>)

    @Update
    suspend fun update(arts: List<Art>)
}