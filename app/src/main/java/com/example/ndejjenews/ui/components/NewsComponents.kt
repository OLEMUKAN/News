package com.example.ndejjenews.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ndejjenews.R
import com.example.ndejjenews.data.model.NewsArticle
import com.example.ndejjenews.utils.Constants.ANIMATION_DURATION_MEDIUM
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Card component for displaying an article in the news feed
 */
@Composable
fun NewsCard(
    article: NewsArticle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) + 
                expandVertically(animationSpec = spring()),
        exit = fadeOut(animationSpec = tween(ANIMATION_DURATION_MEDIUM)) + 
                shrinkVertically(animationSpec = spring())
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            elevation = CardDefaults.cardElevation(
                defaultElevation = dimensionResource(R.dimen.elevation_small)
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(dimensionResource(R.dimen.card_padding))
            ) {
                // Article image
                AsyncImage(
                    model = article.imageUrl,
                    contentDescription = article.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(R.dimen.card_image_height))
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.ic_launcher_foreground),
                    placeholder = painterResource(R.drawable.ic_launcher_foreground)
                )
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                
                // Article title
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
                
                // Article summary
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                
                // Category chip
                CategoryChip(category = article.category)
                
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                
                // Metadata row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Author
                    Text(
                        text = article.authorName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Timestamp
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small)),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = formatTimestamp(article.getPublishedAtMillis()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_medium)))
                    
                    // Likes
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = stringResource(R.string.likes_count, article.likeCount),
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small)),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = article.likeCount.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_medium)))
                    
                    // Comments
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = stringResource(R.string.comments_count, article.commentCount),
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small)),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = article.commentCount.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Chip component for displaying article category
 */
@Composable
fun CategoryChip(
    category: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = category.uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.spacing_small),
                vertical = dimensionResource(R.dimen.spacing_xsmall)
            )
        )
    }
}

/**
 * Format timestamp into a readable date
 */
private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
} 