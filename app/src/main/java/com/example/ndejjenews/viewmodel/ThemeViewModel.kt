package com.example.ndejjenews.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ndejjenews.utils.Constants.PREFERENCE_DARK_MODE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Extension property for DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * ViewModel for theme-related functionality
 */
class ThemeViewModel(private val context: Context) : ViewModel() {
    
    // Preferences key for dark mode
    private val darkModeKey = booleanPreferencesKey(PREFERENCE_DARK_MODE)
    
    // Dark mode state
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode
    
    // Initialize the ViewModel by loading theme preference
    init {
        viewModelScope.launch {
            context.dataStore.data.map { preferences ->
                // Default to false if preference is not set
                preferences[darkModeKey] ?: false
            }.collect { darkModeEnabled ->
                _isDarkMode.value = darkModeEnabled
            }
        }
    }
    
    /**
     * Toggle dark mode
     */
    fun toggleDarkMode() {
        viewModelScope.launch {
            val newValue = !_isDarkMode.value
            _isDarkMode.value = newValue
            
            // Save preference to DataStore
            context.dataStore.edit { preferences ->
                preferences[darkModeKey] = newValue
            }
        }
    }
}

/**
 * Factory for creating a ThemeViewModel with the context dependency
 */
class ThemeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            return ThemeViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 