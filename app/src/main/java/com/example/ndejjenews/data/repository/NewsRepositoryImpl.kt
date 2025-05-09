package com.example.ndejjenews.data.repository

import com.example.ndejjenews.data.model.Comment
import com.example.ndejjenews.data.model.NewsArticle
import com.example.ndejjenews.utils.Constants.COLLECTION_ARTICLES
import com.example.ndejjenews.utils.Constants.COLLECTION_COMMENTS
import com.example.ndejjenews.utils.Constants.COLLECTION_USERS
import com.example.ndejjenews.utils.Resource
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Implementation of NewsRepository interface using Firebase Firestore
 */
class NewsRepositoryImpl : NewsRepository {
    
    // Simple Firestore instance
    private val firestore = FirebaseFirestore.getInstance()
    private val articlesCollection = firestore.collection(COLLECTION_ARTICLES)
    private val commentsCollection = firestore.collection(COLLECTION_COMMENTS)
    private val usersCollection = firestore.collection(COLLECTION_USERS)
    
    /**
     * Get all news articles with real-time updates
     */
    override fun getAllArticles(): Flow<Resource<List<NewsArticle>>> = callbackFlow {
        // Start with loading state
        trySend(Resource.Loading)
        
        // Register listener for real-time updates
        val listener = articlesCollection
            .whereEqualTo("published", true)
            .orderBy("publishedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Unknown error occurred"))
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val articles = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(NewsArticle::class.java)?.copy(id = doc.id)
                    }
                    trySend(Resource.Success(articles))
                }
            }
        
        // Clean up listener when the flow is canceled
        awaitClose { listener.remove() }
    }
    
    /**
     * Get a specific news article by ID
     */
    override fun getArticleById(articleId: String): Flow<Resource<NewsArticle>> = callbackFlow {
        trySend(Resource.Loading)
        
        val listener = articlesCollection.document(articleId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Unknown error occurred"))
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    val article = snapshot.toObject(NewsArticle::class.java)?.copy(id = snapshot.id)
                    if (article != null) {
                        trySend(Resource.Success(article))
                    } else {
                        trySend(Resource.Error("Failed to parse article data"))
                    }
                } else {
                    trySend(Resource.Error("Article not found"))
                }
            }
        
        awaitClose { listener.remove() }
    }
    
    /**
     * Search for news articles by title or content
     */
    override fun searchArticles(query: String): Flow<Resource<List<NewsArticle>>> = flow {
        emit(Resource.Loading)
        
        try {
            // Firestore doesn't support full-text search, so we need to use partial matching
            // This is a simple implementation that might need to be replaced with a more 
            // sophisticated solution like Algolia or ElasticSearch for production use
            val queryLower = query.lowercase()
            
            val snapshot = articlesCollection
                .whereEqualTo("published", true)
                .get()
                .await()
            
            val articles = snapshot.documents.mapNotNull { doc ->
                val article = doc.toObject(NewsArticle::class.java)?.copy(id = doc.id)
                
                // Simple client-side filtering - not efficient for large datasets
                if (article != null && (
                        article.title.lowercase().contains(queryLower) ||
                        article.content.lowercase().contains(queryLower) ||
                        article.summary.lowercase().contains(queryLower)
                    )) {
                    article
                } else {
                    null
                }
            }
            
            emit(Resource.Success(articles))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred during search"))
        }
    }
    
    /**
     * Filter news articles by category
     */
    override fun getArticlesByCategory(category: String): Flow<Resource<List<NewsArticle>>> = flow {
        emit(Resource.Loading)
        
        try {
            val snapshot = if (category == "all") {
                articlesCollection
                    .whereEqualTo("published", true)
                    .orderBy("publishedAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
            } else {
                articlesCollection
                    .whereEqualTo("category", category)
                    .whereEqualTo("published", true)
                    .orderBy("publishedAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }
            
            val articles = snapshot.documents.mapNotNull { doc ->
                doc.toObject(NewsArticle::class.java)?.copy(id = doc.id)
            }
            
            emit(Resource.Success(articles))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }
    
    /**
     * Save an article to the user's saved list
     */
    override suspend fun saveArticle(userId: String, articleId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        
        try {
            // Add article ID to user's saved_articles array
            usersCollection.document(userId)
                .update("savedArticles", FieldValue.arrayUnion(articleId))
                .await()
            
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to save article"))
        }
    }
    
    /**
     * Remove an article from the user's saved list
     */
    override suspend fun unsaveArticle(userId: String, articleId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        
        try {
            // Remove article ID from user's saved_articles array
            usersCollection.document(userId)
                .update("savedArticles", FieldValue.arrayRemove(articleId))
                .await()
            
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to unsave article"))
        }
    }
    
    /**
     * Get all articles saved by a user
     */
    override fun getSavedArticles(userId: String): Flow<Resource<List<NewsArticle>>> = flow {
        emit(Resource.Loading)
        
        try {
            // Get the user document to access their saved article IDs
            val userDoc = usersCollection.document(userId).get().await()
            val savedArticleIds = userDoc.get("savedArticles") as? List<String> ?: emptyList()
            
            if (savedArticleIds.isEmpty()) {
                emit(Resource.Success(emptyList()))
                return@flow
            }
            
            // Firestore limits 'in' queries to 10 items, so we might need multiple queries
            val articles = mutableListOf<NewsArticle>()
            
            // Process in batches of 10
            savedArticleIds.chunked(10).forEach { batch ->
                val snapshot = articlesCollection
                    .whereIn("id", batch)
                    .whereEqualTo("published", true)
                    .get()
                    .await()
                
                articles.addAll(snapshot.documents.mapNotNull { doc ->
                    doc.toObject(NewsArticle::class.java)?.copy(id = doc.id)
                })
            }
            
            emit(Resource.Success(articles))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to get saved articles"))
        }
    }
    
    /**
     * Add a new news article
     */
    override suspend fun addArticle(article: NewsArticle): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        
        try {
            // Generate a new ID if one isn't provided
            val articleId = if (article.id.isBlank()) UUID.randomUUID().toString() else article.id
            
            // Create the article with the timestamp
            val articleToAdd = article.copy(
                id = articleId,
                publishedAt = Timestamp.now()
            )
            
            // Add to Firestore
            articlesCollection.document(articleId)
                .set(articleToAdd)
                .await()
            
            emit(Resource.Success(articleId))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to add article"))
        }
    }
    
    /**
     * Get comments for an article
     */
    override fun getComments(articleId: String): Flow<Resource<List<Comment>>> = callbackFlow {
        trySend(Resource.Loading)
        
        val listener = commentsCollection
            .whereEqualTo("articleId", articleId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Unknown error occurred"))
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val comments = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Comment::class.java)?.copy(id = doc.id)
                    }
                    trySend(Resource.Success(comments))
                }
            }
        
        awaitClose { listener.remove() }
    }
    
    /**
     * Add a comment to an article
     */
    override suspend fun addComment(comment: Comment): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        
        try {
            // Generate a new ID if one isn't provided
            val commentId = if (comment.id.isBlank()) UUID.randomUUID().toString() else comment.id
            
            // Create the comment with metadata
            val commentToAdd = comment.copy(
                id = commentId,
                createdAt = System.currentTimeMillis()
            )
            
            // Add to Firestore
            commentsCollection.document(commentId)
                .set(commentToAdd)
                .await()
            
            // Update comment count on the article
            articlesCollection.document(comment.articleId)
                .update("commentCount", FieldValue.increment(1))
                .await()
            
            emit(Resource.Success(commentId))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to add comment"))
        }
    }
    
    /**
     * Like or unlike an article
     */
    override suspend fun toggleLike(userId: String, articleId: String, isLiked: Boolean): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        
        try {
            // Create/update user's likes in users collection
            val userLikesRef = usersCollection.document(userId)
                .collection("likes")
                .document(articleId)
            
            if (isLiked) {
                // Like the article
                userLikesRef.set(mapOf(
                    "articleId" to articleId,
                    "likedAt" to System.currentTimeMillis()
                )).await()
                
                // Increment like count on the article
                articlesCollection.document(articleId)
                    .update("likeCount", FieldValue.increment(1))
                    .await()
            } else {
                // Unlike the article
                userLikesRef.delete().await()
                
                // Decrement like count on the article
                articlesCollection.document(articleId)
                    .update("likeCount", FieldValue.increment(-1))
                    .await()
            }
            
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to toggle like"))
        }
    }
    
    /**
     * Check if a user has liked an article
     */
    override fun isArticleLikedByUser(userId: String, articleId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        
        try {
            // Check if the user has a like document for this article
            val userLikesRef = usersCollection.document(userId)
                .collection("likes")
                .document(articleId)
            
            val document = userLikesRef.get().await()
            
            // If document exists, the user has liked the article
            emit(Resource.Success(document.exists()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to check if article is liked"))
        }
    }
} 