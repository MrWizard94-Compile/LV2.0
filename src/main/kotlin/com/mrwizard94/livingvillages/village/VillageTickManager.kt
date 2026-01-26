package com.mrwizard94.livingvillages.village

import com.mrwizard94.livingvillages.building.ConstructionAnimation
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.world.ServerWorld

/**
 * Village Tick Manager
 * 
 * Handles per-tick updates for all villages.
 * Ticks expansion engine and construction animation.
 */
object VillageTickManager {
    
    private var tickCounter = 0
    
    /**
     * Register tick handlers
     */
    fun register() {
        ServerTickEvents.END_WORLD_TICK.register { world ->
            if (world is ServerWorld) {
                tick(world)
            }
        }
    }
    
    /**
     * Tick all villages in the world
     */
    private fun tick(world: ServerWorld) {
        // Only tick every N ticks for performance (configurable)
        tickCounter++
        val updateInterval = 20 // Tick every second (20 ticks)
        
        if (tickCounter < updateInterval) {
            return
        }
        
        tickCounter = 0
        
        // Tick all villages
        VillageRegistry.getAllVillages().forEach { village ->
            // Only tick villages in loaded chunks
            if (world.isChunkLoaded(village.centerPos.x shr 4, village.centerPos.z shr 4)) {
                // Tick expansion engine
                VillageExpansionEngine.tick(village, world)
                
                // Tick construction animation for queued builds
                village.constructionQueue.toList().forEach { task ->
                    ConstructionAnimation.tickConstruction(task, village, world)
                }
            }
        }
        
        // Tick shop restock system
        com.mrwizard94.livingvillages.shop.RestockSystem.tick(world)
    }
}
