package com.instagram.mini.domain.interactors

import com.instagram.mini.domain.entities.User
import com.instagram.mini.domain.repositories.UserRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class SetUserLocalIteractor: KoinComponent {
    private val userRepository: UserRepository by inject()
    suspend operator fun invoke(user: User) = userRepository.setUserLocal(user)
}