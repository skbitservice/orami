package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.model.Product
import com.example.model.ReelComment
import com.example.model.ReelItem
import com.example.ui.theme.OramiCoral
import com.example.viewmodel.OramiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelsScreen(
    viewModel: OramiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val reels by viewModel.reels.collectAsStateWithLifecycle()
    val allProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { reels.size })

    var activeCommentReel by remember { mutableStateOf<ReelItem?>(null) }
    var showAddReelDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("reels_screen")
    ) {
        if (reels.isNotEmpty()) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val reel = reels[page]
                val isPageActive = pagerState.currentPage == page

                SingleReelPage(
                    reel = reel,
                    isActive = isPageActive,
                    onToggleLike = { viewModel.toggleLikeReel(reel.id) },
                    onOpenComments = { activeCommentReel = reel },
                    onShare = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "${reel.title}\n\n${reel.description}\n\nShop on Orami: www.orami.in"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Video Reel"))
                    },
                    onBuyNow = { viewModel.buyNowFromReel(reel.taggedProduct) },
                    onAddToCart = { viewModel.addToCart(reel.taggedProduct) },
                    onOpenProduct = { viewModel.openProductDetail(reel.taggedProduct) }
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No reels available yet.\nTap '+ Add Video' to upload!",
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        // Top Floating Header: Orami Reels + "+ Add Video" CTA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.85f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(OramiCoral),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Orami Reels",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = "Diwali Corporate & Retail Video Showcase",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                }
            }

            Surface(
                onClick = { showAddReelDialog = true },
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.testTag("button_add_reel")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Reel",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Video",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }

        // Comments Bottom Sheet
        if (activeCommentReel != null) {
            ModalBottomSheet(
                onDismissRequest = { activeCommentReel = null },
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("reels_comments_sheet")
            ) {
                val currentReel = reels.find { it.id == activeCommentReel?.id } ?: activeCommentReel!!
                ReelsCommentsSheetContent(
                    reel = currentReel,
                    onAddComment = { text ->
                        viewModel.addCommentToReel(currentReel.id, text, viewModel.userProfile.value.name)
                    },
                    onClose = { activeCommentReel = null }
                )
            }
        }

        // Add Video Dialog
        if (showAddReelDialog) {
            AddReelDialog(
                products = allProducts,
                onDismiss = { showAddReelDialog = false },
                onPublish = { title, desc, url, product ->
                    viewModel.addCustomReel(title, desc, url, product)
                    showAddReelDialog = false
                }
            )
        }
    }
}

@Composable
private fun SingleReelPage(
    reel: ReelItem,
    isActive: Boolean,
    onToggleLike: () -> Unit,
    onOpenComments: () -> Unit,
    onShare: () -> Unit,
    onBuyNow: () -> Unit,
    onAddToCart: () -> Unit,
    onOpenProduct: () -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(false) }
    var isExpandedCaption by remember { mutableStateOf(false) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var showPlayPauseIndicator by remember { mutableStateOf(false) }

    // When page changes from active to inactive, pause video
    DisposableEffect(isActive) {
        if (!isActive) {
            videoViewRef?.pause()
        } else if (isPlaying) {
            videoViewRef?.start()
        }
        onDispose {
            videoViewRef?.stopPlayback()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Poster / Fallback Image
        AsyncImage(
            model = reel.thumbnailUrl ?: reel.taggedProduct.imageUrl,
            contentDescription = reel.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Native VideoView playback
        val hasLocalRawVideo = reel.videoUrl.contains("diwali_corporate_gifting") || reel.videoUrl.contains("raw")
        if (hasLocalRawVideo || reel.videoUrl.startsWith("http")) {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        val videoUri = if (hasLocalRawVideo) {
                            Uri.parse("android.resource://${ctx.packageName}/${R.raw.diwali_corporate_gifting}")
                        } else {
                            Uri.parse(reel.videoUrl)
                        }
                        setVideoURI(videoUri)
                        setOnPreparedListener { mp ->
                            mp.isLooping = true
                            val vol = if (isMuted) 0f else 1f
                            mp.setVolume(vol, vol)
                            if (isActive && isPlaying) {
                                start()
                            }
                        }
                        setOnErrorListener { _, _, _ -> true }
                        videoViewRef = this
                    }
                },
                update = { vv ->
                    if (isActive) {
                        if (isPlaying && !vv.isPlaying) {
                            vv.start()
                        } else if (!isPlaying && vv.isPlaying) {
                            vv.pause()
                        }
                    } else {
                        if (vv.isPlaying) {
                            vv.pause()
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Tap area for Play / Pause toggle
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    val nextPlay = !isPlaying
                    isPlaying = nextPlay
                    showPlayPauseIndicator = true
                    videoViewRef?.let { vv ->
                        if (nextPlay) vv.start() else vv.pause()
                    }
                }
        )

        // Animated Play/Pause indicator icon on tap
        AnimatedVisibility(
            visible = !isPlaying || showPlayPauseIndicator,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.55f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isPlaying) "Playing" else "Paused",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        // Dark Gradient scrim for legible text and controls
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.88f)
                        )
                    )
                )
        )

        // Right side engagement action rail
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mute / Unmute Button
            IconButton(
                onClick = {
                    val nextMute = !isMuted
                    isMuted = nextMute
                    videoViewRef?.let {
                        // Handled via volume toggle
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.Black.copy(alpha = 0.45f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = if (isMuted) "Unmute" else "Mute",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            val scale by animateFloatAsState(
                targetValue = if (reel.isLiked) 1.25f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "like_scale"
            )

            // Like
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onToggleLike,
                    modifier = Modifier
                        .scale(scale)
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                        .testTag("reel_like_button_${reel.id}")
                ) {
                    Icon(
                        imageVector = if (reel.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (reel.isLiked) Color(0xFFFF2D55) else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "${reel.likesCount}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Comments
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onOpenComments,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                        .testTag("reel_comment_button_${reel.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "${reel.comments.size}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Share
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                        .testTag("reel_share_button_${reel.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "${reel.sharesCount}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Bottom Details & Tagged Product Card
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.82f)
                .padding(start = 16.dp, bottom = 20.dp)
        ) {
            // Author info & Verified tag
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(OramiCoral),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = reel.authorName.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = reel.authorHandle,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0x33FF9800)
                ) {
                    Text(
                        text = "Verified Store",
                        color = Color(0xFFFF9800),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // WhatsApp Quick Inquiry CTA for Corporate Gifting
            if (reel.whatsappContact != null) {
                Surface(
                    onClick = {
                        val waUrl = "https://api.whatsapp.com/send?phone=91${reel.whatsappContact}&text=Hello%20Orami%20Retail,%20I%20am%20interested%20in%20Bulk%20Corporate%20Diwali%20Gifting!"
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(waUrl)))
                        } catch (e: Exception) {
                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${reel.whatsappContact}")))
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF25D366),
                    modifier = Modifier.testTag("whatsapp_corporate_inquiry_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "WhatsApp",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "📲 WhatsApp Bulk Orders: 9812644480",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Title
            Text(
                text = reel.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = if (isExpandedCaption) 5 else 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description with expand / collapse toggle
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isExpandedCaption = !isExpandedCaption }
            ) {
                Text(
                    text = reel.description,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = if (isExpandedCaption) 15 else 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isExpandedCaption) "Show less ▲" else "...more ▼",
                    color = Color(0xFFFFB74D),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tagged Product Floating Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenProduct() }
                    .testTag("reel_tagged_product_card_${reel.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = reel.taggedProduct.imageUrl,
                        contentDescription = reel.taggedProduct.title,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = reel.taggedProduct.title,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "₹${reel.taggedProduct.price.toInt()}",
                                color = OramiCoral,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "₹${reel.taggedProduct.originalPrice.toInt()}",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 11.sp,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color.White.copy(alpha = 0.18f), CircleShape)
                            .testTag("reel_quick_add_cart_${reel.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Add to Bag",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        onClick = onBuyNow,
                        colors = ButtonDefaults.buttonColors(containerColor = OramiCoral),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("reel_buy_now_button_${reel.id}")
                    ) {
                        Text("Buy", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReelsCommentsSheetContent(
    reel: ReelItem,
    onAddComment: (String) -> Unit,
    onClose: () -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Comments (${reel.comments.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (reel.comments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No comments yet. Be the first to comment!")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                items(reel.comments, key = { it.id }) { comment ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(OramiCoral.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = comment.authorName.take(1),
                                fontWeight = FontWeight.Bold,
                                color = OramiCoral
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = comment.authorName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = comment.timeAgo,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(text = comment.text, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Add a comment...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("reel_comment_input_field"),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (commentText.isNotBlank()) {
                        onAddComment(commentText)
                        commentText = ""
                    }
                },
                modifier = Modifier
                    .background(OramiCoral, CircleShape)
                    .testTag("reel_send_comment_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun AddReelDialog(
    products: List<Product>,
    onDismiss: () -> Unit,
    onPublish: (String, String, String, Product) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf(products.firstOrNull()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Add Product Video Reel",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reel Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Tags") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = videoUrl,
                    onValueChange = { videoUrl = it },
                    label = { Text("Video URL (MP4)") },
                    singleLine = true,
                    placeholder = { Text("https://example.com/video.mp4") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Select Tagged Product:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(modifier = Modifier.height(120.dp)) {
                    items(products.take(15)) { product ->
                        val isSelected = product.id == selectedProduct?.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedProduct = product }
                                .background(
                                    if (isSelected) OramiCoral.copy(alpha = 0.15f) else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = product.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) OramiCoral else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            selectedProduct?.let { prod ->
                                onPublish(title, description, videoUrl, prod)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OramiCoral),
                        enabled = selectedProduct != null
                    ) {
                        Text("Publish", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
