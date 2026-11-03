# Orami E-Commerce Mobile App Redesign

Redesigning **Orami (orami.in)** from the ground up into a high-performance, minimalist multi-category Android e-commerce application. The new experience elevates everyday shopping with expansive whitespace, elegant typographic contrast, instant search & faceted filtering, interactive customer ratings & reviews, flash deals, coupon-enabled cart checkout, persistent wishlist, and visual order tracking.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The following directions have been confirmed and tailored based on your answers:
> - **Product Scope**: Multi-category retail and general e-commerce catalog (Fashion & Apparel, Electronics & Tech, Home & Living, Beauty & Grooming, and Bags & Accessories).
> - **Design Aesthetic**: Clean, modern, and minimalist with generous negative space, refined typography, subtle border strokes, and focused high-contrast accents.
> - **Key Highlights**: Fast live search with tag suggestions, deep category filtering (price range, ratings, in-stock), rich product details with image carousels, customer reviews, promotional coupon system with instant discount calculation, cart & mock order placement with real-time status tracking, and wishlist management.

---

### 1. Overview & Core Concept

- **What It Does**: Transforms Orami into a seamless, tactile mobile shopping destination. Shoppers can effortlessly discover trending collections, search and filter across thousands of products, inspect high-resolution product visuals, read verified customer reviews, apply discount promo codes, save favorites, and track past and active orders.
- **Target Audience / Persona**: Contemporary mobile shoppers looking for an uncluttered, fast, and trustworthy online shopping experience across diverse product categories.
- **Key Value**: Replaces cluttered web browsing with a responsive, native Android app that prioritizes product discovery, speed, intuitive navigation, and frictionless checkout.

---

### 2. User Experience & Visual Design

#### Key User Flows
1. **Home & Discovery**:
   - Dynamic Hero Banner with featured seasonal promotions and swipeable campaign highlights.
   - Horizontal category pill navigation (All, Fashion, Electronics, Home, Beauty, Bags).
   - "Flash Deals" section with countdown timer badge and discount percentages.
   - Staggered curated grid of trending products with quick wishlist heart toggles and one-tap cart addition.
2. **Search & Exploration**:
   - Prominent search bar with instant query matching and recent search chips.
   - Comprehensive filter sheet (Price Range slider, Minimum Rating, Sort by Popularity/Price/Rating).
   - Dynamic result count and zero-state suggestions if no matches are found.
3. **Product Detail View**:
   - Immersive multi-image preview with pagination indicator.
   - Stock status, pricing with strikethrough original price and badge savings.
   - Variant selector (Size, Color, Edition) with visual chips.
   - Expandable product description, specifications table, and verified customer review breakdown (star distribution + written feedback).
   - Sticky bottom action bar with "Add to Cart" and "Buy Now".
4. **Cart & Seamless Checkout**:
   - Itemized cart list with steppers (+ / -) and swipe-to-delete.
   - Coupon voucher input (e.g. `ORAMI20`, `WELCOME10`, `FREESHIP`) with immediate validation, savings breakdown, and free shipping tier progress bar.
   - Clean checkout sheet collecting delivery address and payment method (UPI, Cards, NetBanking, COD).
   - Order confirmation celebration dialog with immediate redirection to Order Tracking.
5. **Wishlist & Account**:
   - Wishlist tab with one-tap "Move all to cart" and individual removal.
   - Profile & Order History tab showing active orders with a visual milestone progress bar (Ordered → Packed → Shipped → Delivered).

#### Visual Identity & Theme
- **Aesthetic Direction**: High-end modern minimalism. Focus on content-first hierarchy, crisp geometric borders, and breathable padding (16dp-24dp margins).
- **Color Palette**:
  - `Primary`: Deep Charcoal / Obsidian (`#121316`) for strong typography and high-contrast primary CTA buttons.
  - `Accent / Brand`: Vibrant Coral / Warm Amber (`#FF5722` / `#E64A19`) for discount badges, sale tags, and review stars.
  - `Surface`: Pure White (`#FFFFFF`) and Soft Milk Background (`#F8F9FA`) with delicate outline borders (`#EEEEEE` / `#E2E4E8`).
  - `Success`: Fresh Emerald (`#00875A`) for in-stock indicators, coupon applied alerts, and delivery milestones.
- **Typography & Hierarchy**:
  - Crisp Display font styling for hero titles and price headers.
  - Clear, legible medium weight body typography with strict `sp` scaling for accessibility.
  - Distinctive micro-copy for SKU details, ratings, and promo tags.
- **Interactive Feedback & Motion**:
  - Haptic touch feedback and Material ripple on all cards and buttons.
  - Spring-animated heart toggles for wishlist.
  - Smooth bottom sheet slide-ups for filters, coupons, and quick checkout.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Unified State Management (MVVM with Kotlin StateFlow)**
  - *Chosen Approach*: Centralized `OramiViewModel` managing reactive flows for catalog, search query, active filters, cart items, applied coupons, wishlist IDs, and order records.
  - *Why*: Guarantees instant UI reactivity across all screens without reloading delays; changes in cart or wishlist reflect everywhere in real time.
- **Decision 2: Rich Built-in Catalog with Extensible Category Models**
  - *Chosen Approach*: Realistic, multi-category mock dataset covering electronics, fashion, bags, home, and beauty with high-resolution imagery, rich specs, ratings, and user reviews.
  - *Why*: Delivers an immediate, fully working, and delightful end-to-end shopping journey right out of the box without waiting on an external API downtime.
- **Decision 3: Zero-Friction Native Checkout Simulation**
  - *Chosen Approach*: In-app address validation, coupon engine with real math discounts, and instantaneous order placement leading directly into interactive order status tracking.
  - *Why*: Provides the user with a realistic conversion funnel and testable e-commerce UX.

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                          Orami Mobile App UI                           │
├───────────────────┬───────────────────┬────────────────────────────────┤
│    Home & Deals   │  Catalog & Search │  Product Detail & Reviews      │
│  - Hero Banners   │  - Search Bar     │  - Image Gallery Carousel      │
│  - Category Chips │  - Filter Bottom- │  - Variant Selector            │
│  - Featured Grid  │    sheet & Sort   │  - Customer Ratings & Reviews  │
├───────────────────┴───────────────────┴────────────────────────────────┤
│               Cart, Checkout & Order Tracking Sheet                    │
│  - Item Stepper Quantity & Promo Code Engine                           │
│  - Order Completion & Step-by-Step Delivery Milestones                 │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                           OramiViewModel                               │
│  - productsState: StateFlow<List<Product>>                             │
│  - cartState: StateFlow<CartState> (items, coupon, discount, total)    │
│  - wishlistState: StateFlow<Set<String>>                               │
│  - ordersState: StateFlow<List<Order>>                                 │
│  - searchQuery & filterState: StateFlow<FilterCriteria>                │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                     Repository & Persistence Layer                     │
│  - OramiRepository: Catalog source, product query engine               │
│  - Local DataStore / Room storage for persistent Cart, Wishlist, Orders │
└────────────────────────────────────────────────────────────────────────┘
```

#### Data Model Entities
- **Product**: ID, title, subtitle, category, price, originalPrice, discountPercent, rating, reviewCount, imageUrls, description, tags, inStock, specs, variants.
- **Review**: ID, reviewerName, rating, date, comment, isVerifiedPurchase.
- **CartItem**: product, selectedVariant, quantity.
- **Coupon**: code, discountPercent, minOrderValue, maxDiscount, description.
- **Order**: orderId, items, subtotal, discount, total, timestamp, deliveryAddress, status (Ordered, Packed, Shipped, Delivered).
