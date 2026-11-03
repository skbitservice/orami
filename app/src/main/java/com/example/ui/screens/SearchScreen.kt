package com.example.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ProductCategory
import com.example.ui.components.CategorySidebarNav
import com.example.viewmodel.OramiViewModel
import com.example.viewmodel.SortOption

/**
 * Explore & Categories Screen featuring the Amazon-Inspired Sidebar Category Navigation System.
 *
 * Provides a split-screen interface:
 * - Left vertical sidebar for selecting main product categories with icons and indicators.
 * - Right dynamic content pane displaying 2-column subcategory image grids, drilldown products,
 *   "View All" options, and quick category filtering.
 */
@Composable
fun SearchScreen(
    viewModel: OramiViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val subcategories by viewModel.subcategories.collectAsStateWithLifecycle()
    val selectedSubcategory by viewModel.selectedSubcategory.collectAsStateWithLifecycle()
    val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val wishlistIds by viewModel.wishlistIds.collectAsStateWithLifecycle()

    val hasActiveFilters = filterState.minRating > 0f ||
            filterState.maxPrice < 6000f ||
            filterState.sortOption != SortOption.FEATURED ||
            filterState.inStockOnly

    // 15 Main Product Categories for Amazon-Inspired Sidebar Navigation
    val mainCategories = listOf(
        ProductCategory.AUTOMOTIVES,
        ProductCategory.BABY_TOYS,
        ProductCategory.BACKPACK_LUGGAGE,
        ProductCategory.BATHROOM_LINEN,
        ProductCategory.BED_LINEN,
        ProductCategory.CAMERAS_PHOTOGRAPHY,
        ProductCategory.COMPUTERS_ACCESSORIES,
        ProductCategory.GAMING,
        ProductCategory.HOME_KITCHEN_APPLIANCES,
        ProductCategory.HOME_DECOR,
        ProductCategory.KITCHENWARE,
        ProductCategory.MOBILE_ACCESSORIES,
        ProductCategory.MUSICAL_INSTRUMENTS,
        ProductCategory.PERSONAL_CARE_APPLIANCES,
        ProductCategory.TOYS_GAMES
    )

    CategorySidebarNav(
        categories = mainCategories,
        selectedCategory = selectedCategory,
        subcategories = subcategories,
        selectedSubcategory = selectedSubcategory,
        products = products,
        wishlistIds = wishlistIds,
        onSelectCategory = { viewModel.selectCategory(it) },
        onSelectSubcategory = { viewModel.selectSubcategory(it) },
        onProductClick = { viewModel.openProductDetail(it) },
        onWishlistToggle = { viewModel.toggleWishlist(it) },
        onAddToCart = { viewModel.addToCart(it) },
        onOpenFilterSheet = { viewModel.setFilterSheetOpen(true) },
        hasActiveFilters = hasActiveFilters,
        modifier = modifier
            .fillMaxSize()
            .testTag("search_category_screen")
    )
}
