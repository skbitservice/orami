package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.model.CartItem
import com.example.model.Coupon
import com.example.ui.theme.OramiCoral
import com.example.ui.theme.OramiEmerald
import com.example.ui.theme.OramiEmeraldLight
import com.example.viewmodel.AppScreen
import com.example.viewmodel.OramiViewModel

@Composable
fun CartScreen(
    viewModel: OramiViewModel,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val appliedCoupon by viewModel.appliedCoupon.collectAsStateWithLifecycle()
    val couponMessage by viewModel.couponMessage.collectAsStateWithLifecycle()
    val subtotal by viewModel.cartSubtotal.collectAsStateWithLifecycle()
    val discount by viewModel.cartDiscount.collectAsStateWithLifecycle()
    val deliveryFee by viewModel.deliveryFee.collectAsStateWithLifecycle()
    val total by viewModel.cartTotal.collectAsStateWithLifecycle()
    val availableCoupons by viewModel.availableCoupons.collectAsStateWithLifecycle()

    var couponInput by remember { mutableStateOf("") }

    if (cartItems.isEmpty()) {
        // Empty Cart State
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your bag is empty",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Explore curated collections and add your favorite minimalist pieces.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.navigateTo(AppScreen.HOME) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("cart_empty_explore_button")
            ) {
                Text("Start Shopping", fontWeight = FontWeight.Bold)
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .testTag("cart_screen")
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 96.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Free Shipping Progress
                item {
                    val progress = (subtotal / 399.0).coerceIn(0.0, 1.0).toFloat()
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (subtotal >= 399.0 || appliedCoupon?.code == "FREESHIP") {
                                        "🎉 You have unlocked Free Express Delivery!"
                                    } else {
                                        "Add ₹${(399.0 - subtotal).toInt()} more for Free Delivery"
                                    },
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = if (subtotal >= 399.0) OramiEmerald else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = OramiEmerald,
                                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        }
                    }
                }

                // Item List
                item {
                    Text(
                        text = "ITEMS IN BAG (${cartItems.sumOf { it.quantity }})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(cartItems, key = { "${it.product.id}_${it.selectedVariant?.id}" }) { item ->
                    CartItemRow(
                        item = item,
                        onQuantityChange = { newQty -> viewModel.updateCartItemQuantity(item, newQty) },
                        onRemove = { viewModel.removeFromCart(item) }
                    )
                }

                // Promo Coupon Voucher Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = OramiCoral,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Coupons & Offers",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (appliedCoupon != null) {
                            // Applied Coupon Pill
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(OramiEmeraldLight, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${appliedCoupon!!.code} APPLIED",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = OramiEmerald
                                    )
                                    Text(
                                        text = "Saved ₹${discount.toInt()} on this order",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OramiEmerald
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.removeCoupon() },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove coupon",
                                        tint = OramiEmerald
                                    )
                                }
                            }
                        } else {
                            // Coupon Input Field
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = couponInput,
                                    onValueChange = { couponInput = it },
                                    placeholder = { Text("Enter coupon code") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("coupon_input_field")
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        viewModel.applyCoupon(couponInput)
                                        couponInput = ""
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    modifier = Modifier.testTag("coupon_apply_button")
                                ) {
                                    Text("Apply")
                                }
                            }

                            if (couponMessage != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = couponMessage!!,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (appliedCoupon != null) OramiEmerald else OramiCoral
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quick Pick Chips
                            Text(
                                text = "AVAILABLE VOUCHERS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            availableCoupons.forEach { coupon ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                        .clickable { viewModel.applyCoupon(coupon.code) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = coupon.code,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = coupon.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "Apply",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = OramiCoral
                                    )
                                }
                            }
                        }
                    }
                }

                // Price Breakdown Bill
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "BILL DETAILS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            BillRow(label = "Item Total", value = "₹${subtotal.toInt()}")

                            if (discount > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                BillRow(
                                    label = "Coupon Discount",
                                    value = "-₹${discount.toInt()}",
                                    valueColor = OramiEmerald
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            BillRow(
                                label = "Express Delivery Fee",
                                value = if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}",
                                valueColor = if (deliveryFee == 0.0) OramiEmerald else MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Grand Total",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "₹${total.toInt()}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Sticky Bottom Checkout Button
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL AMOUNT",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${total.toInt()}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                        )
                    }

                    Button(
                        onClick = { viewModel.setCheckoutSheetOpen(true) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("cart_proceed_checkout_button")
                    ) {
                        Text("Proceed to Checkout", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.product.imageUrls.firstOrNull(),
                contentDescription = item.product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1
                )

                if (item.selectedVariant != null) {
                    Text(
                        text = "Option: ${item.selectedVariant.name}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "₹${item.totalPrice.toInt()}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Stepper
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                IconButton(
                    onClick = { onQuantityChange(item.quantity - 1) },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = if (item.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                        contentDescription = "Decrease",
                        modifier = Modifier.size(14.dp)
                    )
                }

                Text(
                    text = item.quantity.toString(),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                IconButton(
                    onClick = { onQuantityChange(item.quantity + 1) },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BillRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = valueColor
        )
    }
}
