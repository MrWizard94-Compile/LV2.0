package com.mrwizard94.livingvillages.config

import com.mrwizard94.livingvillages.LivingVillages
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

/**
 * Living Villages Configuration Manager
 * 
 * Handles loading, saving, and accessing mod configuration.
 * Config file location: config/living_villages.json
 * 
 * Usage:
 *   LVConfig.load()  // Load config on mod init
 *   LVConfig.config.villageExpansion.enabled  // Access values
 */
object LVConfig {
    
    private const val CONFIG_FILE_NAME = "living_villages.json"
    
    private val configPath: Path by lazy {
        FabricLoader.getInstance().configDir.resolve(CONFIG_FILE_NAME)
    }
    
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    /**
     * Current loaded configuration
     * Initialized with defaults, then overridden by file
     */
    var config: ModConfig = ModConfig()
        private set
    
    /**
     * Load configuration from file
     * Creates default config if file doesn't exist
     * Uses defaults if file is malformed
     */
    fun load() {
        try {
            if (!configPath.exists()) {
                LivingVillages.log("Config file not found, creating default config...")
                createDefault()
                return
            }
            
            val configText = configPath.readText()
            config = json.decodeFromString(configText)
            
            LivingVillages.log("Configuration loaded successfully")
            logConfigSummary()
            
        } catch (e: Exception) {
            LivingVillages.error("Failed to load config, using defaults", e)
            config = ModConfig()
            
            // Try to save defaults to fix corrupted file
            try {
                save()
                LivingVillages.log("Default config saved to fix corruption")
            } catch (saveError: Exception) {
                LivingVillages.error("Failed to save default config", saveError)
            }
        }
    }
    
    /**
     * Save current configuration to file
     */
    fun save() {
        try {
            val configText = json.encodeToString(config)
            configPath.writeText(configText)
            LivingVillages.log("Configuration saved successfully")
        } catch (e: Exception) {
            LivingVillages.error("Failed to save config", e)
        }
    }
    
    /**
     * Create default configuration file
     */
    private fun createDefault() {
        try {
            // Ensure config directory exists
            Files.createDirectories(configPath.parent)
            
            // Create default config
            config = ModConfig()
            
            // Save to file
            val configText = json.encodeToString(config)
            configPath.writeText(configText)
            
            LivingVillages.log("Default configuration created at: $configPath")
        } catch (e: Exception) {
            LivingVillages.error("Failed to create default config", e)
        }
    }
    
    /**
     * Log configuration summary (for debugging)
     */
    private fun logConfigSummary() {
        val exp = config.villageExpansion
        val pop = config.population
        val shop = config.shopSystem
        
        LivingVillages.log("=== Configuration Summary ===")
        LivingVillages.log("Village Expansion: ${if (exp.enabled) "Enabled" else "Disabled"}")
        LivingVillages.log("  - Expansion Interval: ${exp.expansionIntervalTicks} ticks")
        LivingVillages.log("  - Require Materials: ${exp.requireMaterials}")
        LivingVillages.log("Population Immigration: ${if (pop.enableImmigration) "Enabled" else "Disabled"}")
        LivingVillages.log("Shop System: ${if (shop.enabled) "Enabled" else "Disabled"}")
        LivingVillages.log("  - Restock Mode: ${shop.restockMode}")
        LivingVillages.log("Debug Commands: ${if (config.debug.enableDebugCommands) "Enabled" else "Disabled"}")
        LivingVillages.log("============================")
    }
    
    /**
     * Reload configuration from file
     * Useful for testing or in-game config changes
     */
    fun reload() {
        LivingVillages.log("Reloading configuration...")
        load()
    }
    
    // ========================================
    // Convenience accessor methods
    // ========================================
    
    /**
     * Quick access to expansion settings
     */
    val expansion: VillageExpansionConfig
        get() = config.villageExpansion
    
    /**
     * Quick access to population settings
     */
    val population: PopulationConfig
        get() = config.population
    
    /**
     * Quick access to shop settings
     */
    val shops: ShopSystemConfig
        get() = config.shopSystem
    
    /**
     * Quick access to naming settings
     */
    val naming: NamingConfig
        get() = config.naming
    
    /**
     * Quick access to debug settings
     */
    val debug: DebugConfig
        get() = config.debug

    /**
     * Quick access to performance settings
     */
    val performance: PerformanceConfig
        get() = config.performance

    /**
     * Quick access to defense settings
     */
    val defense: DefenseConfig
        get() = config.defense
    
    /**
     * Check if a specific feature is enabled
     */
    fun isFeatureEnabled(feature: String): Boolean {
        return when (feature) {
            "expansion" -> config.villageExpansion.enabled
            "immigration" -> config.population.enableImmigration
            "shops" -> config.shopSystem.enabled
            "custom_professions" -> config.professions.enableCustomProfessions
            "relationships" -> config.socialSystems.enableRelationships
            "mood" -> config.socialSystems.enableMoodSystem
            "golem_repair" -> config.defense.enableGolemRepair
            "debug_commands" -> config.debug.enableDebugCommands
            else -> false
        }
    }
}
