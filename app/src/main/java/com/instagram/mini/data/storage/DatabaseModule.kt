package com.instagram.mini.data.storage

import android.app.Application
import androidx.room.Room
import com.instagram.mini.data.storage.dao.ArtDao
import com.instagram.mini.data.storage.database.UserDatabase
import com.instagram.mini.data.storage.dao.UserDao
import com.instagram.mini.data.storage.database.ArtDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val DatabaseModule = module {
    fun provideUserDb(application: Application): UserDatabase {
        return Room.databaseBuilder(application, UserDatabase::class.java, "users")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideUserDao(database: UserDatabase): UserDao {
        return  database.userDao()
    }

    single { provideUserDb(androidApplication()) }
    single { provideUserDao(get()) }

    fun provideArtDb(application: Application): ArtDatabase {
        return Room.databaseBuilder(application, ArtDatabase::class.java, "arts")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideArtDao(database: ArtDatabase): ArtDao {
        return  database.artDao()
    }

    single { provideArtDb(androidApplication()) }
    single { provideArtDao(get()) }
}