package com.example.ndejjenews.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ndejjenews.R
import com.example.ndejjenews.data.model.NewsArticle
import com.example.ndejjenews.ui.components.NewsCard
import com.example.ndejjenews.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedArticlesScreen(
    newsViewModel: NewsViewModel,
    onNavigateBack: () -> Unit,
    onArticleClick: (String) -> Unit
) {
    val savedArticles by newsViewModel.savedArticles.collectAsState()
    val isLoading by newsViewModel.isLoading.collectAsState()
    val errorMessage by newsViewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.saved_articles)) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
            } else if (savedArticles.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.no_saved_articles),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
            } else {
                SavedArticlesList(
                    articles = savedArticles,
                    onArticleClick = onArticleClick
                )
            }
        }
    }
}

@Composable
fun SavedArticlesList(
    articles: List<NewsArticle>,
    onArticleClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(articles) { article ->
            NewsCard(
                article = article,
                onClick = { onArticleClick(article.id) }
            )
        }
    }
} 