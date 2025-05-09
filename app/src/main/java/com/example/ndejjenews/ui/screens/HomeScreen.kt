package com.example.ndejjenews.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ndejjenews.R
import com.example.ndejjenews.ui.components.ErrorMessage
import com.example.ndejjenews.ui.components.LoadingIndicator
import com.example.ndejjenews.ui.components.NdejjeNewsTopAppBar
import com.example.ndejjenews.ui.components.NewsCard
import com.example.ndejjenews.utils.Constants.ANIMATION_DURATION_SHORT
import com.example.ndejjenews.utils.Constants.CATEGORY_ACADEMICS
import com.example.ndejjenews.utils.Constants.CATEGORY_ALL
import com.example.ndejjenews.utils.Constants.CATEGORY_ANNOUNCEMENTS
import com.example.ndejjenews.utils.Constants.CATEGORY_EVENTS
import com.example.ndejjenews.utils.Constants.CATEGORY_SPORTS
import com.example.ndejjenews.utils.Constants.ROUTE_ADMIN
import com.example.ndejjenews.utils.Constants.ROUTE_SEARCH
import com.example.ndejjenews.utils.Constants.ROUTE_SETTINGS
import com.example.ndejjenews.viewmodel.AuthViewModel
import com.example.ndejjenews.viewmodel.NewsViewModel
import kotlinx.coroutines.delay

/**
 * Home screen displaying the news feed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    newsViewModel: NewsViewModel,
    authViewModel: AuthViewModel
) {
    // Collect state from ViewModels
    val articles by newsViewModel.articles.collectAsState()
    val isLoading by newsViewModel.isLoading.collectAsState()
    val errorMessage by newsViewModel.errorMessage.collectAsState()
    val selectedCategory by newsViewModel.selectedCategory.collectAsState()
    val isAdmin by authViewModel.isAdmin.collectAsState()
    
    // State for staggered animation
    var visibleItems by remember { mutableStateOf(0) }
    
    // Reset animation when loading new articles or categories
    LaunchedEffect(articles, selectedCategory) {
        visibleItems = 0
        delay(100) // Short delay before starting animation
        visibleItems = articles.size
    }
    
    // Define the categories
    val categories = listOf(
        CATEGORY_ALL,
        CATEGORY_ACADEMICS,
        CATEGORY_SPORTS,
        CATEGORY_EVENTS,
        CATEGORY_ANNOUNCEMENTS
    )
    
    Scaffold(
        topBar = {
            NdejjeNewsTopAppBar(
                title = stringResource(R.string.app_name),
                actions = {
                    // Settings icon
                    IconButton(onClick = { navController.navigate(ROUTE_SETTINGS) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    
                    // Search icon
                    IconButton(onClick = { navController.navigate(ROUTE_SEARCH) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.search),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            // Only show FAB for admin users
            if (isAdmin) {
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.add_article)) },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    onClick = { navController.navigate(ROUTE_ADMIN) }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = dimensionResource(R.dimen.spacing_medium))
            ) {
                categories.forEach { category ->
                    val categoryText = when (category) {
                        CATEGORY_ALL -> stringResource(R.string.category_all)
                        CATEGORY_ACADEMICS -> stringResource(R.string.category_academics)
                        CATEGORY_SPORTS -> stringResource(R.string.category_sports)
                        CATEGORY_EVENTS -> stringResource(R.string.category_events)
                        CATEGORY_ANNOUNCEMENTS -> stringResource(R.string.category_announcements)
                        else -> category
                    }
                    
                    ElevatedFilterChip(
                        selected = category == selectedCategory,
                        onClick = { newsViewModel.loadArticlesByCategory(category) },
                        label = { Text(categoryText) }
                    )
                    
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                }
            }
            
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
            
            // News feed
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading && articles.isEmpty() -> {
                        LoadingIndicator()
                    }
                    errorMessage != null -> {
                        ErrorMessage(
                            message = errorMessage ?: stringResource(R.string.error),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    articles.isEmpty() -> {
                        Text(
                            text = stringResource(R.string.no_articles),
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(dimensionResource(R.dimen.spacing_medium))
                        ) {
                            itemsIndexed(
                                items = articles,
                                key = { _, article -> article.id } // Use article ID as key for better animation handling
                            ) { index, article ->
                                // Staggered appearance based on index
                                val isVisible = index < visibleItems
                                
                                // Add staggered delay based on index
                                if (isVisible) {
                                    LaunchedEffect(key1 = index) {
                                        delay(index * 50L) // 50ms delay between items
                                    }
                                }
                                
                                NewsCard(
                                    article = article,
                                    onClick = {
                                        navController.navigate("article/${article.id}")
                                    },
                                    visible = isVisible
                                )
                                
                                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                            }
                        }
                    }
                }
            }
        }
    }
} 