package com.mrwizard94.livingvillages.shop

import com.mrwizard94.livingvillages.LivingVillages
import net.minecraft.util.math.BlockPos
import java.util.concurrent.ConcurrentHashMap

/**
 * Shop Registry
 * 
 * In-memory tracking of all shops in the world.
 */
object ShopRegistry {
    
    private val shops = ConcurrentHashMap<String, ShopData>()
    
    /**
     * Register a shop
     */
    fun registerShop(shop: ShopData) {
        shops[shop.shopId] = shop
    }
    
    /**
     * Get shop by ID
     */
    fun getShop(shopId: String): ShopData? {
        return shops[shopId]
    }
    
    /**
     * Get shop at position
     */
    fun getShopAt(pos: BlockPos): ShopData? {
        return shops.values.firstOrNull { shop ->
            shop.position.x == pos.x &&
            shop.position.y == pos.y &&
            shop.position.z == pos.z
        }
    }
    
    /**
     * Get shops near a position
     */
    fun getShopsNear(pos: BlockPos, maxDistance: Double): List<ShopData> {
        return shops.values.filter { shop ->
            val shopPos = BlockPos(shop.position.x, shop.position.y, shop.position.z)
            pos.getSquaredDistance(shopPos) < maxDistance * maxDistance
        }
    }
    
    /**
     * Get all shops
     */
    fun getAllShops(): Collection<ShopData> {
        return shops.values
    }
    
    /**
     * Unregister a shop
     */
    fun unregisterShop(shopId: String) {
        shops.remove(shopId)
    }
    
    /**
     * Clear all shops
     */
    fun clear() {
        shops.clear()
    }
    
    /**
     * Get shop count
     */
    fun getShopCount(): Int {
        return shops.size
    }
}
