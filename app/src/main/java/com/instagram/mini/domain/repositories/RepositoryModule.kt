package com.instagram.mini.domain.repositories

import com.instagram.mini.data.ArtRepositoryImpl
import com.instagram.mini.data.UserRepositoryImpl
import org.koin.dsl.module

val RepositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<ArtRepository> { ArtRepositoryImpl(get(), get()) }
}