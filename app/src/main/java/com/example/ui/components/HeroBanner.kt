package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.ProductCategory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class DiwaliBannerSlide(
    val id: String,
    val title: String,
    val assetPath: String,
    val targetCategory: ProductCategory
)

// Legacy alias if needed
typealias BannerSlide = DiwaliBannerSlide

@Composable
fun HeroBanner(
    onExploreClick: () -> Unit = {},
    onBannerClick: (ProductCategory) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 4 uploaded Diwali promotional banners
    val banners = remember {
        listOf(
            DiwaliBannerSlide(
                id = "computer",
                title = "Diwali Special Sale - Computer Accessories Up to 70% Off",
                assetPath = "file:///android_asset/banners/banner_computer.png",
                targetCategory = ProductCategory.COMPUTERS_ACCESSORIES
            ),
            DiwaliBannerSlide(
                id = "towel",
                title = "Diwali Special Sale - Cuddle Cotton Premium Towels Up to 60% Off",
                assetPath = "file:///android_asset/banners/banner_towel.png",
                targetCategory = ProductCategory.BATHROOM_LINEN
            ),
            DiwaliBannerSlide(
                id = "baby",
                title = "Diwali Special Sale - Baby Products Up to 70% Off",
                assetPath = "file:///android_asset/banners/banner_baby.png",
                targetCategory = ProductCategory.BABY_TOYS
            ),
            DiwaliBannerSlide(
                id = "mobile",
                title = "Diwali Special Sale - Mobile Accessories Up to 70% Off",
                assetPath = "file:///android_asset/banners/banner_mobile.png",
                targetCategory = ProductCategory.MOBILE_ACCESSORIES
            )
        )
    }

    // High virtual page count to allow infinite continuous loop sliding in both directions
    val virtualCount = 10000
    val initialPage = (virtualCount / 2) - ((virtualCount / 2) % banners.size)
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { virtualCount }
    )

    // Continuous auto-sliding loop every 3.5 seconds
    LaunchedEffect(pagerState) {
        while (true) {
            delay(3500)
            if (!pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage(
                    page = pagerState.currentPage + 1,
                    animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    val currentBannerIndex = pagerState.currentPage % banners.size

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .aspectRatio(16f / 9f)
            .testTag("hero_banner_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("hero_banner_pager")
            ) { page ->
                val index = page % banners.size
                val item = banners[index]

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            onBannerClick(item.targetCategory)
                            onExploreClick()
                        }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(item.assetPath)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Subtle gradient scrim at the bottom edge for dot indicator contrast
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.5f)
                            )
                        )
                    )
                    .padding(vertical = 12.dp)
            ) {
                // Frosted glass indicator pill with animated dots
                Surface(
                    modifier = Modifier.align(Alignment.Center),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.45f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        banners.indices.forEach { idx ->
                            val isActive = idx == currentBannerIndex
                            val indicatorWidth by animateDpAsState(
                                targetValue = if (isActive) 18.dp else 6.dp,
                                animationSpec = tween(durationMillis = 300),
                                label = "banner_dot_width"
                            )
                            val dotColor = if (isActive) Color(0xFFFF9800) else Color.White.copy(alpha = 0.6f)

                            Box(
                                modifier = Modifier
                                    .size(width = indicatorWidth, height = 6.dp)
                                    .clip(CircleShape)
                                    .background(dotColor)
                                    .clickable {
                                        coroutineScope.launch {
                                            val offset = idx - currentBannerIndex
                                            pagerState.animateScrollToPage(pagerState.currentPage + offset)
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}
