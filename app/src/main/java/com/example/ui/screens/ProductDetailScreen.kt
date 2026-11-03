package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.model.Product
import com.example.model.ProductReview
import com.example.model.ProductVariant
import com.example.ui.theme.OramiAmber
import com.example.ui.theme.OramiCoral
import com.example.ui.theme.OramiEmerald
import com.example.viewmodel.AppScreen
import com.example.viewmodel.OramiViewModel

@Composable
fun ProductDetailScreen(
    viewModel: OramiViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.handleBack()
    }

    val product = viewModel.selectedProduct.collectAsStateWithLifecycle().value ?: return
    val selectedVariant by viewModel.selectedVariant.collectAsStateWithLifecycle()
    val reviews by viewModel.productReviews.collectAsStateWithLifecycle()
    val wishlistIds by viewModel.wishlistIds.collectAsStateWithLifecycle()
    val isWishlisted = wishlistIds.contains(product.id)

    var currentImageIndex by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("product_detail_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Top App Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.handleBack() },
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }

                    Text(
                        text = product.brand.uppercase(),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row {
                        IconButton(
                            onClick = { viewModel.showNotice("Share link copied to clipboard") }
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                        }
                        IconButton(
                            onClick = { viewModel.toggleWishlist(product) },
                            modifier = Modifier.testTag("detail_wishlist_toggle")
                        ) {
                            Icon(
                                imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (isWishlisted) OramiCoral else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Image Gallery with pagination dots
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    val activeImage = product.imageUrls.getOrElse(currentImageIndex) { product.imageUrls.first() }

                    AsyncImage(
                        model = activeImage,
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Dots indicator
                    if (product.imageUrls.size > 1) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            product.imageUrls.indices.forEach { index ->
                                Box(
                                    modifier = Modifier
                                        .size(if (index == currentImageIndex) 20.dp else 8.dp, 8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (index == currentImageIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                                                alpha = 0.3f
                                            )
                                        )
                                        .clickable { currentImageIndex = index }
                                )
                            }
                        }
                    }
                }
            }

            // Product Details Block
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Category & Rating row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.category.displayName,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = OramiCoral
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = OramiAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${product.rating} (${product.reviewCount} reviews)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Price Section
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹${product.price.toInt()}",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (product.originalPrice > product.price) {
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "₹${product.originalPrice.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    textDecoration = TextDecoration.LineThrough
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(OramiCoral.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${product.discountPercent}% OFF",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = OramiCoral
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Inclusive of all taxes • Free shipping on orders over ₹999",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Variant Selector (Color / Size)
                    if (product.variants.isNotEmpty()) {
                        Text(
                            text = "SELECT OPTION: ${selectedVariant?.name ?: "Default"}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            product.variants.forEach { variant ->
                                val isSelected = selectedVariant?.id == variant.id
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                        )
                                        .clickable { viewModel.selectVariant(variant) }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                        .testTag("variant_chip_${variant.id}")
                                ) {
                                    Text(
                                        text = variant.name,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Product Description
                    Text(
                        text = "PRODUCT OVERVIEW",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Specifications Table
                    if (product.specs.isNotEmpty()) {
                        Text(
                            text = "SPECIFICATIONS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                product.specs.entries.forEachIndexed { index, (key, value) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = key,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = value,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    if (index < product.specs.size - 1) {
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Customer Ratings & Reviews
                    ReviewsSection(
                        rating = product.rating,
                        reviewCount = product.reviewCount,
                        reviews = reviews
                    )
                }
            }
        }

        // Sticky Bottom CTA Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Add to Cart
                OutlinedButton(
                    onClick = { viewModel.addToCart(product, selectedVariant) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("detail_add_to_cart_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add to Cart", fontWeight = FontWeight.Bold)
                }

                // Buy Now (Adds to cart & navigates directly to cart/checkout)
                Button(
                    onClick = {
                        viewModel.addToCart(product, selectedVariant)
                        viewModel.setCheckoutSheetOpen(true)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("detail_buy_now_button")
                ) {
                    Text("Buy Now", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ReviewsSection(
    rating: Float,
    reviewCount: Int,
    reviews: List<ProductReview>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "CUSTOMER REVIEWS",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Rating Overview Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(
                    text = String.format("%.1f", rating),
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp, fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < rating.toInt()) OramiAmber else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$reviewCount verified ratings",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Star distribution progress bars
            Column(modifier = Modifier.weight(1f)) {
                listOf(5 to 0.82f, 4 to 0.12f, 3 to 0.04f, 2 to 0.01f, 1 to 0.01f).forEach { (stars, frac) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 1.dp)
                    ) {
                        Text(
                            text = "${stars}★",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.width(22.dp)
                        )
                        LinearProgressIndicator(
                            progress = { frac },
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = OramiAmber,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Individual Reviews
        reviews.forEach { review ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = review.reviewer,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            if (review.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Purchase",
                                    tint = OramiEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Text(
                            text = review.date,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row {
                        repeat(5) { starIndex ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (starIndex < review.rating.toInt()) OramiAmber else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = review.comment,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
