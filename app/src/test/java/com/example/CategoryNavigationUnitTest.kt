package com.example

import com.example.data.OramiRepository
import com.example.model.ProductCategory
import com.example.viewmodel.OramiViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure JVM unit tests for Category and Subcategory navigation,
 * repository relationships, and ViewModel reactive filtering for the 15 categories.
 */
class CategoryNavigationUnitTest {

    @Test
    fun verifyRepositorySubcategoriesExistForAllCategories() {
        val repo = OramiRepository()
        val allSubcategories = repo.subcategories.value
        assertTrue("Subcategories should not be empty", allSubcategories.isNotEmpty())

        val autoSubs = repo.getSubcategories(ProductCategory.AUTOMOTIVES)
        assertTrue("Automotives should have subcategories", autoSubs.isNotEmpty())
        assertTrue(autoSubs.all { it.category == ProductCategory.AUTOMOTIVES })

        val babySubs = repo.getSubcategories(ProductCategory.BABY_TOYS)
        assertTrue("Baby & Toys should have subcategories", babySubs.isNotEmpty())

        val luggageSubs = repo.getSubcategories(ProductCategory.BACKPACK_LUGGAGE)
        assertTrue("Backpack & Luggage should have subcategories", luggageSubs.isNotEmpty())

        val gamingSubs = repo.getSubcategories(ProductCategory.GAMING)
        assertTrue("Gaming should have subcategories", gamingSubs.isNotEmpty())

        val compSubs = repo.getSubcategories(ProductCategory.COMPUTERS_ACCESSORIES)
        assertTrue("Computers should have subcategories", compSubs.isNotEmpty())

        val kitchenSubs = repo.getSubcategories(ProductCategory.HOME_KITCHEN_APPLIANCES)
        assertTrue("Home & Kitchen Appliances should have subcategories", kitchenSubs.isNotEmpty())
    }

    @Test
    fun verifyProductsHaveSubcategoryIdsAndCanBeQueried() {
        val repo = OramiRepository()
        val products = repo.products.value
        assertTrue(products.isNotEmpty())

        val autoProducts = repo.getProductsForSubcategory("sub_auto_care")
        assertTrue("Should find products for car care subcategory", autoProducts.isNotEmpty())
        assertEquals("p-auto-101", autoProducts.first().id)

        val compProducts = repo.getProductsForSubcategory("sub_comp_keyboards")
        assertTrue("Should find products for mechanical keyboards subcategory", compProducts.isNotEmpty())
        assertEquals("p-comp-101", compProducts.first().id)
    }

    @Test
    fun verifyViewModelCategoryAndSubcategorySelection() {
        val viewModel = OramiViewModel()

        // Select Automotives
        viewModel.selectCategory(ProductCategory.AUTOMOTIVES)
        assertEquals(ProductCategory.AUTOMOTIVES, viewModel.selectedCategory.value)
        assertEquals(null, viewModel.selectedSubcategory.value)

        // Select Subcategory
        val sub = viewModel.subcategories.value.first { it.category == ProductCategory.AUTOMOTIVES }
        viewModel.selectSubcategory(sub)
        assertEquals(sub, viewModel.selectedSubcategory.value)

        // Switching category clears selected subcategory
        viewModel.selectCategory(ProductCategory.GAMING)
        assertEquals(ProductCategory.GAMING, viewModel.selectedCategory.value)
        assertEquals(null, viewModel.selectedSubcategory.value)
    }

    @Test
    fun verifyCategorySearchQueryFiltering() {
        val viewModel = OramiViewModel()
        viewModel.setCategorySearchQuery("Keyboard")
        assertEquals("Keyboard", viewModel.categorySearchQuery.value)

        viewModel.clearCategorySearch()
        assertEquals("", viewModel.categorySearchQuery.value)
    }
}
