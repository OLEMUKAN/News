package com.example.ndejjenews.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ndejjenews.R
import com.example.ndejjenews.data.model.NewsArticle
import com.example.ndejjenews.utils.Constants.CATEGORY_ACADEMICS
import com.example.ndejjenews.utils.Constants.CATEGORY_ANNOUNCEMENTS
import com.example.ndejjenews.utils.Constants.CATEGORY_EVENTS
import com.example.ndejjenews.utils.Constants.CATEGORY_SPORTS
import com.example.ndejjenews.viewmodel.NewsViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    newsViewModel: NewsViewModel,
    onNavigateBack: () -> Unit
) {
    val isLoading by newsViewModel.isLoading.collectAsState()
    val errorMessage by newsViewModel.errorMessage.collectAsState()
    val scrollState = rememberScrollState()
    
    // Form state
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(CATEGORY_ACADEMICS) }
    
    // Validation state errors
    var titleError by remember { mutableStateOf<Int?>(null) }
    var contentError by remember { mutableStateOf<Int?>(null) }
    var summaryError by remember { mutableStateOf<Int?>(null) }
    
    // Success message
    var showSuccessMessage by remember { mutableStateOf(false) }
    
    // Categories for dropdown
    val categories = listOf(
        CATEGORY_ACADEMICS,
        CATEGORY_SPORTS,
        CATEGORY_EVENTS,
        CATEGORY_ANNOUNCEMENTS
    )
    
    // Title required error text
    val titleRequiredText = stringResource(id = R.string.article_title_required)
    val summaryRequiredText = stringResource(id = R.string.article_summary_required)
    val contentRequiredText = stringResource(id = R.string.article_content_required)
    
    // Handle article submission success
    LaunchedEffect(Unit) {
        newsViewModel.articleSubmissionStatus.collectLatest { status ->
            if (status == true) {
                // Reset form
                title = ""
                content = ""
                summary = ""
                imageUrl = ""
                selectedCategory = CATEGORY_ACADEMICS
                showSuccessMessage = true
                newsViewModel.resetArticleSubmissionStatus()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.admin_panel)) },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.create_article),
                    style = MaterialTheme.typography.headlineMedium
                )
                
                // Title input
                OutlinedTextField(
                    value = title,
                    onValueChange = { 
                        title = it
                        titleError = if (it.isBlank()) R.string.article_title_required else null
                    },
                    label = { Text(stringResource(id = R.string.article_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = titleError != null,
                    supportingText = { 
                        if (titleError != null) {
                            Text(stringResource(id = titleError!!))
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
                
                // Summary input
                OutlinedTextField(
                    value = summary,
                    onValueChange = { 
                        summary = it
                        summaryError = if (it.isBlank()) R.string.article_summary_required else null
                    },
                    label = { Text(stringResource(id = R.string.article_summary)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = summaryError != null,
                    supportingText = { 
                        if (summaryError != null) {
                            Text(stringResource(id = summaryError!!))
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    minLines = 2,
                    maxLines = 4
                )
                
                // Content input
                OutlinedTextField(
                    value = content,
                    onValueChange = { 
                        content = it
                        contentError = if (it.isBlank()) R.string.article_content_required else null
                    },
                    label = { Text(stringResource(id = R.string.article_content)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    isError = contentError != null,
                    supportingText = { 
                        if (contentError != null) {
                            Text(stringResource(id = contentError!!))
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
                
                // Image URL input
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text(stringResource(id = R.string.article_image_url)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
                
                // Category dropdown
                var expanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = stringResource(id = when(selectedCategory) {
                            CATEGORY_ACADEMICS -> R.string.category_academics
                            CATEGORY_SPORTS -> R.string.category_sports
                            CATEGORY_EVENTS -> R.string.category_events
                            CATEGORY_ANNOUNCEMENTS -> R.string.category_announcements
                            else -> R.string.category_academics
                        }),
                        onValueChange = { },
                        label = { Text(stringResource(id = R.string.article_category)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            val categoryStringRes = when (category) {
                                CATEGORY_ACADEMICS -> R.string.category_academics
                                CATEGORY_SPORTS -> R.string.category_sports
                                CATEGORY_EVENTS -> R.string.category_events
                                CATEGORY_ANNOUNCEMENTS -> R.string.category_announcements
                                else -> R.string.category_academics
                            }
                            
                            DropdownMenuItem(
                                text = { Text(stringResource(id = categoryStringRes)) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                // Submit button
                Button(
                    onClick = {
                        // Validate form
                        val hasTitleError = title.isBlank()
                        val hasSummaryError = summary.isBlank()
                        val hasContentError = content.isBlank()
                        
                        titleError = if (hasTitleError) R.string.article_title_required else null
                        summaryError = if (hasSummaryError) R.string.article_summary_required else null
                        contentError = if (hasContentError) R.string.article_content_required else null
                        
                        // Submit if valid
                        if (!hasTitleError && !hasContentError && !hasSummaryError) {
                            val article = NewsArticle(
                                title = title,
                                content = content,
                                summary = summary, 
                                imageUrl = imageUrl,
                                category = selectedCategory,
                                published = true,
                                authorName = "Admin" // This should be replaced with actual admin name
                            )
                            newsViewModel.addArticle(article)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    enabled = !isLoading
                ) {
                    Text(stringResource(id = R.string.submit_article))
                }
                
                // Success message
                if (showSuccessMessage) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.article_submitted),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            
                            IconButton(onClick = { showSuccessMessage = false }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = stringResource(id = R.string.clear),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
                
                // Error message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: stringResource(id = R.string.unknown_error),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // Add some space at the bottom
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            // Loading indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
} 