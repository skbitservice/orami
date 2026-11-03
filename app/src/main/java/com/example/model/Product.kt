package com.example.model

enum class ProductCategory(
    val displayName: String,
    val iconName: String,
    val imageUrl: String,
    val tagline: String,
    val itemCount: Int,
    val discountTag: String
) {
    AUTOMOTIVES(
        "Automotives",
        "DirectionsCar",
        "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=400&auto=format&fit=crop&q=80",
        "Car accessories, care & essentials",
        64,
        "Up to 30% OFF"
    ),
    BABY_TOYS(
        "Baby & Toys",
        "ChildFriendly",
        "https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=400&auto=format&fit=crop&q=80",
        "Care, comfort & early playtime",
        52,
        "Gentle & Safe"
    ),
    BACKPACK_LUGGAGE(
        "Backpack & Luggage",
        "Luggage",
        "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&auto=format&fit=crop&q=80",
        "Travel spinners, backpacks & carry-ons",
        78,
        "Up to 40% OFF"
    ),
    BATHROOM_LINEN(
        "Bathroom Linen",
        "Bathtub",
        "https://images.unsplash.com/photo-1616627547584-bf28cee262db?w=400&auto=format&fit=crop&q=80",
        "Plush 700 GSM towels & bath mats",
        45,
        "100% Cotton"
    ),
    BED_LINEN(
        "Bed & Linen",
        "Bed",
        "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=400&auto=format&fit=crop&q=80",
        "Luxury sheets, duvets & pillows",
        60,
        "Flat 35% OFF"
    ),
    CAMERAS_PHOTOGRAPHY(
        "Cameras & Photography",
        "PhotoCamera",
        "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=400&auto=format&fit=crop&q=80",
        "Mirrorless cameras, lenses & rigs",
        40,
        "Pro Grade"
    ),
    COMPUTERS_ACCESSORIES(
        "Computers & Accessories",
        "Laptop",
        "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400&auto=format&fit=crop&q=80",
        "Keyboards, docks, monitors & mice",
        85,
        "Up to 45% OFF"
    ),
    GAMING(
        "Gaming",
        "SportsEsports",
        "https://images.unsplash.com/photo-1600080972464-8e5f35f63d08?w=400&auto=format&fit=crop&q=80",
        "Controllers, headsets & accessories",
        55,
        "Trending Gear"
    ),
    HOME_KITCHEN_APPLIANCES(
        "Home & Kitchen Appliances",
        "Kitchen",
        "https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?w=400&auto=format&fit=crop&q=80",
        "Air fryers, blenders & kettles",
        72,
        "Best Deals"
    ),
    HOME_DECOR(
        "Home Decor",
        "Chair",
        "https://images.unsplash.com/photo-1513519245088-0e12902e5a38?w=400&auto=format&fit=crop&q=80",
        "Vases, modern lamps & wall art",
        90,
        "Artisanal"
    ),
    KITCHENWARE(
        "Kitchenware",
        "Restaurant",
        "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=400&auto=format&fit=crop&q=80",
        "Cast iron skillets & chef knives",
        68,
        "Chef Grade"
    ),
    MOBILE_ACCESSORIES(
        "Mobile & Accessories",
        "Smartphone",
        "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=400&auto=format&fit=crop&q=80",
        "GaN chargers, cases & MagSafe docks",
        95,
        "Flash Deals"
    ),
    MUSICAL_INSTRUMENTS(
        "Musical Instruments",
        "MusicNote",
        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400&auto=format&fit=crop&q=80",
        "Acoustic guitars, synths & pedals",
        38,
        "Studio Ready"
    ),
    PERSONAL_CARE_APPLIANCES(
        "Personal Care Appliances",
        "ContentCut",
        "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=400&auto=format&fit=crop&q=80",
        "Beard trimmers & ionic dryers",
        50,
        "Up to 50% OFF"
    ),
    TOYS_GAMES(
        "Toys & Games",
        "SmartToy",
        "https://images.unsplash.com/photo-1566576912321-d58ddd7a6088?w=400&auto=format&fit=crop&q=80",
        "Board games, blocks & action figures",
        62,
        "Family Fun"
    ),
    ALL(
        "All Items",
        "Apps",
        "https://images.unsplash.com/photo-1472851294608-062f824d29cc?w=400&auto=format&fit=crop&q=80",
        "Explore everything",
        520,
        "Curated"
    )
}

data class ProductVariant(
    val id: String,
    val name: String,
    val inStock: Boolean = true
)

data class ProductReview(
    val id: String,
    val reviewer: String,
    val rating: Float,
    val date: String,
    val comment: String,
    val isVerified: Boolean = true
)

data class Product(
    val id: String,
    val title: String,
    val brand: String,
    val category: ProductCategory,
    val price: Double,
    val originalPrice: Double,
    val rating: Float,
    val reviewCount: Int,
    val imageUrls: List<String>,
    val description: String,
    val specs: Map<String, String>,
    val variants: List<ProductVariant>,
    val inStock: Boolean = true,
    val isFlashDeal: Boolean = false,
    val badge: String? = null,
    val subcategoryId: String? = null
) {
    val discountPercent: Int
        get() = if (originalPrice > price) {
            (((originalPrice - price) / originalPrice) * 100).toInt()
        } else 0

    val imageUrl: String
        get() = imageUrls.firstOrNull() ?: ""

    val name: String
        get() = title
}
