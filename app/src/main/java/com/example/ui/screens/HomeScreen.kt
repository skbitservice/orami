package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.ui.components.CategoryChips
import com.example.ui.components.CategoryVisualRow
import com.example.ui.components.CategoryCardGrid
import com.example.ui.components.HeroBanner
import com.example.ui.components.ProductCard
import com.example.ui.theme.OramiAmber
import com.example.ui.theme.OramiCoral
import com.example.ui.theme.OramiEmerald
import com.example.ui.theme.OramiEmeraldLight
import com.example.viewmodel.AppScreen
import com.example.viewmodel.OramiViewModel

@Composable
fun HomeScreen(
    viewModel: OramiViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val wishlistIds by viewModel.wishlistIds.collectAsStateWithLifecycle()

    val flashDeals = products.filter { it.isFlashDeal }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Hero Carousel Banner
        item {
            HeroBanner(
                onBannerClick = { category ->
                    viewModel.selectCategory(category)
                },
                onExploreClick = {},
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Trust & Guarantee Badges Strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TrustBadgeItem(text = "100% Authentic")
                Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                TrustBadgeItem(text = "Free Shipping > ₹399")
                Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                TrustBadgeItem(text = "7-Day Easy Returns")
            }
        }

        // Visual Circular Category Story Bubbles & Filter Chips
        item {
            Column(modifier = Modifier.padding(top = 4.dp)) {
                CategoryVisualRow(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) }
                )
                CategoryChips(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) }
                )
            }
        }

        // Flash Deals Section (if any match)
        if (flashDeals.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(OramiCoral, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Flash Deals",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Countdown Pill
                        Box(
                            modifier = Modifier
                                .background(OramiCoral.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Ends in 04:18:22",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = OramiCoral
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(flashDeals, key = { "flash_${it.id}" }) { product ->
                            FlashDealCard(
                                product = product,
                                isWishlisted = wishlistIds.contains(product.id),
                                onProductClick = { viewModel.openProductDetail(it) },
                                onAddToCart = { viewModel.addToCart(it) }
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Curated Catalog
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (selectedCategory == ProductCategory.ALL) "Trending Collections" else selectedCategory.displayName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${products.size} premium products found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (selectedCategory != ProductCategory.ALL) {
                    Text(
                        text = "Reset",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = OramiCoral,
                        modifier = Modifier
                            .clickable { viewModel.selectCategory(ProductCategory.ALL) }
                            .padding(4.dp)
                    )
                }
            }
        }

        // Zero Products State
        if (products.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No products found",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try clearing your filters or selecting a different category.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { viewModel.resetFilters() },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            // 2-Column Product Grid in LazyColumn items
            val chunkedProducts = products.chunked(2)
            items(chunkedProducts, key = { chunk -> chunk.joinToString { it.id } }) { rowProducts ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (product in rowProducts) {
                        Box(modifier = Modifier.weight(1f)) {
                            ProductCard(
                                product = product,
                                isWishlisted = wishlistIds.contains(product.id),
                                onProductClick = { viewModel.openProductDetail(it) },
                                onWishlistToggle = { viewModel.toggleWishlist(it) },
                                onAddToCart = { viewModel.addToCart(it) }
                            )
                        }
                    }
                    if (rowProducts.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun TrustBadgeItem(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun FlashDealCard(
    product: Product,
    isWishlisted: Boolean,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onProductClick(product) }
            .testTag("flash_card_${product.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = product.imageUrls.firstOrNull(),
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .background(OramiCoral, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${product.discountPercent}% OFF",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "₹${product.price.toInt()}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "₹${product.originalPrice.toInt()}",
                            style = MaterialTheme.typography.labelSmall.copy(textDecoration = TextDecoration.LineThrough),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { onAddToCart(product) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = "Add",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}
