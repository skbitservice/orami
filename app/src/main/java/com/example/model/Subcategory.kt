package com.example.model

/**
 * Represents a product subcategory within a parent [ProductCategory],
 * supporting multi-level navigation in the Amazon-inspired sidebar system.
 */
data class Subcategory(
    val id: String,
    val category: ProductCategory,
    val name: String,
    val imageUrl: String,
    val itemCount: Int,
    val tagline: String = "",
    val discountTag: String? = null,
    val isPopular: Boolean = false
)
