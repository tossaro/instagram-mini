package com.instagram.mini.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Art(
    @PrimaryKey
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("image_id") val image_id: String?,
    @SerializedName("credit_line") val credit_line: String?,
    @SerializedName("colorfulness") val colorfulness: String?,
    @SerializedName("inscriptions") val inscriptions: String?,
    @SerializedName("provenance_text") val provenance_text: String?,
    @SerializedName("publication_history") val publication_history: String?,
    @SerializedName("exhibition_history") val exhibition_history: String?,
    var favorite: Int
)