package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Product
import com.example.model.ProductVariant
import com.example.ui.theme.OramiEmerald
import com.example.ui.theme.OramiEmeraldLight
import com.example.ui.theme.OramiCoral
import kotlinx.coroutines.delay

data class AddToCartEvent(
    val id: Long = System.currentTimeMillis(),
    val product: Product,
    val variant: ProductVariant?,
    val quantity: Int = 1
)

@Composable
fun AddToCartFloatingNotification(
    event: AddToCartEvent?,
    onDismiss: () -> Unit,
    onViewBag: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    // Trigger subtle haptic pulse when new item is added
    LaunchedEffect(event?.id) {
        if (event != null) {
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
        }
    }

    val progress = remember { Animatable(1f) }

    LaunchedEffect(event?.id) {
        if (event != null) {
            progress.snapTo(1f)
            progress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 3500, easing = LinearEasing)
            )
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = event != null,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) + fadeIn(animationSpec = tween(250)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(200)
        ) + fadeOut(animationSpec = tween(200)),
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("add_to_cart_animated_banner")
    ) {
        if (event != null) {
            val cardShape = RoundedCornerShape(20.dp)
            val isDark = MaterialTheme.colorScheme.background.red < 0.5f

            val containerBackground = if (isDark) {
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xF0222733),
                        Color(0xF8181C26)
                    )
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xF5FFFFFF),
                        Color(0xFAFAFBFD)
                    )
                )
            }

            val borderColor = if (isDark) Color(0x33FFFFFF) else Color(0x22FF5722)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = cardShape,
                        spotColor = if (isDark) Color(0x90000000) else Color(0x40FF5722)
                    )
                    .clip(cardShape)
                    .background(containerBackground)
                    .border(1.dp, borderColor, cardShape)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Success animated green badge
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(OramiEmeraldLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Added to Cart",
                                tint = OramiEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Product Image thumbnail
                        AsyncImage(
                            model = event.product.imageUrls.firstOrNull() ?: "",
                            contentDescription = event.product.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Product info & Added title
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Added to Bag!",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = OramiEmerald
                                )
                                if (event.quantity > 1) {
                                    Text(
                                        text = " (${event.quantity}x)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                text = event.product.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "₹${(event.product.price * event.quantity).toInt()}${event.variant?.let { " • ${it.name}" } ?: ""}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = OramiCoral
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Action: View Bag Button
                        Button(
                            onClick = onViewBag,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OramiCoral,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("notification_view_bag_button")
                        ) {
                            Text(
                                text = "View Bag",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        // Close Button
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(28.dp)
                                .padding(start = 4.dp)
                                .testTag("notification_close_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Bottom animated auto-dismiss progress line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.value)
                            .height(2.5.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        OramiEmerald,
                                        OramiCoral
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}
