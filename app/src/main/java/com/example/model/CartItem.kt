package com.example.model

data class CartItem(
    val product: Product,
    val selectedVariant: ProductVariant? = null,
    val quantity: Int = 1
) {
    val totalPrice: Double
        get() = product.price * quantity
}
