package com.instagram.mini.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.instagram.mini.data.storage.dao.ArtDao
import com.instagram.mini.domain.entities.Art

@Database(entities = [Art::class], version = 1)
abstract class ArtDatabase : RoomDatabase() {
    abstract fun artDao(): ArtDao
}