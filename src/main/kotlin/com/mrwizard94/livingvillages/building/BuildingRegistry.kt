package com.mrwizard94.livingvillages.building

import com.mrwizard94.livingvillages.LivingVillages
import java.util.concurrent.ConcurrentHashMap

/**
 * Building Template Registry
 * 
 * In-memory catalog of all available building templates.
 * Templates are loaded from JSON files and registered here.
 */
object BuildingRegistry {
    
    private val templates = ConcurrentHashMap<String, BuildingTemplate>()
    
    /**
     * Register a building template
     */
    fun registerTemplate(id: String, template: BuildingTemplate) {
        templates[id] = template
    }
    
    /**
     * Get template by ID
     */
    fun getTemplate(id: String): BuildingTemplate? {
        return templates[id]
    }
    
    /**
     * Get all templates
     */
    fun getAllTemplates(): Collection<BuildingTemplate> {
        return templates.values
    }
    
    /**
     * Get templates by category
     */
    fun getTemplatesByCategory(category: String): List<BuildingTemplate> {
        return templates.values.filter { it.category == category }
    }
    
    /**
     * Get templates valid for biome and tier
     */
    fun getValidTemplates(biomeId: String, tier: Int): List<BuildingTemplate> {
        return templates.values.filter { template ->
            template.isValidForBiome(biomeId) && template.isValidForTier(tier)
        }
    }
    
    /**
     * Get templates by category, biome, and tier
     */
    fun getTemplates(category: String, biomeId: String, tier: Int): List<BuildingTemplate> {
        return templates.values.filter { template ->
            template.category == category &&
            template.isValidForBiome(biomeId) &&
            template.isValidForTier(tier)
        }
    }
    
    /**
     * Get random template matching criteria
     */
    fun getRandomTemplate(
        category: String? = null,
        biomeId: String? = null,
        tier: Int? = null,
        random: java.util.Random = java.util.Random()
    ): BuildingTemplate? {
        val candidates = when {
            category != null && biomeId != null && tier != null -> 
                getTemplates(category, biomeId, tier)
            category != null -> 
                getTemplatesByCategory(category)
            biomeId != null && tier != null -> 
                getValidTemplates(biomeId, tier)
            else -> 
                getAllTemplates().toList()
        }
        
        if (candidates.isEmpty()) return null
        
        // Weight by spawnWeight
        val totalWeight = candidates.sumOf { it.spawnWeight }
        var randomWeight = random.nextInt(totalWeight)
        
        candidates.forEach { template ->
            randomWeight -= template.spawnWeight
            if (randomWeight < 0) {
                return template
            }
        }
        
        return candidates.first()
    }
    
    /**
     * Clear all templates (called on resource reload)
     */
    fun clear() {
        templates.clear()
    }
    
    /**
     * Get template count
     */
    fun getTemplateCount(): Int {
        return templates.size
    }
    
    /**
     * Log registry statistics
     */
    fun logStatistics() {
        val byCategory = templates.values.groupingBy { it.category }.eachCount()
        val byTier = templates.values.groupingBy { it.tierRequirement }.eachCount()
        
        LivingVillages.log("=== Building Template Registry ===")
        LivingVillages.log("Total Templates: ${templates.size}")
        LivingVillages.log("By Category: $byCategory")
        LivingVillages.log("By Tier Requirement: $byTier")
        LivingVillages.log("===================================")
    }
}
