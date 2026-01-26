package com.mrwizard94.livingvillages.shop

import com.mrwizard94.livingvillages.LivingVillages
import net.minecraft.item.Items
import java.util.concurrent.ConcurrentHashMap

/**
 * Shop Type Registry
 * 
 * Defines all shop types and their item pools.
 * 
 * Based on: LIVING_VILLAGES_CODEX.md - Shop Types
 */
object ShopTypeRegistry {
    
    private val shopTypes = ConcurrentHashMap<String, ShopType>()
    
    /**
     * Register all shop types
     */
    fun register() {
        // Food Shop
        registerType("food_shop", ShopType(
            id = "food_shop",
            name = "Food Shop",
            signText = "§6Food & Provisions",
            itemPool = listOf(
                ShopItemEntry("minecraft:bread", 1, 8, 16),
                ShopItemEntry("minecraft:cooked_beef", 2, 4, 8),
                ShopItemEntry("minecraft:cooked_porkchop", 2, 4, 8),
                ShopItemEntry("minecraft:cooked_chicken", 1, 4, 8),
                ShopItemEntry("minecraft:baked_potato", 1, 4, 8),
                ShopItemEntry("minecraft:cooked_mutton", 2, 4, 8),
                ShopItemEntry("minecraft:apple", 1, 4, 8),
                ShopItemEntry("minecraft:carrot", 1, 8, 16),
                ShopItemEntry("minecraft:potato", 1, 8, 16),
                ShopItemEntry("minecraft:beetroot", 1, 4, 8),
                ShopItemEntry("minecraft:melon_slice", 1, 8, 16),
                ShopItemEntry("minecraft:pumpkin_pie", 3, 2, 4),
                ShopItemEntry("minecraft:cookie", 1, 16, 32),
                ShopItemEntry("minecraft:cake", 10, 1, 1)
            ),
            priceMultiplier = 0.8
        ))
        
        // Ore & Ingots Shop
        registerType("ore_shop", ShopType(
            id = "ore_shop",
            name = "Ore & Ingots",
            signText = "§7Metals & Minerals",
            itemPool = listOf(
                ShopItemEntry("minecraft:iron_ingot", 5, 4, 8),
                ShopItemEntry("minecraft:gold_ingot", 8, 2, 4),
                ShopItemEntry("minecraft:copper_ingot", 3, 4, 8),
                ShopItemEntry("minecraft:coal", 1, 16, 32),
                ShopItemEntry("minecraft:redstone", 2, 16, 32),
                ShopItemEntry("minecraft:lapis_lazuli", 3, 8, 16),
                ShopItemEntry("minecraft:diamond", 20, 1, 2),
                ShopItemEntry("minecraft:emerald", 1, 1, 1), // Meta currency
                ShopItemEntry("minecraft:quartz", 2, 8, 16),
                ShopItemEntry("minecraft:netherite_scrap", 50, 1, 1),
                ShopItemEntry("minecraft:iron_block", 45, 1, 2),
                ShopItemEntry("minecraft:gold_block", 72, 1, 2),
                ShopItemEntry("minecraft:diamond_block", 180, 1, 1),
                ShopItemEntry("minecraft:emerald_block", 9, 1, 2)
            ),
            priceMultiplier = 2.5
        ))
        
        // Mob Drop Shop
        registerType("mob_drop_shop", ShopType(
            id = "mob_drop_shop",
            name = "Mob Drops",
            signText = "§cHunter's Goods",
            itemPool = listOf(
                ShopItemEntry("minecraft:leather", 2, 8, 16),
                ShopItemEntry("minecraft:bone", 1, 16, 32),
                ShopItemEntry("minecraft:string", 1, 16, 32),
                ShopItemEntry("minecraft:spider_eye", 2, 4, 8),
                ShopItemEntry("minecraft:gunpowder", 3, 4, 8),
                ShopItemEntry("minecraft:slime_ball", 2, 4, 8),
                ShopItemEntry("minecraft:ender_pearl", 8, 2, 4),
                ShopItemEntry("minecraft:blaze_rod", 10, 2, 4),
                ShopItemEntry("minecraft:phantom_membrane", 12, 2, 4),
                ShopItemEntry("minecraft:shulker_shell", 50, 1, 2),
                ShopItemEntry("minecraft:prismarine_shard", 3, 8, 16),
                ShopItemEntry("minecraft:rabbit_hide", 1, 8, 16),
                ShopItemEntry("minecraft:feather", 1, 16, 32)
            ),
            priceMultiplier = 1.5
        ))
        
        // Block Shop
        registerType("block_shop", ShopType(
            id = "block_shop",
            name = "Building Blocks",
            signText = "§9Construction Materials",
            itemPool = listOf(
                ShopItemEntry("minecraft:oak_planks", 1, 32, 64),
                ShopItemEntry("minecraft:stone", 1, 32, 64),
                ShopItemEntry("minecraft:cobblestone", 1, 32, 64),
                ShopItemEntry("minecraft:bricks", 2, 16, 32),
                ShopItemEntry("minecraft:glass", 1, 16, 32),
                ShopItemEntry("minecraft:glass_pane", 1, 16, 32),
                ShopItemEntry("minecraft:oak_log", 2, 16, 32),
                ShopItemEntry("minecraft:spruce_log", 2, 16, 32),
                ShopItemEntry("minecraft:birch_log", 2, 16, 32),
                ShopItemEntry("minecraft:stone_bricks", 2, 16, 32),
                ShopItemEntry("minecraft:sandstone", 1, 16, 32),
                ShopItemEntry("minecraft:terracotta", 2, 16, 32)
            ),
            priceMultiplier = 1.2
        ))
        
        // Tool Shop
        registerType("tool_shop", ShopType(
            id = "tool_shop",
            name = "Tools & Equipment",
            signText = "§eTools & Equipment",
            itemPool = listOf(
                ShopItemEntry("minecraft:wooden_pickaxe", 3, 1, 1),
                ShopItemEntry("minecraft:stone_pickaxe", 5, 1, 1),
                ShopItemEntry("minecraft:iron_pickaxe", 15, 1, 1),
                ShopItemEntry("minecraft:wooden_axe", 3, 1, 1),
                ShopItemEntry("minecraft:stone_axe", 5, 1, 1),
                ShopItemEntry("minecraft:iron_axe", 15, 1, 1),
                ShopItemEntry("minecraft:wooden_shovel", 2, 1, 1),
                ShopItemEntry("minecraft:iron_shovel", 10, 1, 1),
                ShopItemEntry("minecraft:iron_hoe", 10, 1, 1),
                ShopItemEntry("minecraft:shears", 8, 1, 1),
                ShopItemEntry("minecraft:flint_and_steel", 5, 1, 1),
                ShopItemEntry("minecraft:bucket", 5, 1, 1)
            ),
            priceMultiplier = 1.5
        ))
        
        LivingVillages.log("Registered ${shopTypes.size} shop types")
    }
    
    /**
     * Register a shop type
     */
    private fun registerType(id: String, shopType: ShopType) {
        shopTypes[id] = shopType
    }
    
    /**
     * Get shop type by ID
     */
    fun getShopType(id: String): ShopType? {
        return shopTypes[id]
    }
    
    /**
     * Get random shop type
     */
    fun getRandomShopType(random: java.util.Random = java.util.Random()): ShopType? {
        if (shopTypes.isEmpty()) return null
        val keys = shopTypes.keys.toList()
        return shopTypes[keys[random.nextInt(keys.size)]]
    }

    /**
     * Convenience overload to accept Minecraft's Random directly (world.random)
     */
    fun getRandomShopType(random: net.minecraft.util.math.random.Random): ShopType? {
        return getRandomShopType(java.util.Random(random.nextLong()))
    }
    
    /**
     * Get all shop types
     */
    fun getAllShopTypes(): Collection<ShopType> {
        return shopTypes.values
    }
}
