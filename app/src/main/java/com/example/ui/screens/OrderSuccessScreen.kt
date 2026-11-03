package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Order
import com.example.model.TrackingStep
import com.example.ui.theme.OramiEmerald
import com.example.ui.theme.OramiEmeraldLight
import com.example.ui.theme.OramiCoral
import com.example.util.SoundEffectHelper
import com.example.viewmodel.AppScreen
import com.example.viewmodel.OramiViewModel
import kotlin.random.Random

private data class ConfettiParticle(
    val initialXRatio: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
    val isCircle: Boolean
)

@Composable
fun OrderSuccessScreen(
    viewModel: OramiViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(AppScreen.HOME)
    }

    val order = viewModel.lastPlacedOrder.collectAsStateWithLifecycle().value
    val haptic = LocalHapticFeedback.current

    // Play triumphant celebratory sound and haptic vibration on entry
    LaunchedEffect(Unit) {
        SoundEffectHelper.playOrderConfirmationChime()
        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
    }

    // Spring bounce scale animation for the success check badge
    val checkmarkScale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        checkmarkScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    // Infinite pulsing wave animation for background ring
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    // Confetti falling particles animation
    val confettiProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confetti"
    )

    val confettiColors = listOf(
        Color(0xFFFF5722), // Orami Coral
        Color(0xFF10B981), // Emerald
        Color(0xFFFFB300), // Gold
        Color(0xFF3B82F6), // Blue
        Color(0xFFEC4899), // Pink
        Color(0xFF8B5CF6)  // Purple
    )

    val particles = remember {
        List(45) {
            ConfettiParticle(
                initialXRatio = Random.nextFloat(),
                speed = 0.5f + Random.nextFloat() * 0.8f,
                size = 8f + Random.nextFloat() * 12f,
                color = confettiColors[Random.nextInt(confettiColors.size)],
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                isCircle = Random.nextBoolean()
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Confetti Canvas Layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            particles.forEach { p ->
                val currentY = ((confettiProgress * p.speed) % 1f) * height
                val currentX = (p.initialXRatio * width) + (kotlin.math.sin((confettiProgress * 6.28f * p.speed).toDouble()) * 25f).toFloat()
                val rotation = confettiProgress * p.rotationSpeed

                rotate(degrees = rotation, pivot = Offset(currentX, currentY)) {
                    if (p.isCircle) {
                        drawCircle(
                            color = p.color.copy(alpha = 0.85f),
                            radius = p.size / 2,
                            center = Offset(currentX, currentY)
                        )
                    } else {
                        drawRect(
                            color = p.color.copy(alpha = 0.85f),
                            topLeft = Offset(currentX - p.size / 2, currentY - p.size / 4),
                            size = Size(p.size, p.size / 2)
                        )
                    }
                }
            }
        }

        // Scrollable Confirmation Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .testTag("order_success_screen"),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 28.dp)
        ) {
            item {
                // Celebratory Animated Checkmark with Pulsing Aura
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(checkmarkScale.value),
                    contentAlignment = Alignment.Center
                ) {
                    // Radiating wave ring
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(pulseScale)
                            .background(
                                color = OramiEmerald.copy(alpha = pulseAlpha),
                                shape = CircleShape
                            )
                    )

                    // Outer soft circle
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .background(OramiEmeraldLight, CircleShape)
                            .border(2.dp, OramiEmerald.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Inner solid emerald circle with checkmark
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF34D399),
                                            OramiEmerald
                                        )
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Order Confirmed",
                                tint = Color.White,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Order Confirmed! 🎉",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Your order is confirmed & express packaging has begun.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Order ID Pill & Replay Sound Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Order ID: ${order?.orderId ?: "ORM-XXXXX"}",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Sound Replay Button
                    OutlinedButton(
                        onClick = {
                            SoundEffectHelper.playOrderConfirmationChime()
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        },
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("replay_sound_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Play Sound",
                            tint = OramiCoral,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Chime",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = OramiCoral
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Live Order Tracking Card
            if (order != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "LIVE SHIPMENT STATUS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = OramiEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Express Logistics",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = OramiEmerald
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            order.trackingSteps.forEachIndexed { index, step ->
                                TrackingTimelineRow(
                                    step = step,
                                    isLast = index == order.trackingSteps.size - 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Delivery Address & Payment Summary
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "DELIVERING TO",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = order.shippingAddress,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Payment: ${order.paymentMethod}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Total Paid: ₹${order.total.toInt()}",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = OramiCoral
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Action Buttons
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("success_continue_shopping_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continue Shopping", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.ORDERS_PROFILE) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("success_view_orders_button")
                    ) {
                        Text("View in Orders & Invoices", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TrackingTimelineRow(
    step: TrackingStep,
    isLast: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (step.isCompleted) OramiEmerald else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                if (step.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(34.dp)
                        .background(if (step.isCompleted) OramiEmerald else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 12.dp)) {
            Text(
                text = step.status.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (step.isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${step.location} • ${step.timestamp}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
