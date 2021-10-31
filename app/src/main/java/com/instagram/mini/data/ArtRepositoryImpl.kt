package com.instagram.mini.data

import com.instagram.mini.data.storage.dao.ArtDao
import com.instagram.mini.domain.entities.Art
import com.instagram.mini.data.network.services.ArtServiceV1
import com.instagram.mini.domain.repositories.ArtRepository

class ArtRepositoryImpl(
    private val artServiceV1: ArtServiceV1,
    private val artDao: ArtDao
) : ArtRepository {

    override suspend fun getArt(id: Int) = artServiceV1.getArt(id)
    override suspend fun getArtLocal(id: Int): Art = artDao.getById(id)

    override suspend fun getArts(limit: Int, page: Int) = artServiceV1.getArts(limit, page)
    override suspend fun getArtsLocal(offset: Int, limit: Int): List<Art> = artDao.load(offset, limit)

    override suspend fun getFavoritesLocal(offset: Int, limit: Int): List<Art> = artDao.favorites(offset, limit)

    override suspend fun searchArts(query: String) = artServiceV1.searchArts(query)
    override suspend fun searchArtsLocal(query: String): List<Art> = artDao.search(query)

    override suspend fun setArtsLocal(arts: MutableList<Art>) = artDao.save(arts)
    override suspend fun updateArtsLocal(arts: MutableList<Art>) = artDao.update(arts)

}