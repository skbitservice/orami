package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Premium Glassmorphism Dark/Light Theme Toggle Button.
 *
 * Features:
 * - Floating pill-shaped glass container with frosted transparency and subtle backdrop blur.
 * - Translucent border with soft depth shadow.
 * - Sun icon for Light Mode and Moon icon for Dark Mode.
 * - Smooth sliding glass indicator with spring physics.
 * - Fluid icon rotation, scale, and color transitions.
 * - Full Android accessibility touch target standards (>=48dp).
 */
@Composable
fun GlassmorphismThemeToggle(
    isDarkMode: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Sliding thumb translation between Sun (0.dp) and Moon (32.dp)
    val thumbOffset by animateDpAsState(
        targetValue = if (isDarkMode) 32.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = 0.74f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "theme_toggle_thumb_offset"
    )

    // Sun icon animation values
    val sunRotation by animateFloatAsState(
        targetValue = if (isDarkMode) -90f else 0f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "sun_rotation"
    )
    val sunScale by animateFloatAsState(
        targetValue = if (isDarkMode) 0.82f else 1.05f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "sun_scale"
    )
    val sunColor by animateColorAsState(
        targetValue = if (isDarkMode) Color(0x66FFFFFF) else Color(0xFFF59E0B),
        animationSpec = tween(durationMillis = 280),
        label = "sun_color"
    )

    // Moon icon animation values
    val moonRotation by animateFloatAsState(
        targetValue = if (isDarkMode) 0f else 90f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "moon_rotation"
    )
    val moonScale by animateFloatAsState(
        targetValue = if (isDarkMode) 1.05f else 0.82f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "moon_scale"
    )
    val moonColor by animateColorAsState(
        targetValue = if (isDarkMode) Color(0xFF93C5FD) else Color(0x6664748B),
        animationSpec = tween(durationMillis = 280),
        label = "moon_color"
    )

    val contentDesc = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode"

    // Outer container ensures minimum 48x48dp touch target for accessibility
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag("theme_toggle_button")
            .semantics {
                this.contentDescription = contentDesc
                this.role = Role.Switch
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 24.dp),
                onClick = onToggle
            ),
        contentAlignment = Alignment.Center
    ) {
        // Floating Frosted Glass Pill
        val pillShape = RoundedCornerShape(16.dp)
        val glassBorderColor = if (isDarkMode) Color(0x38FFFFFF) else Color(0x1F000000)
        val glassBgBrush = if (isDarkMode) {
            Brush.linearGradient(
                colors = listOf(
                    Color(0x33FFFFFF),
                    Color(0x1AFFFFFF),
                    Color(0x2BFFFFFF)
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color(0x1C000000),
                    Color(0x0E000000),
                    Color(0x16000000)
                )
            )
        }

        Box(
            modifier = Modifier
                .width(66.dp)
                .height(32.dp)
                .shadow(
                    elevation = if (isDarkMode) 4.dp else 2.dp,
                    shape = pillShape,
                    spotColor = if (isDarkMode) Color(0x66000000) else Color(0x26000000)
                )
                .clip(pillShape)
                .background(glassBgBrush)
                .border(BorderStroke(1.dp, glassBorderColor), pillShape)
                .padding(3.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Subtle blur highlight layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(6.dp)
                    .background(
                        if (isDarkMode) Color(0x12FFFFFF) else Color(0x08000000)
                    )
            )

            // Animated Sliding Glass Thumb Indicator
            val thumbShape = RoundedCornerShape(13.dp)
            val thumbBgBrush = if (isDarkMode) {
                Brush.linearGradient(
                    colors = listOf(
                        Color(0x59FFFFFF),
                        Color(0x38FFFFFF)
                    )
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFF1F5F9)
                    )
                )
            }
            val thumbBorderColor = if (isDarkMode) Color(0x66FFFFFF) else Color(0x22000000)

            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .width(28.dp)
                    .height(26.dp)
                    .shadow(
                        elevation = if (isDarkMode) 2.dp else 1.5.dp,
                        shape = thumbShape,
                        spotColor = if (isDarkMode) Color(0x88000000) else Color(0x33000000)
                    )
                    .clip(thumbShape)
                    .background(thumbBgBrush)
                    .border(BorderStroke(0.75.dp, thumbBorderColor), thumbShape)
            )

            // Icons Layer (Sun on Left, Moon on Right)
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sun Icon Slot (28.dp wide)
                Box(
                    modifier = Modifier
                        .size(28.dp, 26.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.WbSunny,
                        contentDescription = null, // Handled by outer container semantics
                        tint = sunColor,
                        modifier = Modifier
                            .size(15.dp)
                            .scale(sunScale)
                            .rotate(sunRotation)
                    )
                }

                // Small Spacer between slots
                Box(modifier = Modifier.width(4.dp))

                // Moon Icon Slot (28.dp wide)
                Box(
                    modifier = Modifier
                        .size(28.dp, 26.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.DarkMode,
                        contentDescription = null, // Handled by outer container semantics
                        tint = moonColor,
                        modifier = Modifier
                            .size(14.dp)
                            .scale(moonScale)
                            .rotate(moonRotation)
                    )
                }
            }
        }
    }
}
