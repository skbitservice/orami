package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation state drivers
    val ringScale = remember { Animatable(0f) }
    val ringAlpha = remember { Animatable(0f) }
    val textSlide = remember { Animatable(20f) }
    val textAlpha = remember { Animatable(0f) }
    val dotBounce = remember { Animatable(-30f) }
    val dotAlpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Stage 1: Red 'O' ring springs into view
        launch {
            ringAlpha.animateTo(1f, tween(350))
        }
        launch {
            ringScale.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            )
        }

        delay(300)

        // Stage 2: 'RAM' & 'I' slide in with alpha
        launch {
            textSlide.animateTo(0f, tween(400, easing = FastOutSlowInEasing))
        }
        launch {
            textAlpha.animateTo(1f, tween(350))
        }

        delay(250)

        // Stage 3: The red dot drops down with bounce onto the 'I'
        launch {
            dotAlpha.animateTo(1f, tween(200))
        }
        launch {
            dotBounce.animateTo(
                -4f,
                spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium)
            )
        }

        delay(350)

        // Stage 4: "MY ONLY CHOICE" tagline fades in
        taglineAlpha.animateTo(1f, tween(450))

        // Hold briefly for visual delight, then navigate
        delay(1100)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Tap anywhere to skip directly into shop
                onSplashFinished()
            }
            .testTag("splash_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Animated Red 'O' Ring
                Box(
                    modifier = Modifier
                        .scale(ringScale.value)
                        .alpha(ringAlpha.value)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF2A2A),
                                    Color(0xFFD60000),
                                    Color(0xFF9E0000)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Animated "RAM"
                Text(
                    text = "RAM",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .offset(x = textSlide.value.dp)
                        .alpha(textAlpha.value)
                )

                // Animated "I" with dropping red dot
                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .offset(x = textSlide.value.dp)
                        .alpha(textAlpha.value),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Floating bouncing Red Dot
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = dotBounce.value.dp)
                            .alpha(dotAlpha.value)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFFFF3B30), Color(0xFFCC0000))
                                )
                            )
                    )

                    // "I" stem
                    Text(
                        text = "I",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // ".in"
                Text(
                    text = ".in",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .offset(x = textSlide.value.dp)
                        .alpha(textAlpha.value)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Animated Tagline
            Text(
                text = "MY ONLY CHOICE",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp,
                    fontFamily = FontFamily.SansSerif
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                modifier = Modifier.alpha(taglineAlpha.value)
            )
        }

        // Tap to skip hint at bottom
        Text(
            text = "Tap anywhere to skip",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        )
    }
}
