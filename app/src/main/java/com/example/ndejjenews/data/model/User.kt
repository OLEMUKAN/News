package com.example.ndejjenews.data.model

/**
 * Data class representing a user in the app
 */
data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val admin: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) 