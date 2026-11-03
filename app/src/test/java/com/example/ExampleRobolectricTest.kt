package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.OramiRepository
import com.example.model.ProductCategory
import com.example.viewmodel.AppScreen
import com.example.viewmodel.OramiViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verifies Orami app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Orami", appName)
    }

    @Test
    fun `viewModel cart addition and coupon discount calculation works`() {
        val viewModel = OramiViewModel()
        val sampleProduct = OramiRepository.getSeedProducts().first()

        viewModel.addToCart(sampleProduct, quantity = 2)
        assertEquals(1, viewModel.cartItems.value.size)
        assertEquals(2, viewModel.cartItems.value.first().quantity)
        assertEquals(sampleProduct.price * 2, viewModel.cartSubtotal.value, 0.01)

        // Apply ORAMI20 coupon (20% off)
        viewModel.applyCoupon("ORAMI20")
        assertNotNull(viewModel.appliedCoupon.value)
        val expectedDiscount = ((sampleProduct.price * 2) * 20 / 100.0).coerceAtMost(600.0)
        assertEquals(expectedDiscount, viewModel.cartDiscount.value, 0.01)
    }

    @Test
    fun `wishlist toggle works as expected`() {
        val viewModel = OramiViewModel()
        val sampleProduct = OramiRepository.getSeedProducts().first()

        viewModel.toggleWishlist(sampleProduct)
        assertTrue(viewModel.isWishlisted(sampleProduct.id))

        viewModel.toggleWishlist(sampleProduct)
        assertTrue(!viewModel.isWishlisted(sampleProduct.id))
    }

    @Test
    fun `checkout creates and records order`() {
        val viewModel = OramiViewModel()
        val sampleProduct = OramiRepository.getSeedProducts().first()

        viewModel.addToCart(sampleProduct, quantity = 1)
        viewModel.completeCheckout("123 Test Street, Mumbai 400001", "UPI")

        assertEquals(AppScreen.ORDER_SUCCESS, viewModel.currentScreen.value)
        assertNotNull(viewModel.lastPlacedOrder.value)
        assertTrue(viewModel.cartItems.value.isEmpty())
    }

    @Test
    fun `login signup and logout flows work properly`() {
        val viewModel = OramiViewModel()

        // Logout
        viewModel.logout()
        assertFalse(viewModel.isLoggedIn.value)

        // Login
        val loginSuccess = viewModel.login("testuser@gmail.com", "password123")
        assertTrue(loginSuccess)
        assertTrue(viewModel.isLoggedIn.value)
        assertEquals("Testuser", viewModel.userProfile.value.name)

        // Signup
        val signupSuccess = viewModel.signup("Aarav Roy", "aarav@gmail.com", "+91 9988776655", "secret123")
        assertTrue(signupSuccess)
        assertEquals("Aarav Roy", viewModel.userProfile.value.name)
        assertEquals("aarav@gmail.com", viewModel.userProfile.value.email)
    }

    @Test
    fun `category selection updates selectedCategory correctly`() {
        val viewModel = OramiViewModel()
        viewModel.selectCategory(ProductCategory.GAMING)
        assertEquals(ProductCategory.GAMING, viewModel.selectedCategory.value)
    }

    @Test
    fun `subcategory selection filters products and reset restores full list`() {
        val viewModel = OramiViewModel()
        val allSubcategories = viewModel.subcategories.value
        assertTrue("Subcategories should be loaded", allSubcategories.isNotEmpty())

        val carCareSubcategory = allSubcategories.find { it.id == "sub_auto_care" }
        assertNotNull("Car care subcategory should exist", carCareSubcategory)

        viewModel.selectCategory(ProductCategory.AUTOMOTIVES)
        viewModel.selectSubcategory(carCareSubcategory)
        assertEquals(carCareSubcategory, viewModel.selectedSubcategory.value)

        val matchingProducts = OramiRepository().getProductsForSubcategory(carCareSubcategory?.id ?: "")
        assertTrue("Matching products should not be empty", matchingProducts.isNotEmpty())
        assertTrue("All matching products should match subcategory id", matchingProducts.all { it.subcategoryId == carCareSubcategory?.id })

        // Clear subcategory
        viewModel.selectSubcategory(null)
        assertEquals(null, viewModel.selectedSubcategory.value)
    }

    @Test
    fun `delivery fee is zero when subtotal meets 399 threshold`() {
        val viewModel = OramiViewModel()
        val expensiveProduct = OramiRepository.getSeedProducts().first { it.price >= 399.0 }
        viewModel.addToCart(expensiveProduct, quantity = 1)
        assertEquals(0.0, viewModel.deliveryFee.value, 0.01)
    }

    @Test
    fun `theme toggle updates state and persists in ThemePreferences`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val themePrefs = com.example.data.ThemePreferences(context)
        
        val viewModel = OramiViewModel()
        // Initialize with light theme
        viewModel.initTheme(systemInDarkTheme = false, context = context)
        assertEquals(false, viewModel.isDarkMode.value)

        // Toggle to dark theme
        viewModel.toggleTheme(context = context)
        assertEquals(true, viewModel.isDarkMode.value)
        assertTrue(themePrefs.isDarkMode(systemDefault = false))

        // Toggle back to light theme
        viewModel.toggleTheme(context = context)
        assertEquals(false, viewModel.isDarkMode.value)
        assertFalse(themePrefs.isDarkMode(systemDefault = true))
    }
}
