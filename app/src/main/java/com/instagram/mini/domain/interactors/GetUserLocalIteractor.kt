package com.instagram.mini.domain.interactors

import com.instagram.mini.domain.repositories.UserRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetUserLocalIteractor: KoinComponent {
    private val userRepository: UserRepository by inject()
    suspend operator fun invoke() = userRepository.getUserLocal()
}