package com.instagram.mini.domain.repositories

import com.haroldadmin.cnradapter.NetworkResponse
import com.instagram.mini.data.network.response.ArtResponse
import com.instagram.mini.data.network.response.ArtsResponse
import com.instagram.mini.data.network.response.ErrorResponse
import com.instagram.mini.domain.entities.Art

interface ArtRepository {

    suspend fun getArt(id: Int): NetworkResponse<ArtResponse, ErrorResponse>
    suspend fun getArtLocal(id: Int): Art

    suspend fun getArts(limit: Int, page: Int): NetworkResponse<ArtsResponse, ErrorResponse>
    suspend fun getArtsLocal(offset: Int, limit: Int): List<Art>

    suspend fun getFavoritesLocal(offset: Int, limit: Int): List<Art>

    suspend fun searchArts(query: String): NetworkResponse<ArtsResponse, ErrorResponse>
    suspend fun searchArtsLocal(query: String): List<Art>

    suspend fun setArtsLocal(arts: MutableList<Art>)
    suspend fun updateArtsLocal(arts: MutableList<Art>)

}