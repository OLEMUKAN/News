package com.example.ndejjenews.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ndejjenews.data.model.User
import com.example.ndejjenews.data.repository.AuthRepository
import com.example.ndejjenews.utils.Resource
import com.example.ndejjenews.utils.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.google.firebase.auth.FirebaseAuth

/**
 * ViewModel for authentication-related UI state and operations
 */
class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    
    // Login form state
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()
    
    // Registration form state
    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName.asStateFlow()
    
    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()
    
    // Common state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Authentication state
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()
    
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    // Registration success
    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess: StateFlow<Boolean> = _registrationSuccess.asStateFlow()
    
    init {
        // Check for current user on initialization
        observeAuthState()
    }
    
    /**
     * Observe authentication state changes
     */
    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                    is Resource.Success -> {
                        _isLoading.value = false
                        _currentUser.value = result.data
                        _isAuthenticated.value = result.data != null
                        
                        // Set admin status based on Firestore data
                        if (result.data != null) {
                            val uid = FirebaseAuth.getInstance().currentUser?.uid
                            if (uid != null) {
                                checkAdminStatus(uid)
                            } else {
                                _isAdmin.value = false
                            }
                        } else {
                            _isAdmin.value = false
                        }
                    }
                    is Resource.Error -> {
                        _errorMessage.value = result.message
                        _isLoading.value = false
                    }
                }
            }
        }
    }
    
    /**
     * Check if current user is an admin
     */
    private fun checkAdminStatus(uid: String) {
        viewModelScope.launch {
            authRepository.isCurrentUserAdmin().collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        _isAdmin.value = result.data
                    }
                    is Resource.Error -> {
                        _isAdmin.value = false
                        _errorMessage.value = result.message
                    }
                    is Resource.Loading -> {
                        // Do nothing for loading state
                    }
                }
            }
        }
    }
    
    /**
     * Update email input field
     * @param newEmail The new email value
     */
    fun updateEmail(newEmail: String) {
        _email.value = newEmail
        // Clear error when input changes
        _errorMessage.value = null
    }
    
    /**
     * Update password input field
     * @param newPassword The new password value
     */
    fun updatePassword(newPassword: String) {
        _password.value = newPassword
        // Clear error when input changes
        _errorMessage.value = null
    }
    
    /**
     * Update display name input field
     * @param newDisplayName The new display name value
     */
    fun updateDisplayName(newDisplayName: String) {
        _displayName.value = newDisplayName
        // Clear error when input changes
        _errorMessage.value = null
    }
    
    /**
     * Update confirm password input field
     * @param newConfirmPassword The new confirm password value
     */
    fun updateConfirmPassword(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
        // Clear error when input changes
        _errorMessage.value = null
    }
    
    /**
     * Validate login inputs
     * @return True if inputs are valid, false otherwise
     */
    private fun validateLoginInputs(): Boolean {
        if (!Validators.isValidEmail(_email.value)) {
            _errorMessage.value = "Please enter a valid email address"
            return false
        }
        
        if (!Validators.isValidPassword(_password.value)) {
            _errorMessage.value = "Password must be at least 6 characters"
            return false
        }
        
        return true
    }
    
    /**
     * Validate registration inputs
     * @return True if inputs are valid, false otherwise
     */
    private fun validateRegistrationInputs(): Boolean {
        if (!Validators.isValidEmail(_email.value)) {
            _errorMessage.value = "Please enter a valid email address"
            return false
        }
        
        if (_displayName.value.isBlank()) {
            _errorMessage.value = "Please enter a display name"
            return false
        }
        
        if (!Validators.isValidPassword(_password.value)) {
            _errorMessage.value = "Password must be at least 6 characters"
            return false
        }
        
        if (_password.value != _confirmPassword.value) {
            _errorMessage.value = "Passwords do not match"
            return false
        }
        
        return true
    }
    
    /**
     * Attempt to sign in with the current email and password
     */
    fun signIn() {
        if (!validateLoginInputs()) {
            return
        }
        
        viewModelScope.launch {
            authRepository.signIn(_email.value, _password.value).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _isLoading.value = true
                        _errorMessage.value = null
                    }
                    is Resource.Success -> {
                        _isLoading.value = false
                        _currentUser.value = result.data
                        _isAuthenticated.value = true
                        checkAdminStatus(FirebaseAuth.getInstance().currentUser?.uid ?: "")
                        
                        // Clear form fields after successful login
                        _email.value = ""
                        _password.value = ""
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = result.message
                    }
                }
            }
        }
    }
    
    /**
     * Register a new user with the current inputs
     */
    fun register() {
        if (!validateRegistrationInputs()) {
            return
        }
        
        viewModelScope.launch {
            authRepository.registerUser(
                email = _email.value,
                password = _password.value,
                displayName = _displayName.value
            ).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _isLoading.value = true
                        _errorMessage.value = null
                    }
                    is Resource.Success -> {
                        _isLoading.value = false
                        _currentUser.value = result.data
                        _isAuthenticated.value = true
                        _registrationSuccess.value = true
                        
                        // Clear form fields after successful registration
                        clearRegistrationForm()
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = result.message
                    }
                }
            }
        }
    }
    
    /**
     * Clear the registration form fields
     */
    fun clearRegistrationForm() {
        _email.value = ""
        _password.value = ""
        _displayName.value = ""
        _confirmPassword.value = ""
        _errorMessage.value = null
    }
    
    /**
     * Reset the registration success flag
     */
    fun resetRegistrationSuccess() {
        _registrationSuccess.value = false
    }
    
    /**
     * Sign out the current user
     */
    fun signOut() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                authRepository.signOut()
                _currentUser.value = null
                _isAuthenticated.value = false
                _isAdmin.value = false
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error signing out"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Clear any error messages
     */
    fun clearError() {
        _errorMessage.value = null
    }
} 