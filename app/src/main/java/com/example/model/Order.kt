package com.example.model

enum class OrderStatus(val title: String, val subtitle: String) {
    CONFIRMED("Order Placed", "Your order has been received & confirmed"),
    PACKED("Packed at Hub", "Carefully packed & quality checked by Orami"),
    IN_TRANSIT("In Transit", "Shipped via express courier partner"),
    DELIVERED("Delivered", "Package safely handed over")
}

data class TrackingStep(
    val status: OrderStatus,
    val timestamp: String,
    val location: String,
    val isCompleted: Boolean
)

data class Order(
    val orderId: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val discount: Double,
    val deliveryFee: Double,
    val total: Double,
    val shippingAddress: String,
    val paymentMethod: String,
    val orderDate: String,
    val currentStatus: OrderStatus,
    val trackingSteps: List<TrackingStep>
)
