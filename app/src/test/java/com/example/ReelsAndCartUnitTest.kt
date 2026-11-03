package com.example

import com.example.data.OramiRepository
import com.example.model.ProductCategory
import com.example.viewmodel.AppScreen
import com.example.viewmodel.OramiViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReelsAndCartUnitTest {

    @Test
    fun testReelsInitializationAndSeedContent() {
        val repo = OramiRepository()
        val viewModel = OramiViewModel(repo)

        val reels = viewModel.reels.value
        assertTrue("Reels list should not be empty", reels.isNotEmpty())
        assertEquals("Should have 6 initial seed reels", 6, reels.size)

        val firstReel = reels.first()
        assertNotNull(firstReel.taggedProduct)
        assertTrue(firstReel.videoUrl.isNotEmpty())
        assertTrue(firstReel.comments.isNotEmpty())
        assertTrue(firstReel.likesCount > 0)
    }

    @Test
    fun testReelLikeAndCommentInteractions() {
        val repo = OramiRepository()
        val viewModel = OramiViewModel(repo)

        val firstReel = viewModel.reels.value.first()
        val initialLikes = firstReel.likesCount
        val initialLiked = firstReel.isLiked

        // Toggle Like
        viewModel.toggleLikeReel(firstReel.id)
        val afterLike = viewModel.reels.value.first { it.id == firstReel.id }
        assertEquals(!initialLiked, afterLike.isLiked)
        if (!initialLiked) {
            assertEquals(initialLikes + 1, afterLike.likesCount)
        }

        // Add Comment
        val initialCommentsCount = afterLike.comments.size
        viewModel.addCommentToReel(firstReel.id, "Awesome product demonstration!", "Tester")
        val afterComment = viewModel.reels.value.first { it.id == firstReel.id }
        assertEquals(initialCommentsCount + 1, afterComment.comments.size)
        assertEquals("Awesome product demonstration!", afterComment.comments.first().text)
        assertEquals("Tester", afterComment.comments.first().authorName)
    }

    @Test
    fun testAddCustomProjectVideoReel() {
        val repo = OramiRepository()
        val viewModel = OramiViewModel(repo)

        val sampleProduct = repo.products.value.first()
        val initialSize = viewModel.reels.value.size

        viewModel.addCustomReel(
            title = "My Project Video Showcase",
            description = "Tested during Diwali sale",
            videoUrl = "https://example.com/custom_video.mp4",
            product = sampleProduct
        )

        val reels = viewModel.reels.value
        assertEquals(initialSize + 1, reels.size)
        val customReel = reels.first()
        assertEquals("My Project Video Showcase", customReel.title)
        assertTrue(customReel.isCustomUserVideo)
        assertEquals(sampleProduct.id, customReel.taggedProduct.id)
    }

    @Test
    fun testReelsScreenNavigation() {
        val viewModel = OramiViewModel()
        viewModel.navigateTo(AppScreen.REELS)
        assertEquals(AppScreen.REELS, viewModel.currentScreen.value)
    }
}
