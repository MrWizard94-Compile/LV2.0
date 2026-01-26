package com.mrwizard94.livingvillages.shop

import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.Identifier


/**
 * Shop Type
 * 
 * Defines a type of shop with its item pool and pricing.
 * 
 * Based on: LIVING_VILLAGES_CODEX.md - Shop Types
 */
data class ShopType(
    val id: String,
    val name: String,
    val signText: String,
    val itemPool: List<ShopItemEntry>,
    val priceMultiplier: Double = 1.0
) {
    /**
     * Get random items from the pool for stock generation
     */
    fun generateStock(random: java.util.Random, itemCount: Int = 6): List<ShopListing> {
        val stock = mutableListOf<ShopListing>()
        val selectedItems = itemPool.shuffled(random).take(itemCount)
        
        selectedItems.forEach { entry ->
            val quantity = random.nextInt(entry.minQuantity, entry.maxQuantity + 1)
            val basePrice = entry.basePrice
            val finalPrice = (basePrice * priceMultiplier).toInt().coerceAtLeast(1)
            
            stock.add(
                ShopListing(
                    itemId = entry.itemId,
                    itemCount = quantity,
                    stock = entry.maxStock,
                    price = finalPrice,
                    maxStock = entry.maxStock
                )
            )
        }
        
        return stock
    }

    /**
     * Convenience overload to accept Minecraft's Random directly (world.random)
     */
    fun generateStock(random: net.minecraft.util.math.random.Random, itemCount: Int = 6): List<ShopListing> {
        return generateStock(java.util.Random(random.nextLong()), itemCount)
    }
}

/**
 * Shop Item Entry
 * Defines an item that can appear in a shop type
 */
data class ShopItemEntry(
    val itemId: String,
    val basePrice: Int,
    val minQuantity: Int = 1,
    val maxQuantity: Int = 16,
    val maxStock: Int = 64
)
