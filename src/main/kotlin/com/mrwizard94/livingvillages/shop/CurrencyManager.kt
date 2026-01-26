package com.mrwizard94.livingvillages.shop

import com.mrwizard94.livingvillages.config.LVConfig
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Identifier

/**
 * Currency Manager
 * 
 * Handles payment processing for shop purchases.
 * 
 * Based on: LIVING_VILLAGES_CODEX.md - Currency Manager
 */
object CurrencyManager {
    
    /**
     * Get player's currency amount
     */
    fun getPlayerCurrency(player: PlayerEntity): Int {
        var total = 0
        
        // Count emeralds
        for (i in 0 until player.inventory.size()) {
            val stack = player.inventory.getStack(i)
            if (stack.item == Items.EMERALD) {
                total += stack.count
            } else if (stack.item == Items.EMERALD_BLOCK) {
                total += stack.count * 9
            }
        }
        
        // Check accepted currencies from config
        LVConfig.shops.acceptedCurrencies.forEach { currency ->
            val itemId = Identifier.tryParse(currency.item) ?: return@forEach
            val item = net.minecraft.registry.Registries.ITEM.get(itemId) ?: return@forEach
            
            for (i in 0 until player.inventory.size()) {
                val stack = player.inventory.getStack(i)
                if (stack.item == item) {
                    total += stack.count * currency.value
                }
            }
        }
        
        return total
    }
    
    /**
     * Deduct currency from player
     */
    fun deductCurrency(player: PlayerEntity, amount: Int): Boolean {
        var remaining = amount
        
        // Try to use emerald blocks first (more efficient)
        for (i in 0 until player.inventory.size()) {
            if (remaining <= 0) break
            
            val stack = player.inventory.getStack(i)
            if (stack.item == Items.EMERALD_BLOCK) {
                val blocksNeeded = (remaining + 8) / 9 // Round up
                val blocksToTake = blocksNeeded.coerceAtMost(stack.count)
                val emeraldsFromBlocks = blocksToTake * 9
                
                stack.decrement(blocksToTake)
                remaining -= emeraldsFromBlocks
            }
        }
        
        // Then use emeralds
        for (i in 0 until player.inventory.size()) {
            if (remaining <= 0) break
            
            val stack = player.inventory.getStack(i)
            if (stack.item == Items.EMERALD) {
                val emeraldsToTake = remaining.coerceAtMost(stack.count)
                stack.decrement(emeraldsToTake)
                remaining -= emeraldsToTake
            }
        }
        
        // Check other accepted currencies if still needed
        if (remaining > 0) {
            LVConfig.shops.acceptedCurrencies.forEach { currency ->
                if (remaining <= 0) return@forEach
                
                val itemId = Identifier.tryParse(currency.item) ?: return@forEach
                val item = net.minecraft.registry.Registries.ITEM.get(itemId) ?: return@forEach
                
                for (i in 0 until player.inventory.size()) {
                    if (remaining <= 0) break
                    
                    val stack = player.inventory.getStack(i)
                    if (stack.item == item) {
                        val itemsNeeded = (remaining + currency.value - 1) / currency.value
                        val itemsToTake = itemsNeeded.coerceAtMost(stack.count)
                        val valueFromItems = itemsToTake * currency.value
                        
                        stack.decrement(itemsToTake)
                        remaining -= valueFromItems
                    }
                }
            }
        }
        
        return remaining <= 0
    }
    
    /**
     * Check if player has enough currency
     */
    fun hasEnoughCurrency(player: PlayerEntity, amount: Int): Boolean {
        return getPlayerCurrency(player) >= amount
    }
}
