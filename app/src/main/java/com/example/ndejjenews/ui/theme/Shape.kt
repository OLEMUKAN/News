package com.example.ndejjenews.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Define custom shapes for different component types
val Shapes = Shapes(
    // Small components like buttons, chips
    small = RoundedCornerShape(4.dp),
    
    // Medium components like cards, dialogs
    medium = RoundedCornerShape(8.dp),
    
    // Large components like bottom sheets, expanded cards
    large = RoundedCornerShape(16.dp),
    
    // Custom extra large shape for featured content
    extraLarge = RoundedCornerShape(24.dp)
) 