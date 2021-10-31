package com.instagram.mini.domain.interactors

import com.instagram.mini.domain.entities.Art
import com.instagram.mini.domain.repositories.ArtRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class SetArtsLocalInteractor: KoinComponent {
    private val artRepository: ArtRepository by inject()
    suspend operator fun invoke(arts: MutableList<Art>) = artRepository.setArtsLocal(arts)
}