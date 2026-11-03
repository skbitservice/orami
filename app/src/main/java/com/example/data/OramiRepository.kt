package com.example.data

import com.example.model.CartItem
import com.example.model.Coupon
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.model.ProductReview
import com.example.model.ProductVariant
import com.example.model.Subcategory
import com.example.model.TrackingStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OramiRepository {

    private val _products = MutableStateFlow<List<Product>>(getSeedProducts())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _subcategories = MutableStateFlow<List<Subcategory>>(getSeedSubcategories())
    val subcategories: StateFlow<List<Subcategory>> = _subcategories.asStateFlow()

    private val _availableCoupons = MutableStateFlow<List<Coupon>>(getSeedCoupons())
    val availableCoupons: StateFlow<List<Coupon>> = _availableCoupons.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(getSeedOrders())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    fun getSubcategories(category: ProductCategory): List<Subcategory> {
        return if (category == ProductCategory.ALL) {
            _subcategories.value
        } else {
            _subcategories.value.filter { it.category == category }
        }
    }

    fun getProductsForSubcategory(subcategoryId: String): List<Product> {
        return _products.value.filter { it.subcategoryId == subcategoryId }
    }

    fun placeOrder(
        items: List<CartItem>,
        subtotal: Double,
        discount: Double,
        deliveryFee: Double,
        total: Double,
        address: String,
        paymentMethod: String
    ): Order {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val orderNum = (10000..99999).random()
        val orderId = "ORM-$orderNum"

        val tracking = listOf(
            TrackingStep(OrderStatus.CONFIRMED, currentDate, "Orami Central Orders", true),
            TrackingStep(OrderStatus.PACKED, "Estimated within 4 hours", "Warehouse Hub, Mumbai", false),
            TrackingStep(OrderStatus.IN_TRANSIT, "Estimated 2 days", "Express Air Logistics", false),
            TrackingStep(OrderStatus.DELIVERED, "Estimated 3-4 days", address, false)
        )

        val newOrder = Order(
            orderId = orderId,
            items = items,
            subtotal = subtotal,
            discount = discount,
            deliveryFee = deliveryFee,
            total = total,
            shippingAddress = address,
            paymentMethod = paymentMethod,
            orderDate = currentDate,
            currentStatus = OrderStatus.CONFIRMED,
            trackingSteps = tracking
        )

        val currentList = _orders.value.toMutableList()
        currentList.add(0, newOrder)
        _orders.value = currentList
        return newOrder
    }

    companion object {
        fun getSeedCoupons(): List<Coupon> = listOf(
            Coupon(
                code = "ORAMI20",
                discountPercent = 20,
                description = "20% off on orders above ₹1,499 (Max ₹600)",
                minSpend = 1499.0,
                maxDiscount = 600.0
            ),
            Coupon(
                code = "WELCOME10",
                discountPercent = 10,
                description = "10% instant off on all first-time purchases",
                minSpend = 499.0,
                maxDiscount = 350.0
            ),
            Coupon(
                code = "FREESHIP",
                discountPercent = 5,
                description = "Extra 5% off + Free Express Shipping guarantee",
                minSpend = 799.0,
                maxDiscount = 200.0
            )
        )

        fun getSeedOrders(): List<Order> {
            val sampleProduct = getSeedProducts()[2] // Voyager Minimalist Backpack
            val cartItem = CartItem(
                product = sampleProduct,
                selectedVariant = sampleProduct.variants.firstOrNull(),
                quantity = 1
            )
            return listOf(
                Order(
                    orderId = "ORM-88219",
                    items = listOf(cartItem),
                    subtotal = 2799.0,
                    discount = 300.0,
                    deliveryFee = 0.0,
                    total = 2499.0,
                    shippingAddress = "Flat 402, Lotus Towers, Andheri West, Mumbai 400053",
                    paymentMethod = "UPI (Google Pay)",
                    orderDate = "28 Oct 2026, 02:45 PM",
                    currentStatus = OrderStatus.IN_TRANSIT,
                    trackingSteps = listOf(
                        TrackingStep(OrderStatus.CONFIRMED, "28 Oct, 02:45 PM", "Order Received", true),
                        TrackingStep(OrderStatus.PACKED, "28 Oct, 06:10 PM", "Bhiwandi Fulfillment Center", true),
                        TrackingStep(OrderStatus.IN_TRANSIT, "29 Oct, 09:30 AM", "On route with Bluedart Air", true),
                        TrackingStep(OrderStatus.DELIVERED, "Expected Tomorrow, 04:00 PM", "Customer Address", false)
                    )
                )
            )
        }

        fun getSeedSubcategories(): List<Subcategory> = listOf(
            // 1. Automotives
            Subcategory(
                id = "sub_auto_care",
                category = ProductCategory.AUTOMOTIVES,
                name = "Car Care & Detailing",
                imageUrl = "https://images.unsplash.com/photo-1520340356584-f9917d1eea6f?w=500&auto=format&fit=crop&q=80",
                itemCount = 28,
                tagline = "Detailing shampoos, ceramic coatings & microfiber",
                discountTag = "Up to 30% OFF",
                isPopular = true
            ),
            Subcategory(
                id = "sub_auto_interior",
                category = ProductCategory.AUTOMOTIVES,
                name = "Interior Accessories",
                imageUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=500&auto=format&fit=crop&q=80",
                itemCount = 22,
                tagline = "Ergonomic cushions, ambient lighting & floor mats",
                discountTag = "Trending",
                isPopular = true
            ),
            Subcategory(
                id = "sub_auto_electronics",
                category = ProductCategory.AUTOMOTIVES,
                name = "Dash Cams & Audio",
                imageUrl = "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=500&auto=format&fit=crop&q=80",
                itemCount = 18,
                tagline = "4K ultra-wide dash cams & wireless audio adapters",
                discountTag = "New Launch",
                isPopular = false
            ),
            Subcategory(
                id = "sub_auto_tools",
                category = ProductCategory.AUTOMOTIVES,
                name = "Emergency & Tools",
                imageUrl = "https://images.unsplash.com/photo-1486006920555-c77dce18193b?w=500&auto=format&fit=crop&q=80",
                itemCount = 14,
                tagline = "Compact digital tire inflators & portable jump starters",
                discountTag = "Best Value",
                isPopular = false
            ),

            // 2. Baby & Toys
            Subcategory(
                id = "sub_baby_gear",
                category = ProductCategory.BABY_TOYS,
                name = "Strollers & Carriers",
                imageUrl = "https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=500&auto=format&fit=crop&q=80",
                itemCount = 16,
                tagline = "Ultralight one-hand fold cabin travel strollers",
                discountTag = "Up to 25% OFF",
                isPopular = true
            ),
            Subcategory(
                id = "sub_baby_nursery",
                category = ProductCategory.BABY_TOYS,
                name = "Nursery & Bedding",
                imageUrl = "https://images.unsplash.com/photo-1555252333-9f8e92e65df9?w=500&auto=format&fit=crop&q=80",
                itemCount = 20,
                tagline = "100% organic cotton crib bedding & sleep sacks",
                discountTag = "Gentle Choice",
                isPopular = true
            ),
            Subcategory(
                id = "sub_baby_care",
                category = ProductCategory.BABY_TOYS,
                name = "Diapering & Skin",
                imageUrl = "https://images.unsplash.com/photo-1544816155-12df9643f363?w=500&auto=format&fit=crop&q=80",
                itemCount = 24,
                tagline = "Hypoallergenic bamboo wipes & organic nourishing balms",
                discountTag = "Pure Organic",
                isPopular = false
            ),
            Subcategory(
                id = "sub_baby_play",
                category = ProductCategory.BABY_TOYS,
                name = "Sensory & Teething",
                imageUrl = "https://images.unsplash.com/photo-1596461404969-9ae70f2830c1?w=500&auto=format&fit=crop&q=80",
                itemCount = 18,
                tagline = "BPA-free silicone chew teethers & wooden rattles",
                discountTag = "Top Rated",
                isPopular = false
            ),

            // 3. Backpack & Luggage
            Subcategory(
                id = "sub_luggage_carryon",
                category = ProductCategory.BACKPACK_LUGGAGE,
                name = "Cabin Luggage",
                imageUrl = "https://images.unsplash.com/photo-1565026057447-bc90a3dceb87?w=500&auto=format&fit=crop&q=80",
                itemCount = 24,
                tagline = "Polycarbonate impact-resistant hardshell 360 spinners",
                discountTag = "Flat 40% OFF",
                isPopular = true
            ),
            Subcategory(
                id = "sub_luggage_backpack",
                category = ProductCategory.BACKPACK_LUGGAGE,
                name = "Laptop Backpacks",
                imageUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500&auto=format&fit=crop&q=80",
                itemCount = 36,
                tagline = "Weatherproof Cordura minimalist tech backpacks",
                discountTag = "Bestseller",
                isPopular = true
            ),
            Subcategory(
                id = "sub_luggage_duffel",
                category = ProductCategory.BACKPACK_LUGGAGE,
                name = "Duffels & Weekenders",
                imageUrl = "https://images.unsplash.com/photo-1510519138161-58474ebf8804?w=500&auto=format&fit=crop&q=80",
                itemCount = 18,
                tagline = "Heavy-duty waxed canvas gym & weekend travel bags",
                discountTag = "Trending",
                isPopular = false
            ),
            Subcategory(
                id = "sub_luggage_slings",
                category = ProductCategory.BACKPACK_LUGGAGE,
                name = "Crossbody Slings",
                imageUrl = "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=500&auto=format&fit=crop&q=80",
                itemCount = 16,
                tagline = "Anti-theft waterproof compact everyday carry pouches",
                discountTag = "Special Edition",
                isPopular = false
            ),

            // 4. Bathroom Linen
            Subcategory(
                id = "sub_bath_towels",
                category = ProductCategory.BATHROOM_LINEN,
                name = "Bath Towels & Sets",
                imageUrl = "https://images.unsplash.com/photo-1616627547584-bf28cee262db?w=500&auto=format&fit=crop&q=80",
                itemCount = 26,
                tagline = "700 GSM Aegean combed Turkish zero-twist cotton",
                discountTag = "Up to 35% OFF",
                isPopular = true
            ),
            Subcategory(
                id = "sub_bath_robes",
                category = ProductCategory.BATHROOM_LINEN,
                name = "Bathrobes & Wraps",
                imageUrl = "https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?w=500&auto=format&fit=crop&q=80",
                itemCount = 14,
                tagline = "Waffle-knit breathable luxury hotel kimono robes",
                discountTag = "Luxury Feel",
                isPopular = true
            ),
            Subcategory(
                id = "sub_bath_mats",
                category = ProductCategory.BATHROOM_LINEN,
                name = "Memory Foam Mats",
                imageUrl = "https://images.unsplash.com/photo-1507652313519-d4e9174996dd?w=500&auto=format&fit=crop&q=80",
                itemCount = 20,
                tagline = "Ultra-absorbent high-density non-slip bath rugs",
                discountTag = "Top Rated",
                isPopular = false
            ),

            // 5. Bed & Linen
            Subcategory(
                id = "sub_bed_sheets",
                category = ProductCategory.BED_LINEN,
                name = "Bedsheet Sets",
                imageUrl = "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=500&auto=format&fit=crop&q=80",
                itemCount = 30,
                tagline = "400 TC pure Egyptian sateen king & queen sheet sets",
                discountTag = "Flat 30% OFF",
                isPopular = true
            ),
            Subcategory(
                id = "sub_bed_duvets",
                category = ProductCategory.BED_LINEN,
                name = "Duvets & Quilts",
                imageUrl = "https://images.unsplash.com/photo-1615874959474-d609969a20ed?w=500&auto=format&fit=crop&q=80",
                itemCount = 18,
                tagline = "All-season microfibre down-alternative cloud comforters",
                discountTag = "Cozy Pick",
                isPopular = true
            ),
            Subcategory(
                id = "sub_bed_pillows",
                category = ProductCategory.BED_LINEN,
                name = "Pillows & Protectors",
                imageUrl = "https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?w=500&auto=format&fit=crop&q=80",
                itemCount = 22,
                tagline = "Ergonomic cervical contour memory foam sleep pillows",
                discountTag = "Bestseller",
                isPopular = false
            ),

            // 6. Cameras & Photography
            Subcategory(
                id = "sub_cam_mirrorless",
                category = ProductCategory.CAMERAS_PHOTOGRAPHY,
                name = "Mirrorless Cameras",
                imageUrl = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=500&auto=format&fit=crop&q=80",
                itemCount = 14,
                tagline = "4K 60FPS full-frame & APS-C creator hybrid bodies",
                discountTag = "Pro Choice",
                isPopular = true
            ),
            Subcategory(
                id = "sub_cam_lenses",
                category = ProductCategory.CAMERAS_PHOTOGRAPHY,
                name = "Prime & Zoom Lenses",
                imageUrl = "https://images.unsplash.com/photo-1617005082133-548c4dd27f35?w=500&auto=format&fit=crop&q=80",
                itemCount = 18,
                tagline = "f/1.4 low-light bokeh portrait & ultra-wide landscape glass",
                discountTag = "Special Edition",
                isPopular = true
            ),
            Subcategory(
                id = "sub_cam_accessories",
                category = ProductCategory.CAMERAS_PHOTOGRAPHY,
                name = "Tripods & Gimbals",
                imageUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
                itemCount = 16,
                tagline = "Carbon fibre 3-axis motorized video stabilizers & fluid heads",
                discountTag = "Trending",
                isPopular = false
            ),

            // 7. Computers & Accessories
            Subcategory(
                id = "sub_comp_keyboards",
                category = ProductCategory.COMPUTERS_ACCESSORIES,
                name = "Mechanical Keyboards",
                imageUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500&auto=format&fit=crop&q=80",
                itemCount = 30,
                tagline = "Gasket-mount wireless hot-swap RGB mechanical boards",
                discountTag = "Up to 35% OFF",
                isPopular = true
            ),
            Subcategory(
                id = "sub_comp_hubs",
                category = ProductCategory.COMPUTERS_ACCESSORIES,
                name = "Type-C Hubs & Docks",
                imageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=500&auto=format&fit=crop&q=80",
                itemCount = 24,
                tagline = "10-in-1 dual 4K HDMI 100W PD aluminium workstation docks",
                discountTag = "Best Value",
                isPopular = true
            ),
            Subcategory(
                id = "sub_comp_monitors",
                category = ProductCategory.COMPUTERS_ACCESSORIES,
                name = "Desk Mounts & Stands",
                imageUrl = "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500&auto=format&fit=crop&q=80",
                itemCount = 20,
                tagline = "Heavy-duty gas spring dual monitor ergonomic arms",
                discountTag = "Trending",
                isPopular = false
            ),
            Subcategory(
                id = "sub_comp_mouse",
                category = ProductCategory.COMPUTERS_ACCESSORIES,
                name = "Wireless Mice & Mats",
                imageUrl = "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=500&auto=format&fit=crop&q=80",
                itemCount = 26,
                tagline = "Precision optical multi-device wireless mice & desk pads",
                discountTag = "Top Rated",
                isPopular = false
            ),

            // 8. Gaming
            Subcategory(
                id = "sub_game_controllers",
                category = ProductCategory.GAMING,
                name = "Controllers & Gamepads",
                imageUrl = "https://images.unsplash.com/photo-1600080972464-8e5f35f63d08?w=500&auto=format&fit=crop&q=80",
                itemCount = 22,
                tagline = "Hall-effect anti-drift wireless triggers & pro paddles",
                discountTag = "Trending Gear",
                isPopular = true
            ),
            Subcategory(
                id = "sub_game_headsets",
                category = ProductCategory.GAMING,
                name = "Spatial Headsets",
                imageUrl = "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=500&auto=format&fit=crop&q=80",
                itemCount = 20,
                tagline = "7.1 surround sound ultra-low latency wireless audio",
                discountTag = "Up to 40% OFF",
                isPopular = true
            ),
            Subcategory(
                id = "sub_game_accessories",
                category = ProductCategory.GAMING,
                name = "Docks & Grips",
                imageUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=500&auto=format&fit=crop&q=80",
                itemCount = 18,
                tagline = "Dual fast charging docks & non-slip silicone thumb grips",
                discountTag = "Hot Deal",
                isPopular = false
            ),

            // 9. Home & Kitchen Appliances
            Subcategory(
                id = "sub_appl_airfryer",
                category = ProductCategory.HOME_KITCHEN_APPLIANCES,
                name = "Air Fryers & Ovens",
                imageUrl = "https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?w=500&auto=format&fit=crop&q=80",
                itemCount = 18,
                tagline = "Digital rapid 360 air-flow oil-free convection fryers",
                discountTag = "Flat 30% OFF",
                isPopular = true
            ),
            Subcategory(
                id = "sub_appl_blenders",
                category = ProductCategory.HOME_KITCHEN_APPLIANCES,
                name = "Nutri-Blenders & Juicers",
                imageUrl = "https://images.unsplash.com/photo-1570222094114-d054a817e56b?w=500&auto=format&fit=crop&q=80",
                itemCount = 22,
                tagline = "High-torque 1200W nutrient extraction smoothie blenders",
                discountTag = "Bestseller",
                isPopular = true
            ),
            Subcategory(
                id = "sub_appl_coffee",
                category = ProductCategory.HOME_KITCHEN_APPLIANCES,
                name = "Espresso & Coffee",
                imageUrl = "https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=500&auto=format&fit=crop&q=80",
                itemCount = 16,
                tagline = "15-bar Italian thermo-block pump espresso machines",
                discountTag = "Premium",
                isPopular = false
            ),
            Subcategory(
                id = "sub_appl_kettles",
                category = ProductCategory.HOME_KITCHEN_APPLIANCES,
                name = "Electric Kettles",
                imageUrl = "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=500&auto=format&fit=crop&q=80",
                itemCount = 16,
                tagline = "Precision gooseneck variable temperature digital kettles",
                discountTag = "Chef Grade",
                isPopular = false
            ),

            // 10. Home Decor
            Subcategory(
                id = "sub_decor_lighting",
                category = ProductCategory.HOME_DECOR,
                name = "Ambient Lamps",
                imageUrl = "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=500&auto=format&fit=crop&q=80",
                itemCount = 24,
                tagline = "Nordic solid timber warm minimalist bedside lamps",
                discountTag = "Up to 25% OFF",
                isPopular = true
            ),
            Subcategory(
                id = "sub_decor_vases",
                category = ProductCategory.HOME_DECOR,
                name = "Ceramic Vases",
                imageUrl = "https://images.unsplash.com/photo-1583847268964-b28dc8f51f92?w=500&auto=format&fit=crop&q=80",
                itemCount = 30,
                tagline = "Handcrafted textured matte stoneware sculptural vases",
                discountTag = "Artisan Pick",
                isPopular = true
            ),
            Subcategory(
                id = "sub_decor_clocks",
                category = ProductCategory.HOME_DECOR,
                name = "Wall Art & Clocks",
                imageUrl = "https://images.unsplash.com/photo-1513519245088-0e12902e5a38?w=500&auto=format&fit=crop&q=80",
                itemCount = 26,
                tagline = "Modern geometric walnut wood silent sweep wall clocks",
                discountTag = "Trending",
                isPopular = false
            ),

            // 11. Kitchenware
            Subcategory(
                id = "sub_kitchen_cookware",
                category = ProductCategory.KITCHENWARE,
                name = "Cast Iron & Tri-Ply",
                imageUrl = "https://images.unsplash.com/photo-1584990347449-322199f7d08d?w=500&auto=format&fit=crop&q=80",
                itemCount = 24,
                tagline = "Pre-seasoned heavy iron skillets & stainless tri-ply pans",
                discountTag = "Chef Grade",
                isPopular = true
            ),
            Subcategory(
                id = "sub_kitchen_knives",
                category = ProductCategory.KITCHENWARE,
                name = "Chef Knives & Boards",
                imageUrl = "https://images.unsplash.com/photo-1593618998160-e34014e67546?w=500&auto=format&fit=crop&q=80",
                itemCount = 20,
                tagline = "Japanese 67-layer Damascus steel santoku & chef blades",
                discountTag = "Pro Series",
                isPopular = true
            ),
            Subcategory(
                id = "sub_kitchen_storage",
                category = ProductCategory.KITCHENWARE,
                name = "Glass Storage Jars",
                imageUrl = "https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?w=500&auto=format&fit=crop&q=80",
                itemCount = 28,
                tagline = "Airtight borosilicate glass canisters with acacia lids",
                discountTag = "Bestseller",
                isPopular = false
            ),

            // 12. Mobile & Accessories
            Subcategory(
                id = "sub_mob_chargers",
                category = ProductCategory.MOBILE_ACCESSORIES,
                name = "GaN Chargers & Docks",
                imageUrl = "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=500&auto=format&fit=crop&q=80",
                itemCount = 32,
                tagline = "65W & 100W 3-port ultra-compact GaN fast power bricks",
                discountTag = "Up to 45% OFF",
                isPopular = true
            ),
            Subcategory(
                id = "sub_mob_cases",
                category = ProductCategory.MOBILE_ACCESSORIES,
                name = "Kevlar & Armor Cases",
                imageUrl = "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=500&auto=format&fit=crop&q=80",
                itemCount = 38,
                tagline = "Aramid 600D aerospace fibre MagSafe protective cases",
                discountTag = "Trending",
                isPopular = true
            ),
            Subcategory(
                id = "sub_mob_cables",
                category = ProductCategory.MOBILE_ACCESSORIES,
                name = "Braided Fast Cables",
                imageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=500&auto=format&fit=crop&q=80",
                itemCount = 30,
                tagline = "240W braided silicone nylon tangle-free Type-C cords",
                discountTag = "Flash Deal",
                isPopular = false
            ),
            Subcategory(
                id = "sub_mob_mounts",
                category = ProductCategory.MOBILE_ACCESSORIES,
                name = "Car & Desk Mounts",
                imageUrl = "https://images.unsplash.com/photo-1586953208448-b95a79798f07?w=500&auto=format&fit=crop&q=80",
                itemCount = 22,
                tagline = "360 magnetic wireless charging phone dash mounts",
                discountTag = "Best Value",
                isPopular = false
            ),

            // 13. Musical Instruments
            Subcategory(
                id = "sub_music_guitars",
                category = ProductCategory.MUSICAL_INSTRUMENTS,
                name = "Acoustic Guitars",
                imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500&auto=format&fit=crop&q=80",
                itemCount = 16,
                tagline = "Solid Sitka spruce dreadnought natural finish acoustic guitars",
                discountTag = "Flat 20% OFF",
                isPopular = true
            ),
            Subcategory(
                id = "sub_music_keyboards",
                category = ProductCategory.MUSICAL_INSTRUMENTS,
                name = "Keyboards & MIDI",
                imageUrl = "https://images.unsplash.com/photo-1520523839898-5071210a563b?w=500&auto=format&fit=crop&q=80",
                itemCount = 14,
                tagline = "49-key velocity-sensitive semi-weighted synth controllers",
                discountTag = "Studio Ready",
                isPopular = true
            ),
            Subcategory(
                id = "sub_music_accessories",
                category = ProductCategory.MUSICAL_INSTRUMENTS,
                name = "Pedals & Audio Gear",
                imageUrl = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?w=500&auto=format&fit=crop&q=80",
                itemCount = 20,
                tagline = "True bypass analog chorus pedals & clip-on digital tuners",
                discountTag = "Curated",
                isPopular = false
            ),

            // 14. Personal Care Appliances
            Subcategory(
                id = "sub_care_trimmers",
                category = ProductCategory.PERSONAL_CARE_APPLIANCES,
                name = "Beard Trimmers & Shavers",
                imageUrl = "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=500&auto=format&fit=crop&q=80",
                itemCount = 26,
                tagline = "Titanium ceramic blade waterproof cordless groomers",
                discountTag = "Up to 40% OFF",
                isPopular = true
            ),
            Subcategory(
                id = "sub_care_dryers",
                category = ProductCategory.PERSONAL_CARE_APPLIANCES,
                name = "Ionic Hair Dryers",
                imageUrl = "https://images.unsplash.com/photo-1527799820374-dcf8d9d4a388?w=500&auto=format&fit=crop&q=80",
                itemCount = 18,
                tagline = "110,000 RPM high-speed brushless negative ion hair dryers",
                discountTag = "Salon Grade",
                isPopular = true
            ),
            Subcategory(
                id = "sub_care_dental",
                category = ProductCategory.PERSONAL_CARE_APPLIANCES,
                name = "Sonic Toothbrushes",
                imageUrl = "https://images.unsplash.com/photo-1559599101-f09722fb4948?w=500&auto=format&fit=crop&q=80",
                itemCount = 20,
                tagline = "40,000 VPM ultrasonic smart electric brushes with travel case",
                discountTag = "Bestseller",
                isPopular = false
            ),

            // 15. Toys & Games
            Subcategory(
                id = "sub_toys_boardgames",
                category = ProductCategory.TOYS_GAMES,
                name = "Board Games",
                imageUrl = "https://images.unsplash.com/photo-1610890716171-6b1bb98ffd09?w=500&auto=format&fit=crop&q=80",
                itemCount = 26,
                tagline = "Strategic tabletop multiplayer games & cooperative quests",
                discountTag = "Family Fun",
                isPopular = true
            ),
            Subcategory(
                id = "sub_toys_building",
                category = ProductCategory.TOYS_GAMES,
                name = "Building Blocks",
                imageUrl = "https://images.unsplash.com/photo-1566576912321-d58ddd7a6088?w=500&auto=format&fit=crop&q=80",
                itemCount = 24,
                tagline = "Precision architectural modular brick construction sets",
                discountTag = "Popular",
                isPopular = true
            ),
            Subcategory(
                id = "sub_toys_figures",
                category = ProductCategory.TOYS_GAMES,
                name = "Action Figures",
                imageUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=500&auto=format&fit=crop&q=80",
                itemCount = 22,
                tagline = "Articulated collectible anime & superhero display figurines",
                discountTag = "Trending",
                isPopular = false
            )
        )

                fun getSeedProducts(): List<Product> = listOf(
            // 1. Automotives
            Product(
                id = "p-auto-101",
                title = "Orami Turbo Jet High-Pressure Car Foam Cannon",
                brand = "Orami AutoLab",
                category = ProductCategory.AUTOMOTIVES,
                subcategoryId = "sub_auto_care",
                price = 1499.0,
                originalPrice = 2499.0,
                rating = 4.8f,
                reviewCount = 142,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1520340356584-f9917d1eea6f?w=800&auto=format&fit=crop&q=80",
                    "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&auto=format&fit=crop&q=80"
                ),
                description = "Thick, snow-like cleaning foam cannon with heavy-duty brass connector and 1L translucent bottle for scratchless car washing.",
                specs = mapOf(
                    "Capacity" to "1.0 Litre",
                    "Connector" to "1/4 Inch Quick Connect",
                    "Material" to "Solid Brass & HDPE",
                    "Pressure Range" to "1000 - 3000 PSI"
                ),
                variants = listOf(
                    ProductVariant("v1", "Snow Foam Standard"),
                    ProductVariant("v2", "Pro Quick Connect Kit")
                ),
                isFlashDeal = true,
                badge = "Bestseller"
            ),
            Product(
                id = "p-auto-102",
                title = "Ultra-Plush Dual-Sided Microfiber Detailing Towels (Pack of 3)",
                brand = "Orami AutoLab",
                category = ProductCategory.AUTOMOTIVES,
                subcategoryId = "sub_auto_care",
                price = 699.0,
                originalPrice = 1199.0,
                rating = 4.7f,
                reviewCount = 89,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&auto=format&fit=crop&q=80"
                ),
                description = "800 GSM lint-free edgeless microfiber drying towels designed for swirl-free buffing, waxing, and water absorption.",
                specs = mapOf(
                    "Density" to "800 GSM",
                    "Dimensions" to "40 x 40 cm",
                    "Pack Size" to "3 Towels"
                ),
                variants = listOf(
                    ProductVariant("v1", "Grey & Yellow Edge")
                ),
                badge = "Top Value"
            ),

            // 2. Baby & Toys
            Product(
                id = "p-baby-101",
                title = "CloudGlide Ultralight One-Hand Compact Cabin Stroller",
                brand = "Orami Little",
                category = ProductCategory.BABY_TOYS,
                subcategoryId = "sub_baby_gear",
                price = 8499.0,
                originalPrice = 12999.0,
                rating = 4.9f,
                reviewCount = 76,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=800&auto=format&fit=crop&q=80",
                    "https://images.unsplash.com/photo-1555252333-9f8e92e65df9?w=800&auto=format&fit=crop&q=80"
                ),
                description = "Aerospace grade aluminium lightweight frame weighing only 5.8kg with one-hand automatic gravity fold, suitable from newborn to 22kg.",
                specs = mapOf(
                    "Weight" to "5.8 kg",
                    "Folded Dimensions" to "52 x 44 x 18 cm",
                    "Age Range" to "0 - 4 Years",
                    "Max Load" to "22 kg"
                ),
                variants = listOf(
                    ProductVariant("v1", "Slate Grey"),
                    ProductVariant("v2", "Sand Beige")
                ),
                isFlashDeal = true,
                badge = "Editor Choice"
            ),
            Product(
                id = "p-baby-102",
                title = "100% Organic Combed Cotton Crib Fitted Sheet Set",
                brand = "Orami Little",
                category = ProductCategory.BABY_TOYS,
                subcategoryId = "sub_baby_nursery",
                price = 1299.0,
                originalPrice = 1899.0,
                rating = 4.8f,
                reviewCount = 64,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1555252333-9f8e92e65df9?w=800&auto=format&fit=crop&q=80"
                ),
                description = "Ultra-soft GOTS certified hypoallergenic organic jersey cotton fitted sheets tailored with deep elastic hem for crib mattresses.",
                specs = mapOf(
                    "Material" to "100% GOTS Organic Cotton",
                    "Fit" to "Standard Crib (132 x 71 cm)",
                    "Safety" to "OEKO-TEX Standard 100"
                ),
                variants = listOf(
                    ProductVariant("v1", "Pastel Cloud White"),
                    ProductVariant("v2", "Sage Botanical")
                ),
                badge = "Gentle Choice"
            ),

            // 3. Backpack & Luggage
            Product(
                id = "p-luggage-101",
                title = "Voyager Minimalist Weatherproof Commuter Backpack (24L)",
                brand = "Orami Atelier",
                category = ProductCategory.BACKPACK_LUGGAGE,
                subcategoryId = "sub_luggage_backpack",
                price = 2799.0,
                originalPrice = 4499.0,
                rating = 4.9f,
                reviewCount = 230,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&auto=format&fit=crop&q=80",
                    "https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?w=800&auto=format&fit=crop&q=80"
                ),
                description = "Architectural silhouette constructed with 840D ballistic recycled Cordura. Suspended 16-inch fleece laptop sleeve, hidden magnetic passport pocket, and YKK AquaGuard weather zippers.",
                specs = mapOf(
                    "Capacity" to "24 Litres",
                    "Laptop Size" to "Up to 16-inch MacBook Pro",
                    "Material" to "840D Recycled Cordura",
                    "Weight" to "850g"
                ),
                variants = listOf(
                    ProductVariant("v1", "Stealth Black"),
                    ProductVariant("v2", "Deep Navy"),
                    ProductVariant("v3", "Desert Moss")
                ),
                isFlashDeal = true,
                badge = "Bestseller"
            ),
            Product(
                id = "p-luggage-102",
                title = "AeroPoly 20-Inch Hardshell Cabin Spinner Suitcase",
                brand = "Orami Atelier",
                category = ProductCategory.BACKPACK_LUGGAGE,
                subcategoryId = "sub_luggage_carryon",
                price = 4999.0,
                originalPrice = 7999.0,
                rating = 4.8f,
                reviewCount = 115,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1565026057447-bc90a3dceb87?w=800&auto=format&fit=crop&q=80"
                ),
                description = "100% German Bayer polycarbonate shell with recessed TSA lock and silent Hinomoto 360 Japanese double-wheel system.",
                specs = mapOf(
                    "Size" to "20-inch Cabin Carry-on",
                    "Lock" to "TSA Certified Flush Lock",
                    "Volume" to "38 Litres",
                    "Wheels" to "Dual 360 Hinomoto Ball Bearings"
                ),
                variants = listOf(
                    ProductVariant("v1", "Matte Charcoal"),
                    ProductVariant("v2", "Glacier White")
                ),
                badge = "40% OFF"
            ),

            // 4. Bathroom Linen
            Product(
                id = "p-bath-101",
                title = "Royal Sovereign 700 GSM Aegean Bath Towel Pair",
                brand = "Orami Living",
                category = ProductCategory.BATHROOM_LINEN,
                subcategoryId = "sub_bath_towels",
                price = 1899.0,
                originalPrice = 2899.0,
                rating = 4.9f,
                reviewCount = 98,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1616627547584-bf28cee262db?w=800&auto=format&fit=crop&q=80"
                ),
                description = "Thick 700 GSM zero-twist long-staple Turkish Aegean cotton offering velvety softness and high water absorption.",
                specs = mapOf(
                    "GSM" to "700 GSM Heavyweight",
                    "Dimensions" to "75 x 150 cm",
                    "Material" to "100% Combed Turkish Cotton"
                ),
                variants = listOf(
                    ProductVariant("v1", "Natural Ivory"),
                    ProductVariant("v2", "Ocean Slate")
                ),
                isFlashDeal = true,
                badge = "Luxury"
            ),

            // 5. Bed & Linen
            Product(
                id = "p-bed-101",
                title = "Aura 400 TC Egyptian Sateen King Bedsheet Set",
                brand = "Orami Living",
                category = ProductCategory.BED_LINEN,
                subcategoryId = "sub_bed_sheets",
                price = 2499.0,
                originalPrice = 3799.0,
                rating = 4.8f,
                reviewCount = 156,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&auto=format&fit=crop&q=80"
                ),
                description = "Silk-smooth lustrous sateen weave crafted from extra-long staple Egyptian combed cotton. Includes 1 king fitted sheet and 2 pillowcases.",
                specs = mapOf(
                    "Thread Count" to "400 TC Single-Ply",
                    "Sheet Size" to "King (275 x 275 cm)",
                    "Fit" to "Up to 16-inch mattress depth"
                ),
                variants = listOf(
                    ProductVariant("v1", "Snow White"),
                    ProductVariant("v2", "Warm Linen Beige")
                ),
                badge = "Bestseller"
            ),

            // 6. Cameras & Photography
            Product(
                id = "p-cam-101",
                title = "Lumina X1 4K 60FPS Compact Mirrorless Vlog Camera",
                brand = "Lumina Optics",
                category = ProductCategory.CAMERAS_PHOTOGRAPHY,
                subcategoryId = "sub_cam_mirrorless",
                price = 42999.0,
                originalPrice = 52999.0,
                rating = 4.9f,
                reviewCount = 47,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800&auto=format&fit=crop&q=80"
                ),
                description = "24.2 MP APS-C sensor with advanced Real-time Eye AF, 4K HDR recording with zero crop, and flip-out vari-angle selfie screen.",
                specs = mapOf(
                    "Sensor" to "24.2 MP APS-C CMOS",
                    "Video" to "4K 60p 10-bit 4:2:2",
                    "Weight" to "385g Body"
                ),
                variants = listOf(
                    ProductVariant("v1", "Body Only"),
                    ProductVariant("v2", "With 16-50mm Lens Kit")
                ),
                badge = "Pro Series"
            ),

            // 7. Computers & Accessories
            Product(
                id = "p-comp-101",
                title = "Kinesis Nova Pro Wireless Hot-Swap Mechanical Keyboard",
                brand = "Orami Tech",
                category = ProductCategory.COMPUTERS_ACCESSORIES,
                subcategoryId = "sub_comp_keyboards",
                price = 4599.0,
                originalPrice = 6999.0,
                rating = 4.9f,
                reviewCount = 210,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&auto=format&fit=crop&q=80"
                ),
                description = "75% compact CNC aluminium chassis with gasket mount, lubed linear switches, south-facing RGB, and tri-mode BT 5.2/2.4GHz/USB-C connection.",
                specs = mapOf(
                    "Layout" to "75% 82-Key ANSI",
                    "Switches" to "Factory-Lubed Gateron Yellow Pro",
                    "Battery" to "4000 mAh (Up to 200 hours)"
                ),
                variants = listOf(
                    ProductVariant("v1", "Linear Yellow Switch"),
                    ProductVariant("v2", "Tactile Brown Switch")
                ),
                isFlashDeal = true,
                badge = "Hot Deal"
            ),
            Product(
                id = "p-comp-102",
                title = "OmniDock 10-in-1 Dual 4K HDMI 100W PD Workstation Hub",
                brand = "Orami Tech",
                category = ProductCategory.COMPUTERS_ACCESSORIES,
                subcategoryId = "sub_comp_hubs",
                price = 2899.0,
                originalPrice = 4299.0,
                rating = 4.7f,
                reviewCount = 135,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=800&auto=format&fit=crop&q=80"
                ),
                description = "Dual 4K@60Hz HDMI, 100W USB-C Power Delivery pass-through, Gigabit Ethernet, SD/TF card slots, and 3x USB 3.2 10Gbps ports.",
                specs = mapOf(
                    "Ports" to "10 Ports",
                    "Power Delivery" to "100W PD 3.0",
                    "Chassis" to "Anodized Space Gray Aluminum"
                ),
                variants = listOf(
                    ProductVariant("v1", "Space Grey")
                ),
                badge = "Best Seller"
            ),

            // 8. Gaming
            Product(
                id = "p-game-101",
                title = "Vortex Pro Wireless Controller with Hall Effect Triggers",
                brand = "Orami Gaming",
                category = ProductCategory.GAMING,
                subcategoryId = "sub_game_controllers",
                price = 3299.0,
                originalPrice = 4999.0,
                rating = 4.8f,
                reviewCount = 188,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1600080972464-8e5f35f63d08?w=800&auto=format&fit=crop&q=80"
                ),
                description = "Zero-stick drift electromagnetic Hall Effect analog sticks and triggers, textured grip back, remappable back paddles, and multi-platform compatibility.",
                specs = mapOf(
                    "Compatibility" to "PC, Android, Switch, iOS",
                    "Sticks" to "Hall Effect Magnetic Anti-Drift",
                    "Battery" to "1000 mAh (18 Hours Playtime)"
                ),
                variants = listOf(
                    ProductVariant("v1", "Matte Stealth Black"),
                    ProductVariant("v2", "Cyber Arctic White")
                ),
                isFlashDeal = true,
                badge = "Trending"
            ),

            // 9. Home & Kitchen Appliances
            Product(
                id = "p-appl-101",
                title = "AeroCrisp 5.5L 360-Vortex Touchscreen Digital Air Fryer",
                brand = "Orami Home",
                category = ProductCategory.HOME_KITCHEN_APPLIANCES,
                subcategoryId = "sub_appl_airfryer",
                price = 4899.0,
                originalPrice = 7499.0,
                rating = 4.8f,
                reviewCount = 175,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?w=800&auto=format&fit=crop&q=80"
                ),
                description = "High-velocity 360 rapid air circulation cuts oil usage by up to 85%. Features 8 one-touch cooking presets and dishwasher-safe ceramic non-stick basket.",
                specs = mapOf(
                    "Capacity" to "5.5 Litres",
                    "Power" to "1700 Watts",
                    "Temperature" to "80°C - 200°C",
                    "Presets" to "8 Smart Cook Modes"
                ),
                variants = listOf(
                    ProductVariant("v1", "Matte Obsidian Black")
                ),
                badge = "Bestseller"
            ),

            // 10. Home Decor
            Product(
                id = "p-decor-101",
                title = "Nordic Minimalist Oak Timber Ambient Glow Table Lamp",
                brand = "Orami Atelier",
                category = ProductCategory.HOME_DECOR,
                subcategoryId = "sub_decor_lighting",
                price = 2199.0,
                originalPrice = 3299.0,
                rating = 4.8f,
                reviewCount = 112,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=800&auto=format&fit=crop&q=80"
                ),
                description = "Solid FSC-certified oak wood base with opal frosted glass sphere, offering 3-level warm ambient dimming touch control.",
                specs = mapOf(
                    "Material" to "FSC Natural Oak & Frosted Opal Glass",
                    "Bulb" to "G9 Warm 2700K Included",
                    "Switch" to "Stepless Touch Dimmer"
                ),
                variants = listOf(
                    ProductVariant("v1", "Natural White Oak"),
                    ProductVariant("v2", "Walnut Dark Wood")
                ),
                badge = "Artisan"
            ),

            // 11. Kitchenware
            Product(
                id = "p-kitch-101",
                title = "IronCraft 10-Inch Pre-Seasoned Heavy Duty Cast Iron Skillet",
                brand = "Orami Chef",
                category = ProductCategory.KITCHENWARE,
                subcategoryId = "sub_kitchen_cookware",
                price = 1599.0,
                originalPrice = 2499.0,
                rating = 4.9f,
                reviewCount = 205,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1584990347449-322199f7d08d?w=800&auto=format&fit=crop&q=80"
                ),
                description = "Triple pre-seasoned with 100% natural flaxseed oil. Superior heat retention with dual pour spouts and ergonomic silicone handle cover.",
                specs = mapOf(
                    "Diameter" to "10.25 Inches (26 cm)",
                    "Material" to "Cast Iron Pre-Seasoned",
                    "Stovetop" to "Gas, Induction, Oven & Campfire Safe"
                ),
                variants = listOf(
                    ProductVariant("v1", "Skillet with Silicone Sleeve")
                ),
                badge = "Chef Grade"
            ),

            // 12. Mobile & Accessories
            Product(
                id = "p-mob-101",
                title = "VoltaGaN 65W 3-Port Ultra-Compact Fast GaN Wall Charger",
                brand = "Orami Tech",
                category = ProductCategory.MOBILE_ACCESSORIES,
                subcategoryId = "sub_mob_chargers",
                price = 1699.0,
                originalPrice = 2799.0,
                rating = 4.9f,
                reviewCount = 312,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=800&auto=format&fit=crop&q=80"
                ),
                description = "Gallium Nitride (GaN III) architecture delivers 65W rapid power across 2x USB-C and 1x USB-A ports. Compatible with MacBook, iPhone, and Android.",
                specs = mapOf(
                    "Output" to "65W Max USB-C PD 3.0",
                    "Ports" to "2x USB-C + 1x USB-A",
                    "Size" to "50% Smaller than Standard 65W Bricks"
                ),
                variants = listOf(
                    ProductVariant("v1", "Chalk White"),
                    ProductVariant("v2", "Midnight Black")
                ),
                isFlashDeal = true,
                badge = "Flash Deal"
            ),

            // 13. Musical Instruments
            Product(
                id = "p-music-101",
                title = "Harmony Horizon Solid Spruce Dreadnought Acoustic Guitar",
                brand = "Harmony Sounds",
                category = ProductCategory.MUSICAL_INSTRUMENTS,
                subcategoryId = "sub_music_guitars",
                price = 8999.0,
                originalPrice = 12999.0,
                rating = 4.8f,
                reviewCount = 68,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&auto=format&fit=crop&q=80"
                ),
                description = "Solid Sitka spruce top with mahogany back and sides. Warm balanced projection, bone nut and saddle, with padded gig bag included.",
                specs = mapOf(
                    "Top" to "Solid Sitka Spruce",
                    "Back & Sides" to "Mahogany",
                    "Scale Length" to "25.5 Inches",
                    "Strings" to "D'Addario EXP16 Coated"
                ),
                variants = listOf(
                    ProductVariant("v1", "Natural Satin Finish"),
                    ProductVariant("v2", "Sunburst Vintage")
                ),
                badge = "Studio Choice"
            ),

            // 14. Personal Care Appliances
            Product(
                id = "p-care-101",
                title = "TitanGroom Waterproof Precision Beard & Body Trimmer",
                brand = "Orami Care",
                category = ProductCategory.PERSONAL_CARE_APPLIANCES,
                subcategoryId = "sub_care_trimmers",
                price = 1799.0,
                originalPrice = 2999.0,
                rating = 4.8f,
                reviewCount = 240,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=800&auto=format&fit=crop&q=80"
                ),
                description = "Self-sharpening titanium coated ceramic blades with 40 length settings (0.5mm - 20mm), IPX7 100% waterproof design, and 120-min runtime.",
                specs = mapOf(
                    "Blades" to "Self-Sharpening Titanium Ceramic",
                    "Waterproof" to "IPX7 Fully Washable",
                    "Battery" to "Li-Ion 120 Mins Runtime (USB-C)"
                ),
                variants = listOf(
                    ProductVariant("v1", "Matte Gunmetal")
                ),
                isFlashDeal = true,
                badge = "Bestseller"
            ),

            // 15. Toys & Games
            Product(
                id = "p-toys-101",
                title = "TerraForge Medieval Realm Strategy Tabletop Board Game",
                brand = "Orami Play",
                category = ProductCategory.TOYS_GAMES,
                subcategoryId = "sub_toys_boardgames",
                price = 1999.0,
                originalPrice = 2899.0,
                rating = 4.9f,
                reviewCount = 94,
                imageUrls = listOf(
                    "https://images.unsplash.com/photo-1610890716171-6b1bb98ffd09?w=800&auto=format&fit=crop&q=80"
                ),
                description = "Immersive 2-4 player territory expansion and resource trading game featuring wooden worker tokens, modular hex tiles, and custom resin dice.",
                specs = mapOf(
                    "Players" to "2 - 4 Players",
                    "Play Time" to "60 - 90 Minutes",
                    "Recommended Age" to "10+ Years"
                ),
                variants = listOf(
                    ProductVariant("v1", "Base Game Edition")
                ),
                badge = "Family Fun"
            )
        )

        fun getSampleReviews(productId: String): List<ProductReview> = listOf(
            ProductReview(
                id = "r1",
                reviewer = "Aarav Sharma",
                rating = 5.0f,
                date = "24 Oct 2026",
                comment = "Outstanding build quality and finish. The packaging was immaculate and delivery was delivered a day earlier than expected. Truly matches Orami's promise!",
                isVerified = true
            ),
            ProductReview(
                id = "r2",
                reviewer = "Pooja Mehta",
                rating = 4.5f,
                date = "18 Oct 2026",
                comment = "Very sleek and minimalist aesthetic. Fits into my everyday routine seamlessly. Highly recommended for anyone who values clean product design.",
                isVerified = true
            ),
            ProductReview(
                id = "r3",
                reviewer = "Rohan Verma",
                rating = 5.0f,
                date = "10 Oct 2026",
                comment = "Got it during the flash sale with the ORAMI20 discount code! Great value for money. Will definitely order from here again.",
                isVerified = true
            )
        )
    }
}
