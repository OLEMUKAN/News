package com.example.ndejjenews.utils

/**
 * A sealed class to handle different states of data loading from repositories
 */
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
    
    /**
     * Check if the resource is in a successful state with data
     */
    fun isSuccess(): Boolean = this is Success
    
    /**
     * Check if the resource is in an error state
     */
    fun isError(): Boolean = this is Error
    
    /**
     * Check if the resource is in a loading state
     */
    fun isLoading(): Boolean = this is Loading
    
    /**
     * Get data safely from the resource regardless of state
     */
    fun getDataOrNull(): T? = if (this is Success) data else null
} 