package com.mrwizard94.livingvillages.building

import com.mrwizard94.livingvillages.LivingVillages
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.property.Property
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

/**
 * Building Placer
 * 
 * Handles placement of building templates in the world.
 * Converts template definitions into actual block placements.
 * 
 * Based on: LIVING_VILLAGES_CODEX.md - BuildingPlacer
 */
object BuildingPlacer {
    
    /**
     * Place a building template at a position
     * 
     * @param world The world to place in
     * @param template The template to place
     * @param origin The origin position (bottom corner)
     * @param rotation Rotation in degrees (0, 90, 180, 270)
     * @return Number of blocks placed
     */
    fun placeStructure(
        world: ServerWorld,
        template: BuildingTemplate,
        origin: BlockPos,
        rotation: Int = 0
    ): Int {
        var placedCount = 0
        
        try {
            // Place all blocks
            template.blocks.forEach { blockPlacement ->
                val blockPos = rotatePosition(blockPlacement.getBlockPos(), rotation).add(origin)
                
                try {
                    val blockState = parseBlockState(blockPlacement.block, blockPlacement.properties)
                    if (blockState != null) {
                        world.setBlockState(blockPos, blockState)
                        placedCount++
                    } else {
                        LivingVillages.error("Failed to parse block: ${blockPlacement.block}")
                    }
                } catch (e: Exception) {
                    LivingVillages.error("Failed to place block at $blockPos", e)
                }
            }
            
            // Place entities (if any)
            template.entities.forEach { entityPlacement ->
                // TODO: Implement entity placement
                // This would require entity type registry and NBT parsing
            }
            
            LivingVillages.log("Placed structure ${template.templateId} at $origin ($placedCount blocks)")
            
        } catch (e: Exception) {
            LivingVillages.error("Failed to place structure ${template.templateId}", e)
        }
        
        return placedCount
    }
    
    /**
     * Place a single block
     */
    fun placeBlock(world: ServerWorld, pos: BlockPos, blockId: String, properties: Map<String, String> = emptyMap()) {
        val blockState = parseBlockState(blockId, properties)
        if (blockState != null) {
            world.setBlockState(pos, blockState)
        }
    }
    
    /**
     * Parse block ID and properties into BlockState
     */
    fun parseBlockState(blockId: String, properties: Map<String, String>): BlockState? {
        try {
            val identifier = Identifier.tryParse(blockId) ?: return null
            val block = Registries.BLOCK.get(identifier) ?: return null
            
            var state = block.defaultState
            
            // Apply properties
            properties.forEach { (key, value) ->
                state = applyProperty(state, key, value) ?: return@forEach
            }
            
            return state
        } catch (e: Exception) {
            LivingVillages.error("Failed to parse block state: $blockId", e)
            return null
        }
    }
    
    /**
     * Apply a property to a block state
     */
    private fun applyProperty(state: BlockState, key: String, value: String): BlockState? {
        val property = state.properties.find { it.name == key } ?: return null
        
        return when (property) {
            is net.minecraft.state.property.BooleanProperty -> {
                val boolValue = value.toBooleanStrictOrNull() ?: return null
                state.with(property, boolValue)
            }
            is net.minecraft.state.property.IntProperty -> {
                val intValue = value.toIntOrNull() ?: return null
                state.with(property, intValue)
            }
            is net.minecraft.state.property.EnumProperty<*> -> {
                try {
                    val enumConstants = property.type.enumConstants
                    val enumValue = enumConstants?.firstOrNull { e -> e.toString().equals(value, ignoreCase = true) } ?: return null
                    return withPropertySafeReflect(state, property, enumValue as Any)
                } catch (e: Exception) {
                    return null
                }
            }
            is net.minecraft.state.property.DirectionProperty -> {
                val direction = Direction.byName(value) ?: return null
                state.with(property, direction)
            }
            else -> null
        }
    }

    /**
     * Safely call BlockState.with via reflection to avoid generic inference issues with Property<T>
     */
    private fun withPropertySafeReflect(state: BlockState, property: net.minecraft.state.property.Property<*>, value: Any): BlockState? {
        return try {
            val method = state::class.java.methods.firstOrNull { it.name == "with" && it.parameterTypes.size == 2 }
            method?.invoke(state, property, value) as? BlockState
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Rotate a position around origin based on rotation angle
     */
    private fun rotatePosition(pos: BlockPos, rotation: Int): BlockPos {
        return when (rotation) {
            0 -> pos
            90 -> BlockPos(-pos.z, pos.y, pos.x)
            180 -> BlockPos(-pos.x, pos.y, -pos.z)
            270 -> BlockPos(pos.z, pos.y, -pos.x)
            else -> pos // Invalid rotation, return as-is
        }
    }
    
    /**
     * Check if a position is safe to place a block
     */
    fun isSafeToPlace(world: ServerWorld, pos: BlockPos): Boolean {
        val state = world.getBlockState(pos)
        return state.isAir || state.isReplaceable
    }
}
