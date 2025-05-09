package com.example.ndejjenews.data.repository

import com.example.ndejjenews.data.model.User
import com.example.ndejjenews.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations
 */
interface AuthRepository {
    /**
     * Sign in with email and password
     * @param email User's email
     * @param password User's password
     * @return Flow of Resource<User>
     */
    suspend fun signIn(email: String, password: String): Flow<Resource<User>>
    
    /**
     * Register a new user with email and password
     * @param email User's email
     * @param password User's password
     * @param displayName User's display name
     * @return Flow of Resource<User>
     */
    suspend fun registerUser(email: String, password: String, displayName: String): Flow<Resource<User>>
    
    /**
     * Sign out the current user
     */
    suspend fun signOut()
    
    /**
     * Get the current authenticated user
     * @return Flow of Resource<User?>
     */
    fun getCurrentUser(): Flow<Resource<User?>>
    
    /**
     * Check if the current user is an admin
     * @return Flow of Resource<Boolean>
     */
    fun isCurrentUserAdmin(): Flow<Resource<Boolean>>
} 