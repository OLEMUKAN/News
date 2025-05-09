package com.example.ndejjenews.data.model

/**
 * Data class representing a comment on a news article
 */
data class Comment(
    val id: String = "",
    val articleId: String = "",
    val userId: String = "",
    val userDisplayName: String = "",
    val text: String = "",
    val createdAt: Long = System.currentTimeMillis()
) 