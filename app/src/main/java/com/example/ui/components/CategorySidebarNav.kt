package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.model.Subcategory
import com.example.ui.theme.OramiAmber
import com.example.ui.theme.OramiCoral
import com.example.ui.theme.OramiEmerald

/**
 * Premium Amazon-Inspired Sidebar Category Navigation System.
 *
 * Implements a split-view category browsing experience:
 * - Left Sidebar (approx 30-33% mobile / fixed max on tablet):
 *   Vertical scrollable categories with icon/thumbnail, active highlight pill,
 *   selected border indicator, and discount tags.
 * - Right Content Area (approx 67-70% mobile / expanded):
 *   Selected category title, dynamic 2-column subcategory image grid,
 *   "View All" mode, instant subcategory product previews, and search filter.
 */
@Composable
fun CategorySidebarNav(
    categories: List<ProductCategory>,
    selectedCategory: ProductCategory,
    subcategories: List<Subcategory>,
    selectedSubcategory: Subcategory?,
    products: List<Product>,
    wishlistIds: Set<String>,
    onSelectCategory: (ProductCategory) -> Unit,
    onSelectSubcategory: (Subcategory?) -> Unit,
    onProductClick: (Product) -> Unit,
    onWishlistToggle: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onOpenFilterSheet: () -> Unit,
    hasActiveFilters: Boolean = false,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var isViewAllMode by remember { mutableStateOf(false) }

    // Filter main categories if search query is entered
    val filteredCategories = remember(categories, searchQuery) {
        if (searchQuery.isBlank()) categories
        else {
            val q = searchQuery.trim().lowercase()
            categories.filter { cat ->
                cat.displayName.lowercase().contains(q) ||
                        cat.tagline.lowercase().contains(q) ||
                        subcategories.any { sub -> sub.category == cat && sub.name.lowercase().contains(q) }
            }
        }
    }

    // Filter subcategories for the selected main category & search query
    val currentSubcategories = remember(selectedCategory, subcategories, searchQuery) {
        val baseList = if (selectedCategory == ProductCategory.ALL) {
            subcategories
        } else {
            subcategories.filter { it.category == selectedCategory }
        }

        if (searchQuery.isBlank()) baseList
        else {
            val q = searchQuery.trim().lowercase()
            baseList.filter { it.name.lowercase().contains(q) || it.tagline.lowercase().contains(q) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("amazon_category_nav_system")
    ) {
        // Top Integrated Category Search & Filter Bar
        CategorySearchBar(
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onClearSearch = { searchQuery = "" },
            onFilterClick = onOpenFilterSheet,
            hasActiveFilters = hasActiveFilters
        )

        // Split-pane responsive layout: Left Sidebar + Right Content Area
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenWidth = maxWidth
            val sidebarWidth = if (screenWidth < 600.dp) (screenWidth * 0.32f).coerceIn(100.dp, 130.dp) else 230.dp

            Row(modifier = Modifier.fillMaxSize()) {
                // ==========================================
                // 1. LEFT SIDEBAR: Vertical Category List
                // ==========================================
                CategoryVerticalSidebar(
                    categories = filteredCategories,
                    selectedCategory = selectedCategory,
                    onSelectCategory = { cat ->
                        isViewAllMode = false
                        onSelectCategory(cat)
                    },
                    modifier = Modifier
                        .width(sidebarWidth)
                        .fillMaxHeight()
                )

                // Subtle vertical divider between sidebar and right content
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                )

                // ==========================================
                // 2. RIGHT CONTENT AREA: Subcategories & Products
                // ==========================================
                CategoryRightContentPane(
                    selectedCategory = selectedCategory,
                    subcategories = currentSubcategories,
                    selectedSubcategory = selectedSubcategory,
                    products = products,
                    wishlistIds = wishlistIds,
                    isViewAllMode = isViewAllMode,
                    searchQuery = searchQuery,
                    onSelectSubcategory = onSelectSubcategory,
                    onToggleViewAll = { isViewAllMode = !isViewAllMode },
                    onProductClick = onProductClick,
                    onWishlistToggle = onWishlistToggle,
                    onAddToCart = onAddToCart,
                    onClearSearch = { searchQuery = "" },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}

/**
 * Top Search Header with active filter badge
 */
@Composable
private fun CategorySearchBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onFilterClick: () -> Unit,
    hasActiveFilters: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(
                        "Search in categories & products...",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("category_search_input")
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onFilterClick,
                modifier = Modifier.testTag("category_filter_trigger")
            ) {
                BadgedBox(
                    badge = {
                        if (hasActiveFilters) {
                            Badge(
                                containerColor = OramiCoral,
                                modifier = Modifier.size(8.dp)
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filters",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Left Sidebar: Vertical scrolling list of categories
 */
@Composable
private fun CategoryVerticalSidebar(
    categories: List<ProductCategory>,
    selectedCategory: ProductCategory,
    onSelectCategory: (ProductCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("category_sidebar_list"),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(categories, key = { it.name }) { category ->
                val isSelected = category == selectedCategory

                CategorySidebarItem(
                    category = category,
                    isSelected = isSelected,
                    onClick = { onSelectCategory(category) }
                )
            }
        }
    }
}

/**
 * Individual Category Item inside the Left Sidebar
 */
@Composable
private fun CategorySidebarItem(
    category: ProductCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.surface
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 180),
        label = "itemBg"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 180),
        label = "itemText"
    )

    val itemShape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp, topEnd = 16.dp, bottomEnd = 16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(itemShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .padding(vertical = 10.dp, horizontal = 6.dp)
            .testTag("category_item_${category.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selected Vertical Accent Indicator Bar
            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(38.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isSelected) OramiCoral else Color.Transparent)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Category Thumbnail / Icon
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) OramiCoral else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            shape = CircleShape
                        )
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = category.imageUrl,
                        contentDescription = category.displayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Selected overlay sheen
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(OramiCoral.copy(alpha = 0.12f))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Short readable category title
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 10.5.sp,
                        lineHeight = 13.sp
                    ),
                    color = contentColor,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Discount tag if active
                if (category != ProductCategory.ALL && category.discountTag.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) OramiCoral.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = category.discountTag,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isSelected) OramiCoral else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * Right Content Area: Handles subcategory image grid, drill-down product view, and "View All"
 */
@Composable
private fun CategoryRightContentPane(
    selectedCategory: ProductCategory,
    subcategories: List<Subcategory>,
    selectedSubcategory: Subcategory?,
    products: List<Product>,
    wishlistIds: Set<String>,
    isViewAllMode: Boolean,
    searchQuery: String,
    onSelectSubcategory: (Subcategory?) -> Unit,
    onToggleViewAll: () -> Unit,
    onProductClick: (Product) -> Unit,
    onWishlistToggle: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("right_content_pane"),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Main Category Header Banner
            item {
                CategoryBannerHeader(
                    category = selectedCategory,
                    selectedSubcategory = selectedSubcategory,
                    isViewAllMode = isViewAllMode,
                    productCount = products.size,
                    onBackToSubcategories = { onSelectSubcategory(null) },
                    onToggleViewAll = onToggleViewAll
                )
            }

            // MODE A: Subcategory Selected -> Drill-down Product Listing
            if (selectedSubcategory != null) {
                item {
                    SubcategoryDrilldownHeader(
                        subcategory = selectedSubcategory,
                        productCount = products.size,
                        onBack = { onSelectSubcategory(null) }
                    )
                }

                if (products.isEmpty()) {
                    item {
                        EmptyCategoryState(
                            title = "No products in ${selectedSubcategory.name}",
                            description = "We are adding more inventory to this collection soon.",
                            actionLabel = "Back to Subcategories",
                            onAction = { onSelectSubcategory(null) }
                        )
                    }
                } else {
                    val chunked = products.chunked(2)
                    items(chunked, key = { chunk -> chunk.joinToString { it.id } }) { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (product in row) {
                                Box(modifier = Modifier.weight(1f)) {
                                    CompactProductCard(
                                        product = product,
                                        isWishlisted = wishlistIds.contains(product.id),
                                        onProductClick = onProductClick,
                                        onWishlistToggle = onWishlistToggle,
                                        onAddToCart = onAddToCart
                                    )
                                }
                            }
                            if (row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            // MODE B: "View All" Mode -> Displays all products in the selected main category
            else if (isViewAllMode) {
                if (products.isEmpty()) {
                    item {
                        EmptyCategoryState(
                            title = "No products found",
                            description = "No products match the selected criteria.",
                            actionLabel = "View Subcategories",
                            onAction = onToggleViewAll
                        )
                    }
                } else {
                    val chunked = products.chunked(2)
                    items(chunked, key = { chunk -> chunk.joinToString { it.id } }) { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (product in row) {
                                Box(modifier = Modifier.weight(1f)) {
                                    CompactProductCard(
                                        product = product,
                                        isWishlisted = wishlistIds.contains(product.id),
                                        onProductClick = onProductClick,
                                        onWishlistToggle = onWishlistToggle,
                                        onAddToCart = onAddToCart
                                    )
                                }
                            }
                            if (row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            // MODE C: Standard Overview -> 2-Column Responsive Subcategory Image Grid
            else {
                if (subcategories.isEmpty()) {
                    item {
                        EmptyCategoryState(
                            title = "No subcategories found",
                            description = if (searchQuery.isNotEmpty()) "No subcategories match '$searchQuery'" else "No subcategories available.",
                            actionLabel = if (searchQuery.isNotEmpty()) "Clear Search" else null,
                            onAction = onClearSearch
                        )
                    }
                } else {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "POPULAR CATEGORIES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "${subcategories.size} Collections",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 2-Column Subcategories Grid
                    val chunkedSubcategories = subcategories.chunked(2)
                    items(chunkedSubcategories, key = { chunk -> chunk.joinToString { it.id } }) { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (subcategory in pair) {
                                Box(modifier = Modifier.weight(1f)) {
                                    SubcategoryCard(
                                        subcategory = subcategory,
                                        onClick = { onSelectSubcategory(subcategory) }
                                    )
                                }
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Bottom spacer for floating bottom nav clearance
            item {
                Spacer(modifier = Modifier.height(96.dp))
            }
        }
    }
}

/**
 * Top Header Banner inside Right Content Pane
 */
@Composable
private fun CategoryBannerHeader(
    category: ProductCategory,
    selectedSubcategory: Subcategory?,
    isViewAllMode: Boolean,
    productCount: Int,
    onBackToSubcategories: () -> Unit,
    onToggleViewAll: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("category_header_banner"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = category.tagline,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // View All Button
                if (selectedSubcategory == null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isViewAllMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                            .border(
                                width = 1.dp,
                                color = if (isViewAllMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(onClick = onToggleViewAll)
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isViewAllMode) "Subcategories" else "View All ($productCount)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isViewAllMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

/**
 * Header displayed when user is inside a specific subcategory drilldown
 */
@Composable
private fun SubcategoryDrilldownHeader(
    subcategory: Subcategory,
    productCount: Int,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to subcategories",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column {
                Text(
                    text = subcategory.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$productCount Products Available",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (!subcategory.discountTag.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .background(OramiCoral, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = subcategory.discountTag,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}

/**
 * 2-Column Subcategory Card with rounded image, name, and item count
 */
@Composable
fun SubcategoryCard(
    subcategory: Subcategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("subcat_card_${subcategory.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Image with aspect ratio and optional discount tag
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.15f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = subcategory.imageUrl,
                    contentDescription = subcategory.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Discount / Tag pill
                if (!subcategory.discountTag.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .background(OramiCoral, RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = subcategory.discountTag,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.5.sp
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            // Card Text Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = subcategory.name,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        lineHeight = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${subcategory.itemCount}+ Items",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier.size(10.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Compact Product Card for Right Content Area Grid
 */
@Composable
private fun CompactProductCard(
    product: Product,
    isWishlisted: Boolean,
    onProductClick: (Product) -> Unit,
    onWishlistToggle: (Product) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onProductClick(product) }
            .testTag("compact_product_${product.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = product.imageUrls.firstOrNull(),
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Discount Pill
                if (product.discountPercent > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                            .background(OramiCoral, RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${product.discountPercent}% OFF",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(6.dp)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${product.price.toInt()}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Button(
                        onClick = { onAddToCart(product) },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text("Add", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp))
                    }
                }
            }
        }
    }
}

/**
 * Friendly Empty State Composable
 */
@Composable
private fun EmptyCategoryState(
    title: String,
    description: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(actionLabel, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
