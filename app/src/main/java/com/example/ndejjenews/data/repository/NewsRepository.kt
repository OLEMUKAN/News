package com.example.ndejjenews.data.repository

import com.example.ndejjenews.data.model.Comment
import com.example.ndejjenews.data.model.NewsArticle
import com.example.ndejjenews.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for news-related operations
 */
interface NewsRepository {
    /**
     * Get all news articles
     * @return Flow of Resource<List<NewsArticle>>
     */
    fun getAllArticles(): Flow<Resource<List<NewsArticle>>>
    
    /**
     * Get a specific news article by ID
     * @param articleId The ID of the article to retrieve
     * @return Flow of Resource<NewsArticle>
     */
    fun getArticleById(articleId: String): Flow<Resource<NewsArticle>>
    
    /**
     * Search for news articles by title or content
     * @param query The search query
     * @return Flow of Resource<List<NewsArticle>>
     */
    fun searchArticles(query: String): Flow<Resource<List<NewsArticle>>>
    
    /**
     * Filter news articles by category
     * @param category The category to filter by
     * @return Flow of Resource<List<NewsArticle>>
     */
    fun getArticlesByCategory(category: String): Flow<Resource<List<NewsArticle>>>
    
    /**
     * Save an article to the user's saved list
     * @param userId The ID of the user
     * @param articleId The ID of the article to save
     * @return Flow of Resource<Boolean> indicating success or failure
     */
    suspend fun saveArticle(userId: String, articleId: String): Flow<Resource<Boolean>>
    
    /**
     * Remove an article from the user's saved list
     * @param userId The ID of the user
     * @param articleId The ID of the article to unsave
     * @return Flow of Resource<Boolean> indicating success or failure
     */
    suspend fun unsaveArticle(userId: String, articleId: String): Flow<Resource<Boolean>>
    
    /**
     * Get all saved articles for a user
     * @param userId The ID of the user
     * @return Flow of Resource<List<NewsArticle>>
     */
    fun getSavedArticles(userId: String): Flow<Resource<List<NewsArticle>>>
    
    /**
     * Add a new news article
     * @param article The article to add
     * @return Flow of Resource<String> containing the ID of the new article
     */
    suspend fun addArticle(article: NewsArticle): Flow<Resource<String>>
    
    /**
     * Get comments for an article
     * @param articleId The ID of the article
     * @return Flow of Resource<List<Comment>>
     */
    fun getComments(articleId: String): Flow<Resource<List<Comment>>>
    
    /**
     * Add a comment to an article
     * @param comment The comment to add
     * @return Flow of Resource<String> containing the ID of the new comment
     */
    suspend fun addComment(comment: Comment): Flow<Resource<String>>
    
    /**
     * Like or unlike an article
     * @param userId The ID of the user
     * @param articleId The ID of the article
     * @param isLiked Whether the article is being liked (true) or unliked (false)
     * @return Flow of Resource<Boolean> indicating success or failure
     */
    suspend fun toggleLike(userId: String, articleId: String, isLiked: Boolean): Flow<Resource<Boolean>>
    
    /**
     * Check if a user has liked an article
     * @param userId The ID of the user
     * @param articleId The ID of the article
     * @return Flow of Resource<Boolean> indicating whether the user has liked the article
     */
    fun isArticleLikedByUser(userId: String, articleId: String): Flow<Resource<Boolean>>
} 