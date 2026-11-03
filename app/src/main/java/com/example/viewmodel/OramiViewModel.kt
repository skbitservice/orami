package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.OramiRepository
import com.example.model.CartItem
import com.example.model.Coupon
import com.example.model.Order
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.model.ProductReview
import com.example.model.ProductVariant
import com.example.model.ReelComment
import com.example.model.ReelItem
import com.example.ui.components.AddToCartEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    SEARCH,
    PRODUCT_DETAIL,
    CART,
    WISHLIST,
    ORDERS_PROFILE,
    ORDER_SUCCESS,
    REELS
}

enum class SortOption(val title: String) {
    FEATURED("Featured & Trending"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    RATING("Customer Rating")
}

data class FilterState(
    val maxPrice: Float = 6000f,
    val minRating: Float = 0f,
    val sortOption: SortOption = SortOption.FEATURED,
    val inStockOnly: Boolean = false
)

data class UserProfile(
    val name: String,
    val email: String,
    val phone: String = "+91 98765 43210",
    val memberTier: String = "Orami Gold Member",
    val defaultAddress: String = "B-402, Skyline Residency, Link Road, Mumbai - 400053"
)

class OramiViewModel(
    private val repository: OramiRepository = OramiRepository()
) : ViewModel() {

    private val _isDarkMode = MutableStateFlow<Boolean?>(null)
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    fun initTheme(systemInDarkTheme: Boolean, context: android.content.Context? = null) {
        if (_isDarkMode.value == null) {
            val initial = if (context != null) {
                com.example.data.ThemePreferences.getInstance(context).isDarkMode(systemInDarkTheme)
            } else {
                systemInDarkTheme
            }
            _isDarkMode.value = initial
        }
    }

    fun toggleTheme(context: android.content.Context? = null) {
        val next = !(_isDarkMode.value ?: false)
        _isDarkMode.value = next
        if (context != null) {
            com.example.data.ThemePreferences.getInstance(context).setDarkMode(next)
        }
    }

    fun setDarkMode(isDark: Boolean, context: android.content.Context? = null) {
        _isDarkMode.value = isDark
        if (context != null) {
            com.example.data.ThemePreferences.getInstance(context).setDarkMode(isDark)
        }
    }

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            name = "SK Bit Service",
            email = "skbitservice@gmail.com",
            phone = "+91 98765 43210",
            memberTier = "Orami Gold Member",
            defaultAddress = "B-402, Skyline Residency, Link Road, Mumbai - 400053"
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _screenStack = mutableListOf<AppScreen>(AppScreen.HOME)

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _selectedVariant = MutableStateFlow<ProductVariant?>(null)
    val selectedVariant: StateFlow<ProductVariant?> = _selectedVariant.asStateFlow()

    private val _productReviews = MutableStateFlow<List<ProductReview>>(emptyList())
    val productReviews: StateFlow<List<ProductReview>> = _productReviews.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ProductCategory.AUTOMOTIVES)
    val selectedCategory: StateFlow<ProductCategory> = _selectedCategory.asStateFlow()

    private val _selectedSubcategory = MutableStateFlow<com.example.model.Subcategory?>(null)
    val selectedSubcategory: StateFlow<com.example.model.Subcategory?> = _selectedSubcategory.asStateFlow()

    private val _categorySearchQuery = MutableStateFlow("")
    val categorySearchQuery: StateFlow<String> = _categorySearchQuery.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    private val _isFilterSheetOpen = MutableStateFlow(false)
    val isFilterSheetOpen: StateFlow<Boolean> = _isFilterSheetOpen.asStateFlow()

    private val _isCheckoutSheetOpen = MutableStateFlow(false)
    val isCheckoutSheetOpen: StateFlow<Boolean> = _isCheckoutSheetOpen.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
    val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

    private val _couponMessage = MutableStateFlow<String?>(null)
    val couponMessage: StateFlow<String?> = _couponMessage.asStateFlow()

    private val _wishlistIds = MutableStateFlow<Set<String>>(emptySet())
    val wishlistIds: StateFlow<Set<String>> = _wishlistIds.asStateFlow()

    private val _userNotice = MutableStateFlow<String?>(null)
    val userNotice: StateFlow<String?> = _userNotice.asStateFlow()

    val availableCoupons: StateFlow<List<Coupon>> = repository.availableCoupons
    val orders: StateFlow<List<Order>> = repository.orders
    val subcategories: StateFlow<List<com.example.model.Subcategory>> = repository.subcategories

    private val _lastPlacedOrder = MutableStateFlow<Order?>(null)
    val lastPlacedOrder: StateFlow<Order?> = _lastPlacedOrder.asStateFlow()

    val allProducts: StateFlow<List<Product>> = repository.products

    // Rich animated Add to Cart state
    private val _lastAddedToCart = MutableStateFlow<AddToCartEvent?>(null)
    val lastAddedToCart: StateFlow<AddToCartEvent?> = _lastAddedToCart.asStateFlow()

    fun clearAddToCartNotification() {
        _lastAddedToCart.value = null
    }

    // Reels feed state with initial project video shopping reels
    private val _reels = MutableStateFlow<List<ReelItem>>(createSeedReels(repository.products.value))
    val reels: StateFlow<List<ReelItem>> = _reels.asStateFlow()

    // Filtered Products stream
    val filteredProducts: StateFlow<List<Product>> = combine(
        repository.products,
        _selectedCategory,
        _selectedSubcategory,
        _searchQuery,
        _filterState
    ) { allProducts, category, subcategory, query, filters ->
        allProducts.filter { product ->
            val matchCategory = category == ProductCategory.ALL || product.category == category
            val matchSubcategory = subcategory == null || product.subcategoryId == subcategory.id
            val matchQuery = query.isBlank() ||
                    product.title.contains(query, ignoreCase = true) ||
                    product.brand.contains(query, ignoreCase = true) ||
                    product.category.displayName.contains(query, ignoreCase = true) ||
                    (subcategory != null && subcategory.name.contains(query, ignoreCase = true))
            val matchPrice = product.price <= filters.maxPrice
            val matchRating = product.rating >= filters.minRating
            val matchStock = !filters.inStockOnly || product.inStock

            matchCategory && matchSubcategory && matchQuery && matchPrice && matchRating && matchStock
        }.let { list ->
            when (filters.sortOption) {
                SortOption.FEATURED -> list
                SortOption.PRICE_LOW_HIGH -> list.sortedBy { it.price }
                SortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.price }
                SortOption.RATING -> list.sortedByDescending { it.rating }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Synchronous reactive cart calculations
    private val _cartSubtotal = MutableStateFlow(0.0)
    val cartSubtotal: StateFlow<Double> = _cartSubtotal.asStateFlow()

    private val _cartDiscount = MutableStateFlow(0.0)
    val cartDiscount: StateFlow<Double> = _cartDiscount.asStateFlow()

    private val _deliveryFee = MutableStateFlow(0.0)
    val deliveryFee: StateFlow<Double> = _deliveryFee.asStateFlow()

    private val _cartTotal = MutableStateFlow(0.0)
    val cartTotal: StateFlow<Double> = _cartTotal.asStateFlow()

    private fun recalculateCart() {
        val sub = _cartItems.value.sumOf { it.totalPrice }
        _cartSubtotal.value = sub
        val disc = _appliedCoupon.value?.calculateDiscount(sub) ?: 0.0
        _cartDiscount.value = disc
        val fee = if (sub == 0.0 || sub >= 399.0 || _appliedCoupon.value?.code == "FREESHIP") 0.0 else 49.0
        _deliveryFee.value = fee
        _cartTotal.value = if (sub == 0.0) 0.0 else (sub - disc + fee).coerceAtLeast(0.0)
    }

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            _screenStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun handleBack(): Boolean {
        if (_selectedSubcategory.value != null) {
            _selectedSubcategory.value = null
            return true
        }
        if (_screenStack.size > 1) {
            _screenStack.removeAt(_screenStack.size - 1)
            _currentScreen.value = _screenStack.last()
            return true
        } else if (_currentScreen.value != AppScreen.HOME) {
            _currentScreen.value = AppScreen.HOME
            _screenStack.clear()
            _screenStack.add(AppScreen.HOME)
            return true
        }
        return false
    }

    fun openProductDetail(product: Product) {
        _selectedProduct.value = product
        _selectedVariant.value = product.variants.firstOrNull()
        _productReviews.value = OramiRepository.getSampleReviews(product.id)
        navigateTo(AppScreen.PRODUCT_DETAIL)
    }

    fun selectVariant(variant: ProductVariant) {
        _selectedVariant.value = variant
    }

    fun selectCategory(category: ProductCategory) {
        _selectedCategory.value = category
        _selectedSubcategory.value = null
    }

    fun selectSubcategory(subcategory: com.example.model.Subcategory?) {
        _selectedSubcategory.value = subcategory
    }

    fun setCategorySearchQuery(query: String) {
        _categorySearchQuery.value = query
    }

    fun clearCategorySearch() {
        _categorySearchQuery.value = ""
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFilter(update: FilterState.() -> FilterState) {
        _filterState.value = _filterState.value.update()
    }

    fun resetFilters() {
        _filterState.value = FilterState()
        _selectedCategory.value = ProductCategory.AUTOMOTIVES
        _selectedSubcategory.value = null
    }

    fun setFilterSheetOpen(isOpen: Boolean) {
        _isFilterSheetOpen.value = isOpen
    }

    fun setCheckoutSheetOpen(isOpen: Boolean) {
        _isCheckoutSheetOpen.value = isOpen
    }

    fun toggleWishlist(product: Product) {
        val current = _wishlistIds.value.toMutableSet()
        if (current.contains(product.id)) {
            current.remove(product.id)
            showNotice("Removed from Wishlist")
        } else {
            current.add(product.id)
            showNotice("Saved to Wishlist")
        }
        _wishlistIds.value = current
    }

    fun isWishlisted(productId: String): Boolean = _wishlistIds.value.contains(productId)

    fun addToCart(product: Product, variant: ProductVariant? = null, quantity: Int = 1) {
        val currentItems = _cartItems.value.toMutableList()
        val chosenVariant = variant ?: product.variants.firstOrNull()
        val existingIndex = currentItems.indexOfFirst {
            it.product.id == product.id && it.selectedVariant?.id == chosenVariant?.id
        }

        if (existingIndex >= 0) {
            val existing = currentItems[existingIndex]
            currentItems[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            currentItems.add(CartItem(product = product, selectedVariant = chosenVariant, quantity = quantity))
        }

        _cartItems.value = currentItems
        recalculateCart()

        // Trigger rich interactive animated add-to-cart banner
        _lastAddedToCart.value = AddToCartEvent(
            id = System.currentTimeMillis(),
            product = product,
            variant = chosenVariant,
            quantity = quantity
        )
    }

    fun updateCartItemQuantity(item: CartItem, newQty: Int) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst {
            it.product.id == item.product.id && it.selectedVariant?.id == item.selectedVariant?.id
        }
        if (index >= 0) {
            if (newQty <= 0) {
                current.removeAt(index)
                showNotice("Item removed from cart")
            } else {
                current[index] = current[index].copy(quantity = newQty)
            }
            _cartItems.value = current
            recalculateCart()
        }
    }

    fun removeFromCart(item: CartItem) {
        val current = _cartItems.value.toMutableList()
        current.removeAll { it.product.id == item.product.id && it.selectedVariant?.id == item.selectedVariant?.id }
        _cartItems.value = current
        recalculateCart()
        showNotice("Item removed")
    }

    fun applyCoupon(code: String) {
        val trimmed = code.trim().uppercase()
        val coupon = availableCoupons.value.find { it.code.equals(trimmed, ignoreCase = true) }
        val currentSubtotal = _cartItems.value.sumOf { it.totalPrice }

        if (coupon == null) {
            _couponMessage.value = "Invalid coupon code '$trimmed'"
            return
        }

        if (currentSubtotal < coupon.minSpend) {
            _couponMessage.value = "Minimum order of ₹${coupon.minSpend.toInt()} required for this coupon"
            return
        }

        _appliedCoupon.value = coupon
        recalculateCart()
        val discount = coupon.calculateDiscount(currentSubtotal)
        _couponMessage.value = "Coupon '${coupon.code}' applied! Saved ₹${discount.toInt()}"
        showNotice("Discount of ₹${discount.toInt()} applied!")
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        _couponMessage.value = null
        recalculateCart()
        showNotice("Coupon removed")
    }

    fun completeCheckout(shippingAddress: String, paymentMethod: String) {
        val items = _cartItems.value
        if (items.isEmpty()) return

        val order = repository.placeOrder(
            items = items,
            subtotal = cartSubtotal.value,
            discount = cartDiscount.value,
            deliveryFee = deliveryFee.value,
            total = cartTotal.value,
            address = shippingAddress,
            paymentMethod = paymentMethod
        )

        _lastPlacedOrder.value = order
        _cartItems.value = emptyList()
        _appliedCoupon.value = null
        _couponMessage.value = null
        recalculateCart()
        _isCheckoutSheetOpen.value = false
        navigateTo(AppScreen.ORDER_SUCCESS)
    }

    fun login(email: String, password: String): Boolean {
        if (email.isBlank() || password.isBlank()) {
            showNotice("Please enter email and password")
            return false
        }
        val cleanName = if (email.contains("@")) {
            email.substringBefore("@").replace(".", " ")
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        } else "User"
        _userProfile.value = _userProfile.value.copy(name = cleanName, email = email)
        _isLoggedIn.value = true
        showNotice("Welcome back, $cleanName!")
        return true
    }

    fun signup(name: String, email: String, phone: String, password: String): Boolean {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            showNotice("Please fill in all required fields")
            return false
        }
        _userProfile.value = UserProfile(
            name = name,
            email = email,
            phone = if (phone.isNotBlank()) phone else "+91 98765 43210"
        )
        _isLoggedIn.value = true
        showNotice("Account created! Welcome to Orami, $name!")
        return true
    }

    fun logout() {
        _isLoggedIn.value = false
        showNotice("Logged out successfully")
    }

    fun updateProfile(name: String, phone: String, address: String) {
        _userProfile.value = _userProfile.value.copy(
            name = name,
            phone = phone,
            defaultAddress = address
        )
        showNotice("Profile updated successfully")
    }

    fun showNotice(message: String) {
        _userNotice.value = message
    }

    fun clearNotice() {
        _userNotice.value = null
    }

    // --- Reels Management ---

    fun toggleLikeReel(reelId: String) {
        val updated = _reels.value.map { reel ->
            if (reel.id == reelId) {
                val newLiked = !reel.isLiked
                val newCount = if (newLiked) reel.likesCount + 1 else (reel.likesCount - 1).coerceAtLeast(0)
                reel.copy(isLiked = newLiked, likesCount = newCount)
            } else reel
        }
        _reels.value = updated
    }

    fun addCommentToReel(reelId: String, text: String, authorName: String = "You") {
        if (text.isBlank()) return
        val newComment = ReelComment(
            id = "c_${System.currentTimeMillis()}",
            authorName = authorName,
            text = text.trim(),
            timeAgo = "Just now",
            likesCount = 0
        )
        val updated = _reels.value.map { reel ->
            if (reel.id == reelId) {
                reel.copy(comments = listOf(newComment) + reel.comments)
            } else reel
        }
        _reels.value = updated
    }

    fun addCustomReel(
        title: String,
        description: String,
        videoUrl: String,
        product: Product
    ) {
        val cleanUrl = if (videoUrl.isNotBlank()) videoUrl.trim() else "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        val newReel = ReelItem(
            id = "reel_custom_${System.currentTimeMillis()}",
            title = if (title.isNotBlank()) title.trim() else "My Project Video Showcase",
            description = if (description.isNotBlank()) description.trim() else "Check out this featured product on Orami! #project #showcase #orami",
            videoUrl = cleanUrl,
            taggedProduct = product,
            authorName = _userProfile.value.name,
            authorHandle = "@${_userProfile.value.name.lowercase().replace(" ", "_")}",
            likesCount = 1,
            isLiked = true,
            comments = emptyList(),
            sharesCount = 0,
            isCustomUserVideo = true
        )
        _reels.value = listOf(newReel) + _reels.value
        showNotice("Project Reel Published Successfully!")
    }

    fun buyNowFromReel(product: Product) {
        addToCart(product)
        _isCheckoutSheetOpen.value = true
    }

    companion object {
        fun createSeedReels(products: List<Product>): List<ReelItem> {
            val fallback = products.firstOrNull() ?: Product(
                id = "p-fallback-reel",
                title = "Orami Premium Showcase",
                brand = "Orami",
                category = ProductCategory.AUTOMOTIVES,
                price = 1499.0,
                originalPrice = 2499.0,
                rating = 4.8f,
                reviewCount = 180,
                imageUrls = listOf("https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=600&auto=format&fit=crop&q=80"),
                description = "Premium lifestyle product",
                specs = mapOf("Brand" to "Orami"),
                variants = emptyList(),
                inStock = true,
                subcategoryId = "sub_auto_care"
            )

            val pLuggage = products.find { it.category == ProductCategory.BACKPACK_LUGGAGE } ?: fallback
            val pComp = products.find { it.category == ProductCategory.COMPUTERS_ACCESSORIES || it.category == ProductCategory.GAMING } ?: fallback
            val pLinen = products.find { it.category == ProductCategory.BATHROOM_LINEN } ?: fallback
            val pMobile = products.find { it.category == ProductCategory.MOBILE_ACCESSORIES } ?: fallback
            val pBaby = products.find { it.category == ProductCategory.BABY_TOYS } ?: fallback
            val pAuto = products.find { it.category == ProductCategory.AUTOMOTIVES } ?: fallback

            return listOf(
                ReelItem(
                    id = "reel-diwali-corporate-gifting",
                    title = "🎁✨ Diwali Gifting Preparations in Full Swing at Orami Retail!",
                    description = """From carefully selecting premium gifts to beautifully packing every order, we’re making every corporate gifting experience special. ❤️

Preparing bulk Diwali orders for our corporate clients and getting every gift ready to spread happiness, joy & festive cheer! 🪔

Your Trusted Partner for Corporate & Diwali Gifting!

📦 Bulk & Corporate Orders Welcome
📲 WhatsApp: 9812644480/7419007220
🌐 www.orami.in

Orami Retail – My Only Choice!

#OramiRetail #CorporateGifting #DiwaliGifting #Diwali2026 #CorporateGifts""".trimIndent(),
                    videoUrl = "raw/diwali_corporate_gifting",
                    taggedProduct = pLuggage,
                    authorName = "Orami Retail",
                    authorHandle = "@orami_retail",
                    likesCount = 12480,
                    isLiked = true,
                    sharesCount = 1850,
                    thumbnailUrl = "file:///android_asset/banners/thumb_corporate_gifting.jpg",
                    whatsappContact = "9812644480",
                    comments = listOf(
                        ReelComment("c_g1", "Ramesh Verma (HR)", "Placed bulk order for 250 VIP trolley bags. Outstanding packing & on-time delivery!", "1h ago", 54),
                        ReelComment("c_g2", "Sunil Goel (Corporate)", "VIP luggage and dinner set gifts were loved by our entire corporate team. Highly recommended!", "3h ago", 39),
                        ReelComment("c_g3", "Anjali Sharma", "Diwali corporate gifting sorted with Orami Retail! Great experience contacting via WhatsApp.", "5h ago", 28)
                    )
                ),
                ReelItem(
                    id = "reel-1",
                    title = "Mechanical Gaming RGB Keyboard Typing & Sound Test 🔥",
                    description = "Satisfying blue clicky switches, aircraft aluminum plate & custom Diwali RGB lighting modes! #tech #gaming #setup #orami",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    taggedProduct = pComp,
                    authorName = "Orami Tech Lab",
                    authorHandle = "@oramitech",
                    likesCount = 4280,
                    isLiked = false,
                    sharesCount = 356,
                    comments = listOf(
                        ReelComment("c1", "Rohit Sharma", "Sound is super crisp! What switches are these?", "2h ago", 14),
                        ReelComment("c2", "Priya K", "Just bought this in Diwali sale, delivery was in 24 hours! ❤️", "5h ago", 28),
                        ReelComment("c3", "TechGuru India", "Best budget mechanical board under 3k hands down", "1d ago", 9)
                    )
                ),
                ReelItem(
                    id = "reel-2",
                    title = "Cuddle Cotton 700 GSM Luxury Bath Towel Absorbency Demo 💧",
                    description = "Watch how fast 700 GSM combed cotton absorbs 500ml water in 3 seconds! 100% pure Egyptian cotton feel. #cuddlecotton #luxury #orami",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                    taggedProduct = pLinen,
                    authorName = "Cuddle Cotton Official",
                    authorHandle = "@cuddlecotton",
                    likesCount = 2890,
                    isLiked = true,
                    sharesCount = 210,
                    comments = listOf(
                        ReelComment("c4", "Ananya Verma", "So luxurious! Feeling is like 5-star hotel bath towel 😍", "3h ago", 18),
                        ReelComment("c5", "Sunita Rao", "Ordered the 4-pack pastel set, totally worth it!", "1d ago", 11)
                    )
                ),
                ReelItem(
                    id = "reel-3",
                    title = "Diwali Mobile Accessories: Fast Wireless Charger & Stand ⚡",
                    description = "Strong magnetic MagSafe lock, 15W rapid wireless charge, and folds flat into pocket! #gadgets #mobile #diwalisale",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                    taggedProduct = pMobile,
                    authorName = "Orami Gadgets",
                    authorHandle = "@oramigadgets",
                    likesCount = 5610,
                    isLiked = false,
                    sharesCount = 542,
                    comments = listOf(
                        ReelComment("c6", "Vikram S", "Does it support iPhone 15 and Samsung S24?", "1h ago", 7),
                        ReelComment("c7", "Orami Support", "Yes! Compatible with all Qi and MagSafe enabled devices", "45m ago", 15)
                    )
                ),
                ReelItem(
                    id = "reel-4",
                    title = "Safe & Gentle Newborn Baby Stroller & Comfort Gear 👶",
                    description = "One-hand instant fold, shock-absorbing wheels and UPF 50+ canopy. Best travel companion for infants. #babycare #parenting #orami",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
                    taggedProduct = pBaby,
                    authorName = "Baby World Orami",
                    authorHandle = "@babyworld",
                    likesCount = 1940,
                    isLiked = false,
                    sharesCount = 118,
                    comments = listOf(
                        ReelComment("c8", "Meera Sen", "So easy to fold while carrying baby in the other arm! Highly recommended.", "4h ago", 22)
                    )
                ),
                ReelItem(
                    id = "reel-5",
                    title = "Professional Hydrophobic Car Ceramic Coating Mirror Reflection 🚗",
                    description = "Super slick water beading armor and ultimate deep mirror gloss! Protects from dust and UV rays. #carcare #detailing #orami",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
                    taggedProduct = pAuto,
                    authorName = "AutoPro Detailing",
                    authorHandle = "@autopro",
                    likesCount = 3720,
                    isLiked = false,
                    sharesCount = 320,
                    comments = listOf(
                        ReelComment("c9", "Karan Dave", "Applied on my black Creta, stunning mirror reflections!", "6h ago", 34)
                    )
                )
            )
        }
    }
}
