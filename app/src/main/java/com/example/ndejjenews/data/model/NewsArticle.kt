package com.example.ndejjenews.data.model

import com.google.firebase.Timestamp

/**
 * Data class representing a news article
 */
data class NewsArticle(
    val id: String = "",
    val title: String = "",
    val summary: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val category: String = "",
    val published: Boolean = true,
    val publishedAt: Timestamp = Timestamp.now(),
    val likeCount: Int = 0,
    val commentCount: Int = 0
) {
    /**
     * Get the publishedAt timestamp as milliseconds for easy date formatting
     */
    fun getPublishedAtMillis(): Long {
        return publishedAt.toDate().time
    }
} 