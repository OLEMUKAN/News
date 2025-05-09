package com.example.ndejjenews.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ndejjenews.R
import com.example.ndejjenews.data.model.Comment
import com.example.ndejjenews.ui.components.CategoryChip
import com.example.ndejjenews.utils.Constants.ANIMATION_DURATION_LONG
import com.example.ndejjenews.utils.Constants.ANIMATION_DURATION_MEDIUM
import com.example.ndejjenews.viewmodel.NewsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

/**
 * Screen for displaying the details of a news article
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailsScreen(
    articleId: String,
    navController: NavController,
    newsViewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    // Load article when the screen is first displayed
    LaunchedEffect(articleId) {
        newsViewModel.loadArticle(articleId)
    }
    
    // Collect state from ViewModel
    val article by newsViewModel.selectedArticle.collectAsState()
    val comments by newsViewModel.comments.collectAsState()
    val isLoading by newsViewModel.isLoading.collectAsState()
    val errorMessage by newsViewModel.errorMessage.collectAsState()
    val commentText by newsViewModel.commentText.collectAsState()
    val isArticleLiked by newsViewModel.isArticleLiked.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to news feed"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && article == null) {
                // Show loading indicator
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (errorMessage != null && article == null) {
                // Show error message
                Text(
                    text = errorMessage ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(dimensionResource(R.dimen.spacing_large))
                )
            } else if (article != null) {
                // Show article details
                ArticleContent(
                    article = article!!,
                    comments = comments,
                    commentText = commentText,
                    isLiked = isArticleLiked,
                    onCommentTextChanged = { newsViewModel.updateCommentText(it) },
                    onCommentSubmit = { newsViewModel.addComment() },
                    onLikeClick = { article?.id?.let { newsViewModel.toggleLike(it) } }
                )
            }
        }
    }
}

@Composable
private fun ArticleContent(
    article: com.example.ndejjenews.data.model.NewsArticle,
    comments: List<Comment>,
    commentText: String,
    isLiked: Boolean,
    onCommentTextChanged: (String) -> Unit,
    onCommentSubmit: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // State to track which sections have been animated in
    val (headerVisible, setHeaderVisible) = remember { mutableStateOf(false) }
    val (contentVisible, setContentVisible) = remember { mutableStateOf(false) }
    val (commentsVisible, setCommentsVisible) = remember { mutableStateOf(false) }
    val (commentFormVisible, setCommentFormVisible) = remember { mutableStateOf(false) }
    
    // Sequentially animate in the sections
    LaunchedEffect(article) {
        setHeaderVisible(false)
        setContentVisible(false)
        setCommentsVisible(false)
        setCommentFormVisible(false)
        
        delay(100) // Small initial delay
        setHeaderVisible(true)
        delay(300)
        setContentVisible(true)
        delay(300)
        setCommentsVisible(true)
        delay(200)
        setCommentFormVisible(true)
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(R.dimen.spacing_medium))
    ) {
        // Header section - title, image, metadata
        AnimatedVisibility(
            visible = headerVisible,
            enter = fadeIn(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                    slideInVertically(
                        animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                        initialOffsetY = { it / 3 }
                    )
        ) {
            Column {
                // Article title
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.spacing_medium))
                )
                
                // Article image
                AsyncImage(
                    model = article.imageUrl,
                    contentDescription = article.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.ic_launcher_foreground),
                    placeholder = painterResource(R.drawable.ic_launcher_foreground)
                )
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                
                // Metadata row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category
                    CategoryChip(category = article.category)
                    
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_medium)))
                    
                    // Author
                    Text(
                        text = article.authorName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Timestamp
                    Text(
                        text = formatDate(article.getPublishedAtMillis()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))
            }
        }
        
        // Content section - article body
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                    expandVertically(animationSpec = tween(ANIMATION_DURATION_MEDIUM))
        ) {
            Column {
                // Article content
                Text(
                    text = article.content,
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))
                
                // Like button
                Button(
                    onClick = onLikeClick,
                    modifier = Modifier.align(Alignment.End),
                    colors = if (isLiked) {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    } else {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = if (isLiked) R.string.unlike else R.string.like)
                    )
                }
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))
                
                // Comments header
                Text(
                    text = stringResource(R.string.comments_section, comments.size),
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
            }
        }
        
        // Comments section
        AnimatedVisibility(
            visible = commentsVisible,
            enter = fadeIn(animationSpec = tween(ANIMATION_DURATION_LONG)) +
                    expandVertically(animationSpec = tween(ANIMATION_DURATION_LONG))
        ) {
            Column {
                if (comments.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_comments),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = dimensionResource(R.dimen.spacing_medium))
                    )
                } else {
                    comments.forEach { comment ->
                        CommentItem(comment = comment)
                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                    }
                }
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))
            }
        }
        
        // Comment form
        AnimatedVisibility(
            visible = commentFormVisible,
            enter = fadeIn(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) +
                    slideInVertically(
                        animationSpec = tween(ANIMATION_DURATION_MEDIUM),
                        initialOffsetY = { it / 2 }
                    )
        ) {
            Column {
                // Add comment section
                Text(
                    text = stringResource(R.string.add_comment),
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                
                // Comment text field
                OutlinedTextField(
                    value = commentText,
                    onValueChange = onCommentTextChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.add_comment)) },
                    minLines = 3
                )
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                
                // Submit button
                Button(
                    onClick = onCommentSubmit,
                    modifier = Modifier.align(Alignment.End),
                    enabled = commentText.isNotBlank()
                ) {
                    Text(stringResource(R.string.submit_comment))
                }
            }
        }
    }
}

@Composable
private fun CommentItem(comment: Comment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_medium))
        ) {
            // Comment author and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author
                Text(
                    text = comment.userDisplayName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Date
                Text(
                    text = formatDate(comment.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
            
            // Comment text
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    return SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(date)
} 