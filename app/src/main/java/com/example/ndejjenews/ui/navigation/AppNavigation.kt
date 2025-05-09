package com.example.ndejjenews.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ndejjenews.data.repository.NewsRepositoryImpl
import com.example.ndejjenews.ui.screens.AdminScreen
import com.example.ndejjenews.ui.screens.ArticleDetailsScreen
import com.example.ndejjenews.ui.screens.HomeScreen
import com.example.ndejjenews.ui.screens.LoginScreen
import com.example.ndejjenews.ui.screens.RegisterScreen
import com.example.ndejjenews.ui.screens.SavedArticlesScreen
import com.example.ndejjenews.ui.screens.SearchScreen
import com.example.ndejjenews.ui.screens.SettingsScreen
import com.example.ndejjenews.utils.Constants.ANIMATION_DURATION_MEDIUM
import com.example.ndejjenews.utils.Constants.ROUTE_ADMIN
import com.example.ndejjenews.utils.Constants.ROUTE_ARTICLE_DETAILS
import com.example.ndejjenews.utils.Constants.ROUTE_HOME
import com.example.ndejjenews.utils.Constants.ROUTE_LOGIN
import com.example.ndejjenews.utils.Constants.ROUTE_REGISTER
import com.example.ndejjenews.utils.Constants.ROUTE_SAVED
import com.example.ndejjenews.utils.Constants.ROUTE_SEARCH
import com.example.ndejjenews.utils.Constants.ROUTE_SETTINGS
import com.example.ndejjenews.viewmodel.AuthViewModel
import com.example.ndejjenews.viewmodel.NewsViewModel
import com.example.ndejjenews.viewmodel.NewsViewModelFactory
import com.example.ndejjenews.viewmodel.ThemeViewModel

/**
 * Main navigation graph for the app
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel
) {
    // Observe authentication state
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val isAdmin by authViewModel.isAdmin.collectAsState()
    
    // Initialize NewsViewModel
    val newsRepository = NewsRepositoryImpl()
    val newsViewModel: NewsViewModel = viewModel(
        factory = NewsViewModelFactory(newsRepository)
    )
    
    // Start destination depends on authentication state
    val startDestination = if (isAuthenticated) ROUTE_HOME else ROUTE_LOGIN
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login screen - only accessible when not authenticated
        composable(
            route = ROUTE_LOGIN,
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideIntoContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideOutOfContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideIntoContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideOutOfContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            if (isAuthenticated) {
                // Navigate to home if already authenticated
                navController.navigate(ROUTE_HOME) {
                    popUpTo(ROUTE_LOGIN) { inclusive = true }
                }
            } else {
                // Show login screen
                LoginScreen(authViewModel, navController)
            }
        }
        
        // Registration screen
        composable(
            route = ROUTE_REGISTER,
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideIntoContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideOutOfContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideIntoContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideOutOfContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            if (isAuthenticated) {
                // Navigate to home if already authenticated
                navController.navigate(ROUTE_HOME) {
                    popUpTo(ROUTE_REGISTER) { inclusive = true }
                }
            } else {
                // Show registration screen
                RegisterScreen(authViewModel, navController)
            }
        }
        
        // Home screen - news feed
        composable(
            route = ROUTE_HOME,
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION_MEDIUM))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION_MEDIUM))
            }
        ) {
            if (!isAuthenticated) {
                // Navigate to login if not authenticated
                navController.navigate(ROUTE_LOGIN) {
                    popUpTo(ROUTE_HOME) { inclusive = true }
                }
            } else {
                // Show home screen
                HomeScreen(
                    navController = navController,
                    newsViewModel = newsViewModel,
                    authViewModel = authViewModel
                )
            }
        }
        
        // Article details screen
        composable(
            route = ROUTE_ARTICLE_DETAILS,
            arguments = listOf(
                navArgument("articleId") {
                    type = NavType.StringType
                }
            ),
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideIntoContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideOutOfContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideIntoContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideOutOfContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) { backStackEntry ->
            if (!isAuthenticated) {
                navController.navigate(ROUTE_LOGIN)
            } else {
                val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
                // Show article details screen
                ArticleDetailsScreen(
                    articleId = articleId,
                    navController = navController,
                    newsViewModel = newsViewModel
                )
            }
        }
        
        // Saved articles screen
        composable(
            route = ROUTE_SAVED,
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideIntoContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.Up
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideOutOfContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.Down
                )
            }
        ) {
            if (!isAuthenticated) {
                navController.navigate(ROUTE_LOGIN)
            } else {
                // Show saved articles screen
                SavedArticlesScreen(
                    newsViewModel = newsViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onArticleClick = { articleId ->
                        navController.navigate("article/$articleId")
                    }
                )
            }
        }
        
        // Search screen
        composable(
            route = ROUTE_SEARCH,
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideIntoContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.Up
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideOutOfContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.Down
                )
            }
        ) {
            if (!isAuthenticated) {
                navController.navigate(ROUTE_LOGIN)
            } else {
                // Show search screen
                SearchScreen(
                    newsViewModel = newsViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onArticleClick = { articleId ->
                        navController.navigate("article/$articleId")
                    }
                )
            }
        }
        
        // Settings screen
        composable(
            route = ROUTE_SETTINGS,
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideIntoContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.Up
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideOutOfContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.Down
                )
            }
        ) {
            if (!isAuthenticated) {
                navController.navigate(ROUTE_LOGIN)
            } else {
                // Show settings screen
                SettingsScreen(
                    themeViewModel = themeViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
        
        // Admin screen - only accessible for admin users
        composable(
            route = ROUTE_ADMIN,
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideIntoContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                slideOutOfContainer(
                    animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            if (!isAuthenticated) {
                navController.navigate(ROUTE_LOGIN)
            } else if (!isAdmin) {
                // Navigate back to home if not an admin
                navController.navigate(ROUTE_HOME) {
                    popUpTo(ROUTE_ADMIN) { inclusive = true }
                }
            } else {
                // Show admin screen
                AdminScreen(
                    newsViewModel = newsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
} 