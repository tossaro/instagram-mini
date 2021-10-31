package com.instagram.mini.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val token: String
)