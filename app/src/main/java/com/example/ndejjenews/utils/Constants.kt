package com.example.ndejjenews.utils

/**
 * Object to store application-wide constants
 */
object Constants {
    // Firestore Collection Names
    const val COLLECTION_USERS = "users"
    const val COLLECTION_ARTICLES = "articles"
    const val COLLECTION_COMMENTS = "comments"
    
    // Navigation Routes
    const val ROUTE_LOGIN = "login"
    const val ROUTE_REGISTER = "register"
    const val ROUTE_HOME = "home"
    const val ROUTE_ARTICLE_DETAILS = "article/{articleId}"
    const val ROUTE_SAVED = "saved"
    const val ROUTE_SEARCH = "search"
    const val ROUTE_ADMIN = "admin"
    const val ROUTE_SETTINGS = "settings"
    
    // Article Categories
    const val CATEGORY_ALL = "all"
    const val CATEGORY_ACADEMICS = "academics"
    const val CATEGORY_SPORTS = "sports"
    const val CATEGORY_EVENTS = "events"
    const val CATEGORY_ANNOUNCEMENTS = "announcements"
    
    // Preferences
    const val PREFERENCE_DARK_MODE = "dark_mode"
    
    // Animation Constants
    const val ANIMATION_DURATION_SHORT = 150
    const val ANIMATION_DURATION_MEDIUM = 300
    const val ANIMATION_DURATION_LONG = 500
} 