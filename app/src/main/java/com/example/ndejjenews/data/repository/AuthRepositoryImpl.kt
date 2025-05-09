package com.example.ndejjenews.data.repository

import com.example.ndejjenews.data.model.User
import com.example.ndejjenews.utils.Constants.COLLECTION_USERS
import com.example.ndejjenews.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * Implementation of the AuthRepository interface for Firebase Authentication
 */
class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {
    
    override suspend fun signIn(email: String, password: String): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading)
            
            // Attempt to sign in with Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            
            // Get the user ID from the auth result
            val userId = authResult.user?.uid
                ?: throw Exception("Authentication successful but user ID is null")
                
            // Fetch the user document from Firestore
            val userDoc = firestore.collection(COLLECTION_USERS).document(userId).get().await()
            
            if (userDoc.exists()) {
                // User exists in Firestore, convert to User model
                val user = userDoc.toObject(User::class.java)?.copy(id = userId)
                    ?: throw Exception("User document exists but couldn't be converted")
                    
                emit(Resource.Success(user))
            } else {
                // User authenticated but doesn't exist in Firestore
                // Create a new user document
                val newUser = User(
                    id = userId,
                    email = email,
                    displayName = email.substringBefore('@'),
                    admin = false,
                    createdAt = System.currentTimeMillis()
                )
                
                // Save the new user to Firestore
                firestore.collection(COLLECTION_USERS).document(userId).set(newUser).await()
                
                emit(Resource.Success(newUser))
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            emit(Resource.Error("User not found. Please check your email."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            emit(Resource.Error("Invalid password. Please try again."))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }
    
    override suspend fun registerUser(email: String, password: String, displayName: String): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading)
            
            // Create user in Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            
            // Get the user ID from the auth result
            val userId = authResult.user?.uid
                ?: throw Exception("Registration successful but user ID is null")
            
            // Set display name in Firebase Auth profile
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            
            // Update the Firebase Auth user profile
            authResult.user?.updateProfile(profileUpdates)?.await()
            
            // Create a new user document in Firestore
            val newUser = User(
                id = userId,
                email = email,
                displayName = displayName.ifBlank { email.substringBefore('@') },
                admin = false,
                createdAt = System.currentTimeMillis()
            )
            
            // Save the new user to Firestore
            firestore.collection(COLLECTION_USERS).document(userId).set(newUser).await()
            
            emit(Resource.Success(newUser))
        } catch (e: FirebaseAuthWeakPasswordException) {
            emit(Resource.Error("Password is too weak. Please use at least 6 characters."))
        } catch (e: FirebaseAuthUserCollisionException) {
            emit(Resource.Error("An account with this email already exists."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            emit(Resource.Error("Invalid email format. Please check your email."))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred during registration"))
        }
    }
    
    override suspend fun signOut() {
        auth.signOut()
    }
    
    override fun getCurrentUser(): Flow<Resource<User?>> = callbackFlow {
        trySend(Resource.Loading)
        
        // Listen for authentication state changes
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            
            if (firebaseUser == null) {
                // Not authenticated
                trySend(Resource.Success(null))
            } else {
                // Authenticated, fetch user document from Firestore
                firestore.collection(COLLECTION_USERS).document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val user = document.toObject(User::class.java)?.copy(id = firebaseUser.uid)
                            trySend(Resource.Success(user))
                        } else {
                            // User authenticated but no Firestore document
                            val newUser = User(
                                id = firebaseUser.uid,
                                email = firebaseUser.email ?: "",
                                displayName = firebaseUser.displayName ?: firebaseUser.email?.substringBefore('@') ?: "",
                                admin = false,
                                createdAt = System.currentTimeMillis()
                            )
                            trySend(Resource.Success(newUser))
                        }
                    }
                    .addOnFailureListener { e ->
                        trySend(Resource.Error(e.message ?: "Error fetching user data"))
                    }
            }
        }
        
        // Register the listener
        auth.addAuthStateListener(authStateListener)
        
        // Clean up when the flow collector is cancelled
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }
    
    override fun isCurrentUserAdmin(): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading)
            
            val userId = auth.currentUser?.uid
            
            if (userId == null) {
                emit(Resource.Success(false))
                return@flow
            }
            
            val userDoc = firestore.collection(COLLECTION_USERS).document(userId).get().await()
            
            if (userDoc.exists()) {
                // Check for the 'admin' field that was found in the logs
                val isAdmin = userDoc.getBoolean("admin") ?: false
                emit(Resource.Success(isAdmin))
            } else {
                emit(Resource.Success(false))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error checking admin status"))
        }
    }
} 