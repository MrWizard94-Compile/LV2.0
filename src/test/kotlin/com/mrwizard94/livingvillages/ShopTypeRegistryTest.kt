package com.mrwizard94.livingvillages

import com.mrwizard94.livingvillages.shop.ShopTypeRegistry
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ShopTypeRegistryTest {

    @BeforeTest
    fun setup() {
        // Ensure registration is idempotent; Registry overwrites existing entries
        ShopTypeRegistry.register()
    }

    @Test
    fun `registers default shop types`() {
        val all = ShopTypeRegistry.getAllShopTypes()
        assertEquals(5, all.size, "Expected 5 default shop types")
        assertNotNull(ShopTypeRegistry.getShopType("food_shop"))
    }

    @Test
    fun `random shop type returns non-null`() {
        val randomType = ShopTypeRegistry.getRandomShopType(java.util.Random(42))
        assertNotNull(randomType, "Expected a random shop type to be returned")
    }
}