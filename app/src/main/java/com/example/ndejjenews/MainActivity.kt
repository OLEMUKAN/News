package com.example.ndejjenews

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.ndejjenews.data.repository.AuthRepositoryImpl
import com.example.ndejjenews.ui.navigation.AppNavigation
import com.example.ndejjenews.ui.theme.NdejjeNewsTheme
import com.example.ndejjenews.viewmodel.AuthViewModel
import com.example.ndejjenews.viewmodel.AuthViewModelFactory
import com.example.ndejjenews.viewmodel.ThemeViewModel
import com.example.ndejjenews.viewmodel.ThemeViewModelFactory
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            // Failed to initialize Firebase
        }
        
        enableEdgeToEdge()
        
        // Initialize repositories
        val authRepository = AuthRepositoryImpl()
        
        setContent {
            // Initialize ViewModels
            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(authRepository)
            )
            
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModelFactory(applicationContext)
            )
            
            // Get dark mode preference from ThemeViewModel
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()
            
            // Use system default if theme not set explicitly
            val darkTheme = isDarkMode || isSystemInDarkTheme()
            
            NdejjeNewsTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Set up navigation
                    val navController = rememberNavController()
                    
                    // Set up navigation with ViewModels
                    AppNavigation(
                        navController = navController,
                        authViewModel = authViewModel,
                        themeViewModel = themeViewModel
                    )
                }
            }
        }
    }
}