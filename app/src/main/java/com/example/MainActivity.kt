package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddToCartFloatingNotification
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.OramiBottomNav
import com.example.ui.components.OramiTopBar
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutSheet
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderSuccessScreen
import com.example.ui.screens.OrdersProfileScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ReelsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.WishlistScreen
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalContext
import com.example.data.ThemePreferences
import com.example.viewmodel.AppScreen
import com.example.viewmodel.OramiViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: OramiViewModel = viewModel()
            val context = LocalContext.current
            val systemDark = isSystemInDarkTheme()

            // Initialize saved theme or system theme
            LaunchedEffect(Unit) {
                viewModel.initTheme(systemDark, context)
            }

            val isDarkState by viewModel.isDarkMode.collectAsStateWithLifecycle()
            val effectiveDark = isDarkState ?: ThemePreferences.getInstance(context).isDarkMode(systemDark)

            MyApplicationTheme(darkTheme = effectiveDark) {
                OramiApp(
                    viewModel = viewModel,
                    isDarkMode = effectiveDark
                )
            }
        }
    }
}

@Composable
fun OramiApp(
    viewModel: OramiViewModel = viewModel(),
    isDarkMode: Boolean = false
) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val wishlistIds by viewModel.wishlistIds.collectAsStateWithLifecycle()
    val isFilterSheetOpen by viewModel.isFilterSheetOpen.collectAsStateWithLifecycle()
    val isCheckoutSheetOpen by viewModel.isCheckoutSheetOpen.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val userNotice by viewModel.userNotice.collectAsStateWithLifecycle()
    val cartTotal by viewModel.cartTotal.collectAsStateWithLifecycle()
    val lastAddedToCart by viewModel.lastAddedToCart.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userNotice) {
        userNotice?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearNotice()
        }
    }

    var isSplashVisible by remember { androidx.compose.runtime.mutableStateOf(true) }

    if (isSplashVisible) {
        com.example.ui.screens.SplashScreen(
            onSplashFinished = { isSplashVisible = false }
        )
        return
    }

    val showTopBar = currentScreen != AppScreen.PRODUCT_DETAIL &&
            currentScreen != AppScreen.ORDER_SUCCESS &&
            currentScreen != AppScreen.REELS
    val showBottomNav = currentScreen != AppScreen.PRODUCT_DETAIL && currentScreen != AppScreen.ORDER_SUCCESS

    androidx.activity.compose.BackHandler(enabled = currentScreen != AppScreen.HOME) {
        viewModel.handleBack()
    }

    val totalCartCount = cartItems.sumOf { it.quantity }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            if (showTopBar) {
                OramiTopBar(
                    currentScreen = currentScreen,
                    cartItemCount = totalCartCount,
                    wishlistCount = wishlistIds.size,
                    isDarkMode = isDarkMode,
                    onToggleTheme = { viewModel.toggleTheme(context) },
                    onBackClick = { viewModel.handleBack() },
                    onSearchClick = { viewModel.navigateTo(AppScreen.SEARCH) },
                    onFilterClick = { viewModel.setFilterSheetOpen(true) },
                    onWishlistClick = { viewModel.navigateTo(AppScreen.WISHLIST) },
                    onCartClick = { viewModel.navigateTo(AppScreen.CART) }
                )
            }
        },
        bottomBar = {
            if (showBottomNav) {
                OramiBottomNav(
                    currentScreen = currentScreen,
                    wishlistCount = wishlistIds.size,
                    cartItemCount = totalCartCount,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                AppScreen.SEARCH -> SearchScreen(viewModel = viewModel)
                AppScreen.PRODUCT_DETAIL -> ProductDetailScreen(viewModel = viewModel)
                AppScreen.CART -> CartScreen(viewModel = viewModel)
                AppScreen.WISHLIST -> WishlistScreen(viewModel = viewModel)
                AppScreen.ORDERS_PROFILE -> OrdersProfileScreen(viewModel = viewModel)
                AppScreen.ORDER_SUCCESS -> OrderSuccessScreen(viewModel = viewModel)
                AppScreen.REELS -> ReelsScreen(viewModel = viewModel)
            }

            // Floating interactive Add to Cart notification
            AddToCartFloatingNotification(
                event = lastAddedToCart,
                onDismiss = { viewModel.clearAddToCartNotification() },
                onViewBag = {
                    viewModel.clearAddToCartNotification()
                    viewModel.navigateTo(AppScreen.CART)
                }
            )

            // Refine Filters Modal Sheet
            if (isFilterSheetOpen) {
                FilterBottomSheet(
                    filterState = filterState,
                    onDismiss = { viewModel.setFilterSheetOpen(false) },
                    onApply = { newFilters ->
                        viewModel.updateFilter { newFilters }
                        viewModel.setFilterSheetOpen(false)
                    },
                    onReset = {
                        viewModel.resetFilters()
                        viewModel.setFilterSheetOpen(false)
                    }
                )
            }

            // Checkout Sheet
            if (isCheckoutSheetOpen) {
                CheckoutSheet(
                    viewModel = viewModel,
                    cartTotal = cartTotal,
                    onDismiss = { viewModel.setCheckoutSheetOpen(false) }
                )
            }
        }
    }
}
