package com.instagram.mini.data.network.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("Response") val response: String? = "",
    @SerializedName("HasWarning") val hasWarning: Boolean? = true,
    @SerializedName("Message") val message: String? = ""
)