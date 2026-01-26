package com.mrwizard94.livingvillages.building

import kotlinx.serialization.Serializable
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

/**
 * Building Template
 * 
 * Defines a structure that can be placed in the world.
 * Templates are loaded from JSON files in data/living_villages/templates/
 * 
 * Based on: LIVING_VILLAGES_CODEX.md - Building Template Format
 */
@Serializable
data class BuildingTemplate(
    val templateId: String,
    val name: String,
    val category: String, // house, farm, shop, decorative, defensive
    val biomes: List<String> = emptyList(),
    val tierRequirement: Int = 0,
    val size: BuildingSize,
    val beds: Int = 0,
    val jobSites: Int = 0,
    val constructionTimeTicks: Int = 1200,
    val requiredMaterials: Map<String, Int> = emptyMap(),
    val blocks: List<BlockPlacement> = emptyList(),
    val entities: List<EntityPlacement> = emptyList(),
    val variants: List<String> = emptyList(),
    val rotationSupported: Boolean = true,
    val terrainAdaptation: String = "flatten", // flatten, none, adapt
    val minGroundClearance: Int = 0,
    val spawnWeight: Int = 10
) {
    fun getWidth(): Int = size.width
    fun getHeight(): Int = size.height
    fun getDepth(): Int = size.depth
    
    fun getTotalBlocks(): Int = blocks.size
    
    fun isValidForBiome(biomeId: String): Boolean {
        return biomes.isEmpty() || biomes.contains(biomeId)
    }
    
    fun isValidForTier(tier: Int): Boolean {
        return tier >= tierRequirement
    }
}

@Serializable
data class BuildingSize(
    val width: Int,
    val height: Int,
    val depth: Int
)

/**
 * Block placement definition
 * Position is relative to template origin (0,0,0)
 */
@Serializable
data class BlockPlacement(
    val pos: List<Int>, // [x, y, z]
    val block: String, // minecraft:oak_planks
    val properties: Map<String, String> = emptyMap()
) {
    fun getBlockPos(): BlockPos {
        return BlockPos(pos[0], pos[1], pos[2])
    }
}

/**
 * Entity placement definition
 */
@Serializable
data class EntityPlacement(
    val pos: List<Double>, // [x, y, z] with decimals
    val type: String, // minecraft:item_frame
    val nbt: Map<String, String> = emptyMap()
) {
    fun getPosition(): Triple<Double, Double, Double> {
        return Triple(pos[0], pos[1], pos[2])
    }
}
