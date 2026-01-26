package com.mrwizard94.livingvillages

import com.mrwizard94.livingvillages.config.LVConfig
import com.mrwizard94.livingvillages.village.VillageStorageManager
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.slf4j.LoggerFactory

/**
 * Living Villages 2.0
 * 
 * Transform static villages into living, breathing, evolving communities.
 * Built with Kotlin for clean, maintainable code.
 */
object LivingVillages : ModInitializer {
    const val MOD_ID = "livingvillages"
    const val MOD_NAME = "Living Villages"
    const val VERSION = "2.0.0"
    
    private val logger = LoggerFactory.getLogger(MOD_NAME)
    
    override fun onInitialize() {
        logger.info("🌟 Initializing $MOD_NAME v$VERSION")
        logger.info("🏘️  Bringing villages to life...")
        
        // Phase 1: Load Configuration
        log("Loading configuration...")
        LVConfig.load()
        
        // Phase 1: Register Server Lifecycle Events
        log("Registering server lifecycle events...")
        registerServerEvents()
        
        // Phase 2: Register Building Template Loader
        log("Registering building template loader...")
        com.mrwizard94.livingvillages.building.BuildingTemplateLoader.register()
        
        // Phase 3: Register Village Tick Manager
        log("Registering village tick manager...")
        com.mrwizard94.livingvillages.village.VillageTickManager.register()
        
        // Phase 4: Register Shop System
        log("Registering shop system...")
        com.mrwizard94.livingvillages.shop.ShopTypeRegistry.register()
        
        // TODO: Register additional systems
        // - Mayor System
        // - Reputation System
        // - Golem Repair
        // - UI Systems
        // - Commands
        
        logger.info("✅ $MOD_NAME initialized successfully!")
    }
    
    /**
     * Register server lifecycle events
     * Handles village data loading/saving
     */
    private fun registerServerEvents() {
        // Server start - load village data
        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            log("Server started, loading villages...")
            VillageStorageManager.onServerStart(server)
        }
        
        // Server stop - save village data
        ServerLifecycleEvents.SERVER_STOPPING.register { server ->
            log("Server stopping, saving villages...")
            VillageStorageManager.onServerStop(server)
        }
        
        log("Server lifecycle events registered")
    }
    
    fun log(message: String) {
        logger.info("[$MOD_NAME] $message")
    }
    
    fun error(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            logger.error("[$MOD_NAME] $message", throwable)
        } else {
            logger.error("[$MOD_NAME] $message")
        }
    }
}
