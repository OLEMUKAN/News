package com.example.ndejjenews.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ndejjenews.data.model.Comment
import com.example.ndejjenews.data.model.NewsArticle
import com.example.ndejjenews.data.repository.NewsRepository
import com.example.ndejjenews.utils.Resource
import com.example.ndejjenews.utils.Constants.COLLECTION_USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel for news-related functionality
 */
class NewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {
    
    // Current user ID
    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid
    
    // News articles state
    private val _articles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val articles: StateFlow<List<NewsArticle>> = _articles
    
    // Selected article state
    private val _selectedArticle = MutableStateFlow<NewsArticle?>(null)
    val selectedArticle: StateFlow<NewsArticle?> = _selectedArticle
    
    // Comments for selected article
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments
    
    // Search results
    private val _searchResults = MutableStateFlow<List<NewsArticle>>(emptyList())
    val searchResults: StateFlow<List<NewsArticle>> = _searchResults
    
    // Saved articles
    private val _savedArticles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val savedArticles: StateFlow<List<NewsArticle>> = _savedArticles
    
    // Comment text
    private val _commentText = MutableStateFlow("")
    val commentText: StateFlow<String> = _commentText
    
    // Is article liked by current user
    private val _isArticleLiked = MutableStateFlow(false)
    val isArticleLiked: StateFlow<Boolean> = _isArticleLiked
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    // Currently selected category
    private val _selectedCategory = MutableStateFlow("all")
    val selectedCategory: StateFlow<String> = _selectedCategory
    
    // Article submission status
    private val _articleSubmissionStatus = MutableStateFlow<Boolean?>(null)
    val articleSubmissionStatus: StateFlow<Boolean?> = _articleSubmissionStatus
    
    // Initialize the ViewModel by loading articles
    init {
        loadAllArticles()
        // Load saved articles if user is logged in
        currentUserId?.let { loadSavedArticles() }
    }
    
    /**
     * Add a new article
     */
    fun addArticle(article: NewsArticle) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _articleSubmissionStatus.value = null
            
            newsRepository.addArticle(article).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        _isLoading.value = false
                        _articleSubmissionStatus.value = true
                        // Refresh articles list
                        loadAllArticles()
                    }
                    is Resource.Error -> {
                        _errorMessage.value = result.message
                        _isLoading.value = false
                        _articleSubmissionStatus.value = false
                    }
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }
    
    /**
     * Reset article submission status
     */
    fun resetArticleSubmissionStatus() {
        _articleSubmissionStatus.value = null
    }
    
    /**
     * Load all news articles
     */
    fun loadAllArticles() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            newsRepository.getAllArticles().collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        _articles.value = result.data ?: emptyList()
                        _isLoading.value = false
                    }
                    is Resource.Error -> {
                        _errorMessage.value = result.message
                        _isLoading.value = false
                    }
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }
    
    /**
     * Load articles by category
     */
    fun loadArticlesByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _selectedCategory.value = category
            
            newsRepository.getArticlesByCategory(category).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        _articles.value = result.data ?: emptyList()
                        _isLoading.value = false
                    }
                    is Resource.Error -> {
                        _errorMessage.value = result.message
                        _isLoading.value = false
                    }
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }
    
    /**
     * Search for articles
     */
    fun searchArticles(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            newsRepository.searchArticles(query).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        _searchResults.value = result.data ?: emptyList()
                        _isLoading.value = false
                    }
                    is Resource.Error -> {
                        _errorMessage.value = result.message
                        _isLoading.value = false
                    }
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }
    
    /**
     * Load a specific article by ID
     */
    fun loadArticle(articleId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            newsRepository.getArticleById(articleId).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        _selectedArticle.value = result.data
                        _isLoading.value = false
                        
                        // Load comments for this article
                        loadComments(articleId)
                        
                        // Check if the current user has liked this article
                        currentUserId?.let { userId ->
                            checkIfArticleLiked(userId, articleId)
                        }
                    }
                    is Resource.Error -> {
                        _errorMessage.value = result.message
                        _isLoading.value = false
                    }
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }
    
    /**
     * Check if the current user has liked an article
     */
    private fun checkIfArticleLiked(userId: String, articleId: String) {
        viewModelScope.launch {
            newsRepository.isArticleLikedByUser(userId, articleId).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        _isArticleLiked.value = result.data ?: false
                    }
                    is Resource.Error -> {
                        // Just log the error, don't show to user
                        _isArticleLiked.value = false
                    }
                    is Resource.Loading -> {
                        // No need to show loading state for this
                    }
                }
            }
        }
    }
    
    /**
     * Load comments for an article
     */
    private fun loadComments(articleId: String) {
        viewModelScope.launch {
            newsRepository.getComments(articleId).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        _comments.value = result.data ?: emptyList()
                    }
                    is Resource.Error -> {
                        // Not showing comment loading errors to the user
                        // Just log it or handle silently
                        _comments.value = emptyList()
                    }
                    is Resource.Loading -> {
                        // No need to show loading state for comments
                    }
                }
            }
        }
    }
    
    /**
     * Update comment text input
     */
    fun updateCommentText(text: String) {
        _commentText.value = text
    }
    
    /**
     * Add a comment to the selected article
     */
    fun addComment() {
        val articleId = _selectedArticle.value?.id ?: return
        val userId = currentUserId ?: return
        
        // First try to get display name from Firebase Auth
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val displayNameFromAuth = firebaseUser?.displayName
        
        viewModelScope.launch {
            // If display name is not in Firebase Auth, try to get it from Firestore
            val finalDisplayName = if (displayNameFromAuth.isNullOrBlank()) {
                try {
                    // Get user document from Firestore
                    val userDoc = FirebaseFirestore.getInstance()
                        .collection(COLLECTION_USERS)
                        .document(userId)
                        .get()
                        .await()
                    
                    // Extract display name from user document
                    userDoc.getString("displayName") ?: "Anonymous"
                } catch (e: Exception) {
                    "Anonymous" // Fallback if Firestore fetch fails
                }
            } else {
                displayNameFromAuth
            }
            
            val text = _commentText.value.trim()
            
            if (text.isBlank()) {
                _errorMessage.value = "Comment cannot be empty"
                return@launch
            }
            
            val comment = Comment(
                articleId = articleId,
                userId = userId,
                userDisplayName = finalDisplayName,
                text = text
            )
            
            newsRepository.addComment(comment).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        // Clear the comment text after successful submission
                        _commentText.value = ""
                        // Comments will be updated automatically through the real-time listener
                    }
                    is Resource.Error -> {
                        _errorMessage.value = result.message
                    }
                    is Resource.Loading -> {
                        // Comment submission loading state
                    }
                }
            }
        }
    }
    
    /**
     * Toggle like on an article
     */
    fun toggleLike(articleId: String) {
        val userId = currentUserId ?: return
        
        // Determine if this is a like or unlike action based on current state
        val isLiking = !_isArticleLiked.value
        
        viewModelScope.launch {
            newsRepository.toggleLike(userId, articleId, isLiking).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        // Update the liked state
                        _isArticleLiked.value = isLiking
                        // The article will be updated automatically through real-time listener
                    }
                    is Resource.Error -> {
                        _errorMessage.value = result.message
                    }
                    is Resource.Loading -> {
                        // Like toggling loading state
                    }
                }
            }
        }
    }
    
    /**
     * Save an article
     */
    fun saveArticle(articleId: String) {
        val userId = currentUserId ?: return
        
        viewModelScope.launch {
            newsRepository.saveArticle(userId, articleId).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        // Refresh saved articles after adding
                        loadSavedArticles()
                    }
                    is Resource.Error -> {
                        _errorMessage.value = result.message
                    }
                    is Resource.Loading -> {
                        // Save article loading state
                    }
                }
            }
        }
    }
    
    /**
     * Unsave an article
     */
    fun unsaveArticle(articleId: String) {
        val userId = currentUserId ?: return
        
        viewModelScope.launch {
            newsRepository.unsaveArticle(userId, articleId).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        // Refresh saved articles after removing
                        loadSavedArticles()
                    }
                    is Resource.Error -> {
                        _errorMessage.value = result.message
                    }
                    is Resource.Loading -> {
                        // Unsave article loading state
                    }
                }
            }
        }
    }
    
    /**
     * Load saved articles
     */
    fun loadSavedArticles() {
        val userId = currentUserId ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            newsRepository.getSavedArticles(userId).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        _savedArticles.value = result.data ?: emptyList()
                        _isLoading.value = false
                    }
                    is Resource.Error -> {
                        _errorMessage.value = result.message
                        _isLoading.value = false
                    }
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }
    
    /**
     * Clear the current error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}

/**
 * Factory for creating a NewsViewModel with the repository dependency
 */
class NewsViewModelFactory(private val newsRepository: NewsRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(newsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 