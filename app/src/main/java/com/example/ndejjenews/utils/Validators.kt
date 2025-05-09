package com.example.ndejjenews.utils

/**
 * Utility class with validation functions for forms
 */
object Validators {
    /**
     * Validate email format
     * @param email The email to validate
     * @return True if the email is valid, false otherwise
     */
    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        return email.isNotBlank() && emailRegex.matches(email)
    }
    
    /**
     * Validate password strength
     * @param password The password to validate
     * @return True if password meets minimum requirements, false otherwise
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
    
    /**
     * Validate article title
     * @param title The title to validate
     * @return True if the title is valid, false otherwise
     */
    fun isValidArticleTitle(title: String): Boolean {
        return title.isNotBlank() && title.length in 5..100
    }
    
    /**
     * Validate article content
     * @param content The content to validate
     * @return True if the content is valid, false otherwise
     */
    fun isValidArticleContent(content: String): Boolean {
        return content.isNotBlank() && content.length >= 50
    }
    
    /**
     * Validate comment text
     * @param text The comment text to validate
     * @return True if the comment text is valid, false otherwise
     */
    fun isValidComment(text: String): Boolean {
        return text.isNotBlank() && text.length in 1..500
    }
    
    /**
     * Validate URL format
     * @param url The URL to validate
     * @return True if the URL is valid, false otherwise
     */
    fun isValidUrl(url: String): Boolean {
        val urlRegex = Regex("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$")
        return url.isNotBlank() && urlRegex.matches(url)
    }
} 