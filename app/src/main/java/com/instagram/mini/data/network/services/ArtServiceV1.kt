package com.instagram.mini.data.network.services

import com.haroldadmin.cnradapter.NetworkResponse
import com.instagram.mini.data.network.response.ArtResponse
import com.instagram.mini.data.network.response.ArtsResponse
import com.instagram.mini.data.network.response.ErrorResponse
import com.instagram.mini.domain.entities.Art
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ArtServiceV1 {
    @GET("artworks/{id}")
    suspend fun getArt(
        @Path("id") id: Int
    ): NetworkResponse<ArtResponse, ErrorResponse>

    @GET("artworks")
    suspend fun getArts(
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): NetworkResponse<ArtsResponse, ErrorResponse>

    @GET("artworks/search")
    suspend fun searchArts(
        @Query("q") query: String
    ): NetworkResponse<ArtsResponse, ErrorResponse>
}