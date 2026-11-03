package com.example.model

data class Coupon(
    val code: String,
    val discountPercent: Int,
    val description: String,
    val minSpend: Double = 0.0,
    val maxDiscount: Double = 1000.0
) {
    fun calculateDiscount(subtotal: Double): Double {
        if (subtotal < minSpend) return 0.0
        val discount = (subtotal * discountPercent) / 100.0
        return discount.coerceAtMost(maxDiscount)
    }
}
