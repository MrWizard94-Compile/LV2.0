package com.mrwizard94.livingvillages.shop

import com.mrwizard94.livingvillages.LivingVillages
import com.mrwizard94.livingvillages.config.LVConfig
import net.minecraft.server.world.ServerWorld

/**
 * Restock System
 * 
 * Handles shop inventory refresh over time.
 * 
 * Based on: LIVING_VILLAGES_CODEX.md - Restock System
 */
object RestockSystem {
    
    /**
     * Tick all shops in loaded chunks
     * Manages restock timing
     */
    fun tick(world: ServerWorld) {
        if (!LVConfig.shops.enabled) {
            return
        }
        
        val restockMode = LVConfig.shops.restockMode
        
        ShopRegistry.getAllShops().forEach { shop ->
            when (restockMode) {
                "time_based" -> tickTimeBasedRestock(shop, world)
                "daily" -> tickDailyRestock(shop, world)
                "never" -> {
                    // No restocking
                }
                else -> tickTimeBasedRestock(shop, world) // Default
            }
        }
    }
    
    /**
     * Time-based restocking
     */
    private fun tickTimeBasedRestock(shop: ShopData, world: ServerWorld) {
        shop.restockTimer++
        
        val interval = LVConfig.shops.restockIntervalTicks
        
        if (shop.restockTimer >= interval) {
            restockShop(shop, world)
            shop.restockTimer = 0
        }
    }
    
    /**
     * Daily restocking
     */
    private fun tickDailyRestock(shop: ShopData, world: ServerWorld) {
        val currentTime = world.timeOfDay
        val lastRestock = shop.lastRestockTime
        
        // Check if a new day has started (24000 ticks per day)
        val currentDay = currentTime / 24000
        val lastRestockDay = lastRestock / 24000
        
        if (currentDay > lastRestockDay) {
            restockShop(shop, world)
            shop.lastRestockTime = currentTime
        }
    }
    
    /**
     * Restock a shop
     */
    private fun restockShop(shop: ShopData, world: ServerWorld) {
        val shopType = ShopTypeRegistry.getShopType(shop.shopType)
        if (shopType == null) {
            LivingVillages.error("Shop type not found: ${shop.shopType}")
            return
        }
        
        // Clear old stock
        shop.inventory.clear()
        
        // Generate new stock
        val newStock = shopType.generateStock(world.random)
        shop.inventory.addAll(newStock)
        
        LivingVillages.log("Restocked shop: ${shop.shopId} (${shop.shopType})")
        
        // TODO: Notification to nearby players
    }
}
