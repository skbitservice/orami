package com.example.model

data class ReelComment(
    val id: String,
    val authorName: String,
    val text: String,
    val timeAgo: String = "Just now",
    val likesCount: Int = 0
)

data class ReelItem(
    val id: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val taggedProduct: Product,
    val authorName: String = "Orami Official",
    val authorHandle: String = "@orami_store",
    val likesCount: Int = 1420,
    val isLiked: Boolean = false,
    val comments: List<ReelComment> = emptyList(),
    val sharesCount: Int = 128,
    val isCustomUserVideo: Boolean = false,
    val thumbnailUrl: String? = null,
    val whatsappContact: String? = null
)
