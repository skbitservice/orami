package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.example.ui.theme.OramiCoral
import com.example.viewmodel.AppScreen

@Composable
fun OramiTopBar(
    currentScreen: AppScreen,
    cartItemCount: Int,
    wishlistCount: Int,
    isDarkMode: Boolean = false,
    onToggleTheme: () -> Unit = {},
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentScreen == AppScreen.HOME) {
                // Home Screen: Brand Logo
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    OramiLogo(
                        fontSize = 19.sp,
                        ringSize = 22.dp,
                        dotSize = 6.dp,
                        showTagline = false
                    )
                }

                // Premium Glassmorphism Dark/Light Theme Toggle
                GlassmorphismThemeToggle(
                    isDarkMode = isDarkMode,
                    onToggle = onToggleTheme,
                    modifier = Modifier.padding(end = 2.dp)
                )

                // Search Icon
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.testTag("top_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search Catalog",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Wishlist Icon with badge
                IconButton(
                    onClick = onWishlistClick,
                    modifier = Modifier.testTag("top_wishlist_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (wishlistCount > 0) {
                                Badge(
                                    containerColor = OramiCoral,
                                    contentColor = MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                                ) {
                                    Text(
                                        text = wishlistCount.toString(),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (wishlistCount > 0) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (wishlistCount > 0) OramiCoral else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Cart Icon with badge
                IconButton(
                    onClick = onCartClick,
                    modifier = Modifier.testTag("top_cart_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (cartItemCount > 0) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                                ) {
                                    Text(
                                        text = cartItemCount.toString(),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (cartItemCount > 0) Icons.Default.ShoppingBag else Icons.Outlined.ShoppingBag,
                            contentDescription = "Shopping Cart",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                // Sub-Screens: Navigation Header with Back Arrow and Screen Title
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("top_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                val title = when (currentScreen) {
                    AppScreen.SEARCH -> "Explore & Search"
                    AppScreen.CART -> "Shopping Bag"
                    AppScreen.WISHLIST -> "My Wishlist"
                    AppScreen.ORDERS_PROFILE -> "Account & Orders"
                    else -> "Orami"
                }

                val subtitle = when (currentScreen) {
                    AppScreen.CART -> if (cartItemCount > 0) "$cartItemCount items" else null
                    AppScreen.WISHLIST -> if (wishlistCount > 0) "$wishlistCount saved" else null
                    else -> null
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Premium Glassmorphism Dark/Light Theme Toggle
                GlassmorphismThemeToggle(
                    isDarkMode = isDarkMode,
                    onToggle = onToggleTheme,
                    modifier = Modifier.padding(end = 2.dp)
                )

                // Contextual Actions on right
                if (currentScreen != AppScreen.WISHLIST) {
                    IconButton(
                        onClick = onWishlistClick,
                        modifier = Modifier.testTag("top_wishlist_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (wishlistCount > 0) {
                                    Badge(
                                        containerColor = OramiCoral,
                                        contentColor = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                                    ) {
                                        Text(
                                            text = wishlistCount.toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (wishlistCount > 0) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (wishlistCount > 0) OramiCoral else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                if (currentScreen != AppScreen.CART) {
                    IconButton(
                        onClick = onCartClick,
                        modifier = Modifier.testTag("top_cart_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (cartItemCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                                    ) {
                                        Text(
                                            text = cartItemCount.toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (cartItemCount > 0) Icons.Default.ShoppingBag else Icons.Outlined.ShoppingBag,
                                contentDescription = "Shopping Cart",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
