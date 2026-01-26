package com.mrwizard94.livingvillages.shop

import com.mrwizard94.livingvillages.LivingVillages
import com.mrwizard94.livingvillages.config.LVConfig
import com.mrwizard94.livingvillages.village.VillageData
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import java.util.UUID

/**
 * Shop Generator
 * 
 * Handles shop generation during village expansion.
 * Creates shops in villages based on tier and size.
 * 
 * Based on: LIVING_VILLAGES_CODEX.md - Shop Generation
 */
object ShopGenerator {
    
    /**
     * Generate shops for a village
     * Called when village is detected or expands
     */
    fun generateShopsForVillage(village: VillageData, world: ServerWorld) {
        if (!LVConfig.shops.enabled) {
            return
        }
        
        // Determine village size category
        val villageSize = estimateVillageSize(village)
        val shopCount = getShopCountForSize(villageSize)
        
        // Generate shops
        for (i in 0 until shopCount) {
            val shopPos = findShopLocation(village, world)
            if (shopPos == null) {
                LivingVillages.log("Could not find location for shop in ${village.name}")
                continue
            }
            
            // Pick shop type
            val shopType = ShopTypeRegistry.getRandomShopType(world.random)
            if (shopType == null) {
                LivingVillages.log("No shop types registered")
                continue
            }
            
            // Create shop data
            val shopId = UUID.randomUUID().toString()
            val shopData = ShopData(
                shopId = shopId,
                shopType = shopType.id,
                position = ShopPosition(shopPos.x, shopPos.y, shopPos.z),
                inventory = shopType.generateStock(world.random).toMutableList(),
                restockTimer = 0,
                lastRestockTime = world.time
            )
            
            // Register shop
            ShopRegistry.registerShop(shopData)
            
            // TODO: Place shop building structure
            // For now, we just register the shop data
            // The actual building placement would use BuildingPlacer
            
            LivingVillages.log("Generated shop: ${shopType.name} at $shopPos in ${village.name}")
        }
    }
    
    /**
     * Estimate village size category
     */
    private fun estimateVillageSize(village: VillageData): String {
        val buildingCount = village.getBuildingCount()
        return when {
            buildingCount >= 30 -> "large"
            buildingCount >= 15 -> "medium"
            else -> "small"
        }
    }
    
    /**
     * Get shop count for village size
     */
    private fun getShopCountForSize(size: String): Int {
        return LVConfig.shops.shopsPerVillage[size] ?: 1
    }
    
    /**
     * Find a location for a shop
     * Prefers areas near village center with good accessibility
     */
    private fun findShopLocation(village: VillageData, world: ServerWorld): BlockPos? {
        val center = village.centerPos
        val radius = village.radius
        
        // Search in a spiral pattern from center
        for (r in 5..radius step 5) {
            for (angle in 0 until 360 step 15) {
                val radians = Math.toRadians(angle.toDouble())
                val x = (center.x + r * Math.cos(radians)).toInt()
                val z = (center.z + r * Math.sin(radians)).toInt()
                val y = world.getTopY(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, x, z)
                val pos = BlockPos(x, y, z)
                
                // Check if location is suitable
                if (isSuitableShopLocation(world, pos, village)) {
                    return pos
                }
            }
        }
        
        return null
    }
    
    /**
     * Check if a position is suitable for a shop
     */
    private fun isSuitableShopLocation(
        world: ServerWorld,
        pos: BlockPos,
        village: VillageData
    ): Boolean {
        // Check if position is within village
        if (!pos.isWithinDistance(village.centerPos, village.radius.toDouble())) {
            return false
        }
        
        // Check if there's already a shop nearby
        val nearbyShops = ShopRegistry.getShopsNear(pos, 16.0)
        if (nearbyShops.isNotEmpty()) {
            return false
        }
        
        // Check if there's a building nearby (shops should be near other buildings)
        val nearbyBuildings = village.buildings.filter { building ->
            pos.getSquaredDistance(building.position) < 100 // 10 blocks
        }
        
        // Prefer locations near buildings but not too close
        return nearbyBuildings.isNotEmpty() && nearbyBuildings.none { building ->
            pos.getSquaredDistance(building.position) < 25 // 5 blocks
        }
    }
}
