package com.mrwizard94.livingvillages.village

import com.mrwizard94.livingvillages.LivingVillages
import com.mrwizard94.livingvillages.building.BuildingRegistry
import com.mrwizard94.livingvillages.building.BuildingTemplate
import com.mrwizard94.livingvillages.config.LVConfig
import com.mrwizard94.livingvillages.util.TerrainScanner
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

/**
 * Village Expansion Engine
 * 
 * The heart of village growth. Handles expansion cycles, building selection,
 * and construction queueing.
 * 
 * Based on: LIVING_VILLAGES_CODEX.md - Village Expansion Engine
 */
object VillageExpansionEngine {
    
    /**
     * Tick method - Called every server tick for active villages
     * 
     * TICK CYCLE:
     * 1. Check if expansion timer has elapsed
     * 2. Validate expansion conditions (safety, resources, population)
     * 3. Select building type based on village needs
     * 4. Find suitable build location
     * 5. Queue construction task
     * 6. Reset expansion timer
     */
    fun tick(village: VillageData, world: ServerWorld) {
        // Check if expansion is enabled
        if (!LVConfig.expansion.enabled) {
            return
        }
        
        // Increment expansion timer
        village.incrementExpansionTimer()
        
        // Check if expansion interval reached
        val expansionInterval = LVConfig.expansion.expansionIntervalTicks
        if (village.expansionTimer < expansionInterval) {
            return
        }
        
        // Reset timer
        village.resetExpansionTimer()
        
        // VALIDATION PHASE
        if (!canExpand(village, world)) {
            return
        }
        
        // DECISION PHASE
        val buildingType = selectBuildingType(village, world)
        if (buildingType == null) {
            return
        }
        
        // LOCATION PHASE
        val buildSite = TerrainScanner.findBuildLocation(village, world, buildingType)
        if (buildSite == null) {
            LivingVillages.log("No suitable build location found for ${village.name}")
            return
        }
        
        // EXECUTION PHASE
        queueConstruction(village, buildingType, buildSite, world)
        
        // UPDATE VILLAGE STATE
        village.lastExpansionTime = world.time
        village.markDirty()
        
        LivingVillages.log("Queued construction for ${village.name}: ${buildingType.templateId} at $buildSite")
    }
    
    /**
     * Determines if village can expand
     * 
     * CHECKS:
     * - Village tier allows more buildings
     * - Safety level is acceptable
     * - Not currently under attack
     * - Has required resources (if enabled)
     * - Has available space within radius
     */
    private fun canExpand(village: VillageData, world: ServerWorld): Boolean {
        // Check building cap for current tier
        val currentBuildings = village.getBuildingCount()
        val tierKey = "tier_${village.tier}"
        val maxBuildings = LVConfig.expansion.maxBuildingsPerTier[tierKey] ?: 7
        
        if (currentBuildings >= maxBuildings) {
            return false
        }
        
        // Check safety level
        val safetyLevel = village.safetyLevel
        val safetyThreshold = LVConfig.defense.safetyThresholdForExpansion
        
        if (safetyLevel < safetyThreshold) {
            return false
        }
        
        // Check for active raid
        if (village.isUnderAttack()) {
            return false
        }
        
        // Check resources (if required)
        if (LVConfig.expansion.requireMaterials) {
            if (!village.hasMinimumResources()) {
                return false
            }
        }
        
        return true
    }
    
    /**
     * Selects what type of building to construct
     * 
     * PRIORITY SYSTEM:
     * 1. Housing (if beds < population + 2)
     * 2. Profession buildings (if missing key professions)
     * 3. Infrastructure (paths, lights, decorations)
     * 4. Specialized (based on village personality)
     */
    private fun selectBuildingType(village: VillageData, world: ServerWorld): BuildingTemplate? {
        val population = village.population
        val beds = village.getBedCount()
        val biomeId = village.biomeType
        
        // URGENT: Need more housing
        if (beds < population + 2) {
            return selectHouseTemplate(village, biomeId)
        }
        
        // Check profession needs
        val missingProfessions = village.getMissingProfessions()
        if (missingProfessions.isNotEmpty()) {
            return selectProfessionBuilding(village, biomeId, missingProfessions)
        }
        
        // Infrastructure needs
        if (village.needsMorePaths()) {
            // TODO: Path extension template
            return null
        }
        
        if (village.needsMoreLighting()) {
            // TODO: Lamp post template
            return null
        }
        
        // Personality-driven selection
        val building = selectPersonalityBuilding(village, biomeId)
        
        // If trader personality, occasionally generate shops instead
        if (village.personality == VillagePersonality.TRADER && 
            world.random.nextInt(10) < 2) { // 20% chance
            val shopCount = com.mrwizard94.livingvillages.shop.ShopRegistry.getShopsNear(
                village.centerPos, 
                village.radius.toDouble()
            ).size
            
            // Generate shops if village needs more
            if (shopCount < 3) {
                com.mrwizard94.livingvillages.shop.ShopGenerator.generateShopsForVillage(village, world)
            }
        }
        
        return building
    }
    
    /**
     * Select a house template
     */
    private fun selectHouseTemplate(village: VillageData, biomeId: String): BuildingTemplate? {
        return BuildingRegistry.getRandomTemplate(
            category = "house",
            biomeId = biomeId,
            tier = village.tier
        )
    }
    
    /**
     * Select a profession building
     */
    private fun selectProfessionBuilding(
        village: VillageData,
        biomeId: String,
        missingProfessions: List<String>
    ): BuildingTemplate? {
        // For now, just get any profession building
        // TODO: Match specific profession to building type
        return BuildingRegistry.getRandomTemplate(
            category = "profession",
            biomeId = biomeId,
            tier = village.tier
        )
    }
    
    /**
     * Select building based on village personality
     */
    private fun selectPersonalityBuilding(village: VillageData, biomeId: String): BuildingTemplate? {
        val category = when (village.personality) {
            VillagePersonality.AGRARIAN -> "farm"
            VillagePersonality.TRADER -> "shop"
            VillagePersonality.FORTIFIED -> "defensive"
            VillagePersonality.ARTISAN -> "profession"
            VillagePersonality.BALANCED -> {
                // Random category
                val categories = listOf("house", "farm", "decorative")
                categories.random()
            }
        }
        
        return BuildingRegistry.getRandomTemplate(
            category = category,
            biomeId = biomeId,
            tier = village.tier
        )
    }
    
    /**
     * Queue a construction task
     */
    private fun queueConstruction(
        village: VillageData,
        template: BuildingTemplate,
        buildSite: BlockPos,
        world: ServerWorld
    ) {
        // Flatten terrain if needed
        if (template.terrainAdaptation == "flatten") {
            TerrainScanner.flattenTerrain(world, buildSite, template.getWidth(), template.getDepth())
        }
        
        // Create build task
        val buildTask = BuildTask(
            templateId = template.templateId,
            targetPos = buildSite,
            progress = 0,
            totalBlocks = template.getTotalBlocks(),
            requiredMaterials = template.requiredMaterials
        )
        
        // Add to construction queue
        village.constructionQueue.add(buildTask)
        
        // Mark village as dirty for saving
        village.markDirty()
    }
    
    /**
     * Force expansion (for commands/debugging)
     */
    fun forceExpand(village: VillageData, world: ServerWorld) {
        village.resetExpansionTimer()
        tick(village, world)
    }
}
