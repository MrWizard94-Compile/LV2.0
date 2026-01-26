package com.mrwizard94.livingvillages.shop

import kotlinx.serialization.Serializable
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.util.Identifier

/**
 * Shop Data
 * 
 * Represents a shop's state, inventory, and configuration.
 * 
 * Based on: LIVING_VILLAGES_CODEX.md - Shop System
 */
@Serializable
data class ShopData(
    val shopId: String,
    val shopType: String,
    val position: ShopPosition,
    val inventory: MutableList<ShopListing> = mutableListOf(),
    var restockTimer: Int = 0,
    var lastRestockTime: Long = 0
) {
    fun toNbt(): NbtCompound {
        val nbt = NbtCompound()
        nbt.putString("ShopId", shopId)
        nbt.putString("ShopType", shopType)
        nbt.putInt("RestockTimer", restockTimer)
        nbt.putLong("LastRestockTime", lastRestockTime)
        
        // Position
        val posNbt = NbtCompound()
        posNbt.putInt("X", position.x)
        posNbt.putInt("Y", position.y)
        posNbt.putInt("Z", position.z)
        nbt.put("Position", posNbt)
        
        // Inventory
        val inventoryList = NbtList()
        inventory.forEach { listing ->
            inventoryList.add(listing.toNbt())
        }
        nbt.put("Inventory", inventoryList)
        
        return nbt
    }
    
    companion object {
        fun fromNbt(nbt: NbtCompound): ShopData {
            val shopId = nbt.getString("ShopId")
            val shopType = nbt.getString("ShopType")
            val restockTimer = nbt.getInt("RestockTimer")
            val lastRestockTime = nbt.getLong("LastRestockTime")
            
            val posNbt = nbt.getCompound("Position")
            val position = ShopPosition(
                x = posNbt.getInt("X"),
                y = posNbt.getInt("Y"),
                z = posNbt.getInt("Z")
            )
            
            val shop = ShopData(
                shopId = shopId,
                shopType = shopType,
                position = position,
                restockTimer = restockTimer,
                lastRestockTime = lastRestockTime
            )
            
            // Load inventory
            if (nbt.contains("Inventory")) {
                val inventoryList = nbt.getList("Inventory", 10) // NBT_COMPOUND
                inventoryList.forEach { listingNbt ->
                    shop.inventory.add(ShopListing.fromNbt(listingNbt as NbtCompound))
                }
            }
            
            return shop
        }
    }
}

@Serializable
data class ShopPosition(
    val x: Int,
    val y: Int,
    val z: Int
)

/**
 * Shop Listing
 * Represents an item for sale
 */
@kotlinx.serialization.Serializable
data class ShopListing(
    val itemId: String,
    val itemCount: Int,
    var stock: Int,
    val price: Int, // Price in emeralds
    val maxStock: Int = 64
) {
    fun toNbt(): NbtCompound {
        val nbt = NbtCompound()
        nbt.putString("ItemId", itemId)
        nbt.putInt("ItemCount", itemCount)
        nbt.putInt("Stock", stock)
        nbt.putInt("Price", price)
        nbt.putInt("MaxStock", maxStock)
        return nbt
    }
    
    fun reduceStock(amount: Int) {
        stock = (stock - amount).coerceAtLeast(0)
    }
    
    fun getDisplayText(): String {
        return "${itemCount}x $itemId - ${price} emeralds (Stock: $stock)"
    }
    
    fun createItemStack(): ItemStack? {
        val identifier = Identifier.tryParse(itemId) ?: return null
        val item = net.minecraft.registry.Registries.ITEM.get(identifier) ?: return null
        val mcItem = item as? net.minecraft.item.Item ?: return null
        return ItemStack(mcItem, itemCount)
    }
    
    companion object {
        fun fromNbt(nbt: NbtCompound): ShopListing {
            return ShopListing(
                itemId = nbt.getString("ItemId"),
                itemCount = nbt.getInt("ItemCount"),
                stock = nbt.getInt("Stock"),
                price = nbt.getInt("Price"),
                maxStock = if (nbt.contains("MaxStock")) nbt.getInt("MaxStock") else 64
            )
        }
    }
}
