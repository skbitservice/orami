package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OramiCoral
import com.example.viewmodel.AppScreen

// Premium Dark Glassmorphism color palette
private val DarkGlassBackground = Brush.verticalGradient(
    colors = listOf(
        Color(0xEA21242C), // Top sheen
        Color(0xF216181E), // Middle dark translucency
        Color(0xF8101115)  // Bottom depth
    )
)

private val GlassBorderBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0x50FFFFFF), // Specular highlight
        Color(0x20FFFFFF), // Diffusion
        Color(0x0EFFFFFF)  // Soft edge
    )
)

private val ActiveIndicatorBrush = Brush.horizontalGradient(
    colors = listOf(
        Color(0x38FF5722),
        Color(0x28FF7A50)
    )
)

@Composable
fun OramiBottomNav(
    currentScreen: AppScreen,
    wishlistCount: Int,
    cartItemCount: Int,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    // Floating Navigation Container with system navigation bar insets
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        val navShape = RoundedCornerShape(28.dp)

        // Frosted Glass Floating Pill Bar
        Box(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = 14.dp,
                    shape = navShape,
                    spotColor = Color(0x70000000),
                    ambientColor = Color(0x40000000)
                )
                .clip(navShape)
                .background(DarkGlassBackground)
                .border(
                    width = 1.dp,
                    brush = GlassBorderBrush,
                    shape = navShape
                )
        ) {
            // Subtle top specular sheen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x40FFFFFF),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Evenly distributed 5-destination items row
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Home
                GlassNavItem(
                    selected = currentScreen == AppScreen.HOME,
                    onClick = { onNavigate(AppScreen.HOME) },
                    activeIcon = Icons.Default.Home,
                    inactiveIcon = Icons.Outlined.Home,
                    label = "Home",
                    testTag = "nav_home_item",
                    badgeCount = 0
                )

                // Tab 2: Explore & Categories
                GlassNavItem(
                    selected = currentScreen == AppScreen.SEARCH,
                    onClick = { onNavigate(AppScreen.SEARCH) },
                    activeIcon = Icons.Default.Search,
                    inactiveIcon = Icons.Outlined.Search,
                    label = "Explore",
                    testTag = "nav_search_item",
                    badgeCount = 0
                )

                // Tab 3: Reels Video Shopping
                GlassNavItem(
                    selected = currentScreen == AppScreen.REELS,
                    onClick = { onNavigate(AppScreen.REELS) },
                    activeIcon = Icons.Default.PlayCircle,
                    inactiveIcon = Icons.Outlined.PlayCircle,
                    label = "Reels",
                    testTag = "nav_reels_item",
                    badgeCount = 0
                )

                // Tab 4: Shopping Bag / Cart
                GlassNavItem(
                    selected = currentScreen == AppScreen.CART,
                    onClick = { onNavigate(AppScreen.CART) },
                    activeIcon = Icons.Default.ShoppingBag,
                    inactiveIcon = Icons.Outlined.ShoppingBag,
                    label = "Bag",
                    testTag = "nav_cart_item",
                    badgeCount = cartItemCount
                )

                // Tab 5: Account & Orders
                GlassNavItem(
                    selected = currentScreen == AppScreen.ORDERS_PROFILE,
                    onClick = { onNavigate(AppScreen.ORDERS_PROFILE) },
                    activeIcon = Icons.Default.Person,
                    inactiveIcon = Icons.Outlined.Person,
                    label = "Account",
                    testTag = "nav_orders_item",
                    badgeCount = 0
                )
            }
        }
    }
}

@Composable
private fun RowScope.GlassNavItem(
    selected: Boolean,
    onClick: () -> Unit,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    label: String,
    testTag: String,
    badgeCount: Int,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.12f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "iconScale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color(0x9EFFFFFF),
        animationSpec = tween(durationMillis = 200),
        label = "iconColor"
    )

    val labelColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color(0x7AFFFFFF),
        animationSpec = tween(durationMillis = 200),
        label = "labelColor"
    )

    val indicatorAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "indicatorAlpha"
    )

    val itemShape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .weight(1f)
            .height(52.dp)
            .testTag(testTag)
            .clip(itemShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color(0x33FFFFFF)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Floating Active Capsule Indicator with Glassmorphism
        if (indicatorAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(44.dp)
                    .graphicsLayer { alpha = indicatorAlpha }
                    .background(ActiveIndicatorBrush, itemShape)
                    .border(
                        width = 1.dp,
                        color = Color(0x40FF5722),
                        shape = itemShape
                    )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 2.dp)
        ) {
            // Icon with optional Badge
            BadgedBox(
                badge = {
                    if (badgeCount > 0) {
                        Badge(
                            containerColor = OramiCoral,
                            contentColor = Color.White,
                            modifier = Modifier
                                .offset(x = 6.dp, y = (-2).dp)
                                .size(15.dp)
                        ) {
                            Text(
                                text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = if (selected) activeIcon else inactiveIcon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier
                        .size(20.dp)
                        .scale(iconScale)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Text Label
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                ),
                color = labelColor,
                maxLines = 1
            )
        }
    }
}
