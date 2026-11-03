package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.ProductCategory
import com.example.ui.theme.OramiCoral

@Composable
fun CategoryVisualRow(
    selectedCategory: ProductCategory,
    onCategorySelected: (ProductCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "EXPLORE CATEGORIES",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (selectedCategory != ProductCategory.ALL) {
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = OramiCoral,
                    modifier = Modifier
                        .clickable { onCategorySelected(ProductCategory.ALL) }
                        .padding(4.dp)
                )
            }
        }

        // Horizontal Story-like Circular Category Bubbles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ProductCategory.values().forEach { category ->
                val isSelected = category == selectedCategory
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) OramiCoral else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    animationSpec = spring(),
                    label = "border"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(70.dp)
                        .clickable { onCategorySelected(category) }
                        .testTag("category_bubble_${category.name.lowercase()}")
                ) {
                    // Outer Ring with border
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = borderColor,
                                shape = CircleShape
                            )
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = category.imageUrl,
                            contentDescription = category.displayName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // If selected, subtle dark tint
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(OramiCoral.copy(alpha = 0.15f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = if (isSelected) OramiCoral else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCardGrid(
    onCategorySelected: (ProductCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val categories = ProductCategory.values().filter { it != ProductCategory.ALL }.chunked(2)
        categories.forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pair.forEach { category ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(110.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onCategorySelected(category) }
                            .testTag("category_grid_card_${category.name.lowercase()}"),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = category.imageUrl,
                                contentDescription = category.displayName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Gradient Scrim
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.2f),
                                                Color.Black.copy(alpha = 0.82f)
                                            )
                                        )
                                    )
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Discount Tag
                                Box(
                                    modifier = Modifier
                                        .background(OramiCoral, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = category.discountTag,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                        color = Color.White
                                    )
                                }

                                Column {
                                    Text(
                                        text = category.displayName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${category.itemCount}+ products",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
