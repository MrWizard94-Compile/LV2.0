package com.mrwizard94.livingvillages.config

import kotlinx.serialization.Serializable

/**
 * Living Villages Configuration Data
 * 
 * This data class represents the complete mod configuration structure.
 * It's designed to be serialized/deserialized from JSON.
 * 
 * Default values are set here and can be overridden by config file.
 */
@Serializable
data class ModConfig(
    val villageExpansion: VillageExpansionConfig = VillageExpansionConfig(),
    val population: PopulationConfig = PopulationConfig(),
    val shopSystem: ShopSystemConfig = ShopSystemConfig(),
    val professions: ProfessionsConfig = ProfessionsConfig(),
    val naming: NamingConfig = NamingConfig(),
    val socialSystems: SocialSystemsConfig = SocialSystemsConfig(),
    val defense: DefenseConfig = DefenseConfig(),
    val ui: UIConfig = UIConfig(),
    val performance: PerformanceConfig = PerformanceConfig(),
    val compatibility: CompatibilityConfig = CompatibilityConfig(),
    val debug: DebugConfig = DebugConfig()
)

@Serializable
data class VillageExpansionConfig(
    val enabled: Boolean = true,
    val expansionIntervalTicks: Int = 24000,  // 20 minutes
    val maxVillageRadius: Int = 128,
    val requireMaterials: Boolean = true,
    val constructionSpeedMultiplier: Double = 1.0,
    val allowOffloadedExpansion: Boolean = false,
    val maxBuildingsPerTier: Map<String, Int> = mapOf(
        "tier_0" to 7,
        "tier_1" to 12,
        "tier_2" to 20,
        "tier_3" to 35,
        "tier_4" to 50,
        "tier_5" to 75
    )
)

@Serializable
data class PopulationConfig(
    val enableImmigration: Boolean = true,
    val immigrationIntervalTicks: Int = 12000,  // 10 minutes
    val requiredEmptyBeds: Int = 2,
    val requiredFoodPerImmigrant: Int = 64,
    val maxPopulationPerTier: Map<String, Int> = mapOf(
        "tier_0" to 10,
        "tier_1" to 20,
        "tier_2" to 40,
        "tier_3" to 60,
        "tier_4" to 100,
        "tier_5" to 150
    )
)

@Serializable
data class ShopSystemConfig(
    val enabled: Boolean = true,
    val shopsPerVillage: Map<String, Int> = mapOf(
        "small" to 1,
        "medium" to 2,
        "large" to 4
    ),
    val restockMode: String = "time_based",  // time_based, daily, never
    val restockIntervalTicks: Int = 24000,
    val defaultCurrency: String = "minecraft:emerald",
    val acceptedCurrencies: List<CurrencyEntry> = listOf(
        CurrencyEntry("minecraft:emerald", 1),
        CurrencyEntry("minecraft:emerald_block", 9)
    ),
    val priceMultiplier: Double = 1.0,
    val maxStockPerItem: Int = 64
)

@Serializable
data class CurrencyEntry(
    val item: String,
    val value: Int
)

@Serializable
data class ProfessionsConfig(
    val enableCustomProfessions: Boolean = true,
    val biomeSpecificProfessions: Boolean = true,
    val professionSpawnWeights: Map<String, Double> = mapOf(
        "miner" to 1.0,
        "hunter" to 1.0,
        "woodworker" to 1.5,
        "engineer" to 0.5
    )
)

@Serializable
data class NamingConfig(
    val villagersHaveNames: Boolean = true,
    val villagesHaveNames: Boolean = true,
    val childrenInheritLastName: Boolean = true,
    val disableNameTags: Boolean = true,
    val showNamesAboveHead: Boolean = true
)

@Serializable
data class SocialSystemsConfig(
    val enableRelationships: Boolean = true,
    val enableMoodSystem: Boolean = true,
    val enableHungerSystem: Boolean = false,
    val villagersBefriendMobs: Boolean = true
)

@Serializable
data class DefenseConfig(
    val enableGolemRepair: Boolean = true,
    val repairSpeedMultiplier: Double = 1.0,
    val pauseExpansionDuringRaids: Boolean = true,
    val safetyThresholdForExpansion: Double = 0.7
)

@Serializable
data class UIConfig(
    val showVillageNamesOnEntry: Boolean = true,
    val villageNameDisplayDurationTicks: Int = 60,
    val showEventNotifications: Boolean = true,
    val immersiveMessages: Boolean = true
)

@Serializable
data class PerformanceConfig(
    val maxActiveBuilders: Int = 5,
    val constructionBlocksPerTick: Int = 2,
    val aiUpdateInterval: Int = 10,
    val disableAiInTradingHalls: Boolean = false
)

@Serializable
data class CompatibilityConfig(
    val moreVillagersIntegration: Boolean = true,
    val economyModIntegration: Boolean = true,
    val detectModdedBiomes: Boolean = true
)

@Serializable
data class DebugConfig(
    val enableDebugCommands: Boolean = true,
    val logVillageActions: Boolean = false,
    val showConstructionBounds: Boolean = false
)
