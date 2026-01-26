package com.mrwizard94.livingvillages.util

import com.mrwizard94.livingvillages.building.BuildingTemplate
import com.mrwizard94.livingvillages.village.VillageData
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.fluid.Fluids
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Terrain Scanner
 * 
 * Finds suitable build locations for village expansion.
 * Uses spiral search pattern to find flat, clear areas.
 * 
 * Based on: LIVING_VILLAGES_CODEX.md - TerrainScanner
 */
object TerrainScanner {
    
    private const val MAX_HEIGHT_DIFF = 2
    private const val MIN_SPACING = 5
    
    /**
     * Find a suitable build location for a template
     * 
     * ALGORITHM:
     * 1. Spiral outward from village center
     * 2. Check each position for flatness
     * 3. Verify no existing structures
     * 4. Ensure proper spacing from other buildings
     * 5. Return first valid position
     */
    fun findBuildLocation(
        village: VillageData,
        world: ServerWorld,
        template: BuildingTemplate
    ): BlockPos? {
        val center = village.centerPos
        val searchRadius = village.radius
        val requiredWidth = template.getWidth()
        val requiredDepth = template.getDepth()
        
        // Spiral search pattern
        for (radius in 10..searchRadius step 5) {
            for (angle in 0 until 360 step 15) {
                // Calculate position
                val radians = Math.toRadians(angle.toDouble())
                val x = (center.x + radius * cos(radians)).toInt()
                val z = (center.z + radius * sin(radians)).toInt()
                
                val surfaceY = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z)
                val testPos = BlockPos(x, surfaceY, z)
                
                // Check if valid
                if (isValidBuildSite(world, testPos, requiredWidth, requiredDepth, village)) {
                    return testPos
                }
            }
        }
        
        return null // No valid location found
    }
    
    /**
     * Check if a position is valid for building
     */
    private fun isValidBuildSite(
        world: ServerWorld,
        pos: BlockPos,
        width: Int,
        depth: Int,
        village: VillageData
    ): Boolean {
        // Check flatness
        if (!isFlatEnough(world, pos, width, depth)) {
            return false
        }
        
        // Check for water/lava
        if (hasFluidInArea(world, pos, width, depth)) {
            return false
        }
        
        // Check for existing structures
        if (hasStructuresNearby(world, pos, MIN_SPACING)) {
            return false
        }
        
        // Check spacing from other village buildings
        for (building in village.buildings) {
            val distance = pos.getSquaredDistance(building.position)
            if (distance < MIN_SPACING * MIN_SPACING) {
                return false
            }
        }
        
        return true
    }
    
    /**
     * Check if area is flat enough for building
     */
    private fun isFlatEnough(world: ServerWorld, pos: BlockPos, width: Int, depth: Int): Boolean {
        val baseHeight = pos.y
        
        // Check all positions in area
        for (x in 0 until width) {
            for (z in 0 until depth) {
                val checkPos = pos.add(x, 0, z)
                val height = world.getTopY(Heightmap.Type.WORLD_SURFACE, checkPos.x, checkPos.z)
                
                if (abs(height - baseHeight) > MAX_HEIGHT_DIFF) {
                    return false
                }
            }
        }
        
        return true
    }
    
    /**
     * Check if area has fluids (water/lava)
     */
    private fun hasFluidInArea(world: ServerWorld, pos: BlockPos, width: Int, depth: Int): Boolean {
        for (x in 0 until width) {
            for (z in 0 until depth) {
                val checkPos = pos.add(x, 0, z)
                val state = world.getBlockState(checkPos)
                
                // Check if block is fluid or has fluid above
                if (!state.fluidState.isEmpty && state.fluidState.fluid != Fluids.EMPTY) {
                    return true
                }
                
                // Check block above
                val above = world.getBlockState(checkPos.up())
                if (!above.fluidState.isEmpty && above.fluidState.fluid != Fluids.EMPTY) {
                    return true
                }
            }
        }
        
        return false
    }
    
    /**
     * Check if there are existing structures nearby
     */
    private fun hasStructuresNearby(world: ServerWorld, pos: BlockPos, minSpacing: Int): Boolean {
        // Check for non-natural blocks in area
        for (x in -minSpacing..minSpacing) {
            for (z in -minSpacing..minSpacing) {
                if (x == 0 && z == 0) continue
                
                val checkPos = pos.add(x, 0, z)
                val state = world.getBlockState(checkPos)
                
                // Skip air, grass, dirt, stone (natural blocks)
                if (state.isAir || 
                    state.block == Blocks.GRASS_BLOCK ||
                    state.block == Blocks.DIRT ||
                    state.block == Blocks.STONE) {
                    continue
                }
                
                // If we find non-natural blocks, might be a structure
                // This is a simple heuristic - could be improved
                if (state.block != Blocks.WATER && 
                    state.block != Blocks.LAVA &&
                    !state.isAir) {
                    return true
                }
            }
        }
        
        return false
    }
    
    /**
     * Flatten terrain for building (if terrain adaptation is enabled)
     */
    fun flattenTerrain(world: ServerWorld, pos: BlockPos, width: Int, depth: Int) {
        val baseHeight = pos.y
        
        for (x in 0 until width) {
            for (z in 0 until depth) {
                val checkPos = pos.add(x, 0, z)
                val currentHeight = world.getTopY(Heightmap.Type.WORLD_SURFACE, checkPos.x, checkPos.z)
                
                // Fill or remove blocks to match base height
                if (currentHeight < baseHeight) {
                    // Fill up
                    for (y in currentHeight until baseHeight) {
                        world.setBlockState(checkPos.withY(y), Blocks.DIRT.defaultState)
                    }
                } else if (currentHeight > baseHeight) {
                    // Remove down
                    for (y in baseHeight until currentHeight) {
                        world.setBlockState(checkPos.withY(y), Blocks.AIR.defaultState)
                    }
                }
            }
        }
        
        // Place grass on top
        for (x in 0 until width) {
            for (z in 0 until depth) {
                val grassPos = pos.add(x, 0, z)
                world.setBlockState(grassPos, Blocks.GRASS_BLOCK.defaultState)
            }
        }
    }
}
