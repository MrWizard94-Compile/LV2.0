package com.mrwizard94.livingvillages.shop

import com.mrwizard94.livingvillages.LivingVillages
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Formatting

/**
 * Shop Screen
 * 
 * GUI for purchasing items from shops.
 * 
 * Based on: LIVING_VILLAGES_CODEX.md - Shop UI
 * 
 * Note: This is a client-side screen. For server-side interaction,
 * you would use a ScreenHandler instead. This is a simplified version.
 */
class ShopScreen(
    private val shop: ShopData,
    private val player: PlayerEntity
) : Screen(Text.literal(ShopTypeRegistry.getShopType(shop.shopType)?.name ?: "Shop")) {
    
    private var scrollOffset = 0
    private val itemsPerPage = 6
    
    override fun init() {
        super.init()
        
        // Title
        val titleY = 10
        // Title is handled by super class
        
        // Currency display
        val currencyY = height - 30
        addDrawableChild(
            ButtonWidget.builder(
                Text.literal("§6Emeralds: ${CurrencyManager.getPlayerCurrency(player)}"),
                { }
            )
            .dimensions(width / 2 - 100, currencyY, 200, 20)
            .build()
        )
        
        // Item listings
        setupItemListings()
    }
    
    private fun setupItemListings() {
        val startY = 40
        val itemHeight = 60
        val startX = width / 2 - 120
        
        val visibleItems = shop.inventory.subList(
            scrollOffset,
            (scrollOffset + itemsPerPage).coerceAtMost(shop.inventory.size)
        )
        
        visibleItems.forEachIndexed { index, listing ->
            val y = startY + index * itemHeight
            
            // Item display (simplified - would use ItemRenderer in actual implementation)
            addDrawableChild(
                ButtonWidget.builder(
                    Text.literal(listing.getDisplayText()),
                    { purchase(listing, 1) }
                )
                .dimensions(startX, y, 200, 20)
                .build()
            )
            
            // Buy buttons
            addDrawableChild(
                ButtonWidget.builder(
                    Text.literal("Buy 1"),
                    { purchase(listing, 1) }
                )
                .dimensions(startX + 210, y, 50, 20)
                .build()
            )
            
            addDrawableChild(
                ButtonWidget.builder(
                    Text.literal("Buy Stack"),
                    { purchase(listing, listing.itemCount) }
                )
                .dimensions(startX + 265, y, 70, 20)
                .build()
            )
            
            addDrawableChild(
                ButtonWidget.builder(
                    Text.literal("Buy Max"),
                    { purchaseMax(listing) }
                )
                .dimensions(startX + 340, y, 60, 20)
                .build()
            )
        }
        
        // Scroll buttons
        if (scrollOffset > 0) {
            addDrawableChild(
                ButtonWidget.builder(
                    Text.literal("↑"),
                    { scrollOffset = (scrollOffset - itemsPerPage).coerceAtLeast(0); setupItemListings() }
                )
                .dimensions(width - 30, 40, 20, 20)
                .build()
            )
        }
        
        if (scrollOffset + itemsPerPage < shop.inventory.size) {
            addDrawableChild(
                ButtonWidget.builder(
                    Text.literal("↓"),
                    { scrollOffset += itemsPerPage; setupItemListings() }
                )
                .dimensions(width - 30, height - 50, 20, 20)
                .build()
            )
        }
    }
    
    private fun purchase(listing: ShopListing, quantity: Int) {
        // Calculate total cost
        val totalCost = listing.price * quantity
        
        // Check if player has enough currency
        if (!CurrencyManager.hasEnoughCurrency(player, totalCost)) {
            player.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0f, 1.0f)
            player.sendMessage(Text.literal("§cNot enough emeralds!").formatted(Formatting.RED), false)
            return
        }
        
        // Check if shop has enough stock
        val actualQuantity = quantity.coerceAtMost(listing.stock)
        if (actualQuantity <= 0) {
            player.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0f, 1.0f)
            player.sendMessage(Text.literal("§cOut of stock!").formatted(Formatting.RED), false)
            return
        }
        
        val actualCost = listing.price * actualQuantity
        
        // Deduct currency
        if (!CurrencyManager.deductCurrency(player, actualCost)) {
            player.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0f, 1.0f)
            player.sendMessage(Text.literal("§cPayment failed!").formatted(Formatting.RED), false)
            return
        }
        
        // Give items to player
        val itemStack = listing.createItemStack()
        if (itemStack != null) {
            itemStack.count = actualQuantity
            if (!player.inventory.insertStack(itemStack)) {
                // Drop if inventory is full
                player.dropItem(itemStack, false)
            }
        }
        
        // Reduce shop stock
        listing.reduceStock(actualQuantity)
        
        // Success feedback
        player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
        player.sendMessage(
            Text.literal("§aPurchased ${actualQuantity}x ${listing.itemId} for ${actualCost} emeralds")
                .formatted(Formatting.GREEN),
            false
        )
        
        // Refresh display
        clearChildren()
        init()
    }
    
    private fun purchaseMax(listing: ShopListing) {
        val maxAffordable = CurrencyManager.getPlayerCurrency(player) / listing.price
        val maxAvailable = listing.stock
        val maxPurchase = maxAffordable.coerceAtMost(maxAvailable)
        
        if (maxPurchase > 0) {
            purchase(listing, maxPurchase)
        } else {
            player.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1.0f, 1.0f)
        }
    }
    
    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context, mouseX, mouseY, delta)
        super.render(context, mouseX, mouseY, delta)
        
        // Draw shop type name
        val shopType = ShopTypeRegistry.getShopType(shop.shopType)
        val title = shopType?.signText ?: shop.shopType
        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal(title),
            width / 2,
            10,
            0xFFFFFF
        )
    }
}
