package com.instagram.mini.domain.interactors

import com.instagram.mini.domain.repositories.ArtRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetArtsLocalInteractor: KoinComponent {
    private val artRepository: ArtRepository by inject()
    suspend operator fun invoke(offset: Int, limit: Int) = artRepository.getArtsLocal(offset, limit)
}