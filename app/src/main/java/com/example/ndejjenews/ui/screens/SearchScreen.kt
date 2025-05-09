package com.example.ndejjenews.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ndejjenews.R
import com.example.ndejjenews.data.model.NewsArticle
import com.example.ndejjenews.ui.components.NewsCard
import com.example.ndejjenews.utils.Constants.CATEGORY_ACADEMICS
import com.example.ndejjenews.utils.Constants.CATEGORY_ALL
import com.example.ndejjenews.utils.Constants.CATEGORY_ANNOUNCEMENTS
import com.example.ndejjenews.utils.Constants.CATEGORY_EVENTS
import com.example.ndejjenews.utils.Constants.CATEGORY_SPORTS
import com.example.ndejjenews.viewmodel.NewsViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SearchScreen(
    newsViewModel: NewsViewModel,
    onNavigateBack: () -> Unit,
    onArticleClick: (String) -> Unit
) {
    val searchResults by newsViewModel.searchResults.collectAsState()
    val isLoading by newsViewModel.isLoading.collectAsState()
    val errorMessage by newsViewModel.errorMessage.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    val selectedCategory = remember { mutableStateOf(CATEGORY_ALL) }
    
    // Categories for filtering
    val categories = listOf(
        CATEGORY_ALL,
        CATEGORY_ACADEMICS,
        CATEGORY_SPORTS,
        CATEGORY_EVENTS,
        CATEGORY_ANNOUNCEMENTS
    )
    
    // Debounce search input to avoid too many requests
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank() && searchQuery.length >= 2) {
            newsViewModel.searchArticles(searchQuery)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.search)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search TextField
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(stringResource(id = R.string.search_hint)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.search)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(id = R.string.clear)
                            )
                        }
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
            
            // Category Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory.value == category,
                        onClick = {
                            selectedCategory.value = category
                            if (searchQuery.isNotBlank()) {
                                newsViewModel.searchArticles(searchQuery)
                            }
                        },
                        label = {
                            Text(
                                text = when (category) {
                                    CATEGORY_ALL -> stringResource(id = R.string.category_all)
                                    CATEGORY_ACADEMICS -> stringResource(id = R.string.category_academics)
                                    CATEGORY_SPORTS -> stringResource(id = R.string.category_sports)
                                    CATEGORY_EVENTS -> stringResource(id = R.string.category_events)
                                    CATEGORY_ANNOUNCEMENTS -> stringResource(id = R.string.category_announcements)
                                    else -> category
                                }
                            )
                        }
                    )
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: stringResource(id = R.string.unknown_error),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                } else if (searchResults.isEmpty() && searchQuery.isNotBlank()) {
                    Text(
                        text = stringResource(id = R.string.search_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                } else if (searchQuery.isBlank()) {
                    Text(
                        text = stringResource(id = R.string.search_hint),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(searchResults) { article ->
                            NewsCard(
                                article = article,
                                onClick = { onArticleClick(article.id) }
                            )
                        }
                    }
                }
            }
        }
    }
} 