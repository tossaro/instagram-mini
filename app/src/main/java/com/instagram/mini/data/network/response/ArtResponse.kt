package com.instagram.mini.data.network.response

import com.google.gson.annotations.SerializedName
import com.instagram.mini.domain.entities.Art

data class ArtResponse(
    @SerializedName("data") var data: Art,
)