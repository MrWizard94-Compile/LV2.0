package com.mrwizard94.livingvillages.building

import com.mrwizard94.livingvillages.LivingVillages
import com.mrwizard94.livingvillages.config.LVConfig
import com.mrwizard94.livingvillages.village.BuildTask
import com.mrwizard94.livingvillages.village.VillageData
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos

/**
 * Construction Animation
 * 
 * Handles gradual building placement over time.
 * Creates immersive construction experience with particles and sounds.
 * 
 * Based on: LIVING_VILLAGES_CODEX.md - Construction Animation System
 */
object ConstructionAnimation {
    
    /**
     * Tick construction for a build task
     * Places blocks gradually over time
     * 
     * VISUAL EFFECTS:
     * - Blocks place one by one
     * - Particle effects during placement
     * - Sound effects (hammer, saw, etc.)
     */
    fun tickConstruction(task: BuildTask, village: VillageData, world: ServerWorld) {
        val template = BuildingRegistry.getTemplate(task.templateId)
        if (template == null) {
            LivingVillages.error("Template not found for task: ${task.templateId}")
            return
        }
        
        val blocksPerTick = LVConfig.performance.constructionBlocksPerTick
        
        for (i in 0 until blocksPerTick) {
            if (task.isComplete()) {
                finishConstruction(task, village, world, template)
                return
            }
            
            // Get next block to place
            val nextBlock = getNextBlock(task, template)
            if (nextBlock == null) {
                // No more blocks to place
                task.progress = task.totalBlocks // Mark as complete
                finishConstruction(task, village, world, template)
                return
            }
            
            // Place the block
            val blockPos = nextBlock.getBlockPos().add(task.targetPos)
            val blockState = BuildingPlacer.parseBlockState(nextBlock.block, nextBlock.properties)
            
            if (blockState != null && BuildingPlacer.isSafeToPlace(world, blockPos)) {
                world.setBlockState(blockPos, blockState)
                
                // Visual feedback
                spawnConstructionParticles(world, blockPos)
                playConstructionSound(world, blockPos)
                
                // Update progress
                task.incrementProgress()
            }
        }
    }
    
    /**
     * Get next block to place from template
     */
    private fun getNextBlock(task: BuildTask, template: BuildingTemplate): BlockPlacement? {
        if (task.progress >= template.blocks.size) {
            return null
        }
        
        return template.blocks[task.progress]
    }
    
    /**
     * Finish construction
     */
    private fun finishConstruction(
        task: BuildTask,
        village: VillageData,
        world: ServerWorld,
        template: BuildingTemplate
    ) {
        // Remove from queue
        village.constructionQueue.remove(task)
        
        // Add to buildings list
        village.buildings.add(
            com.mrwizard94.livingvillages.village.VillageBuilding(
                templateId = template.templateId,
                position = task.targetPos,
                rotation = 0,
                isBuilt = true,
                bedCount = template.beds,
                jobSiteCount = template.jobSites
            )
        )
        
        // Update village stats
        village.prosperityScore = (village.prosperityScore + 5.0).coerceAtMost(100.0)
        village.markDirty()
        
        // Celebration effects
        spawnCompletionEffects(world, task.targetPos)
        
        LivingVillages.log("Construction completed: ${template.name} at ${task.targetPos} in ${village.name}")
    }
    
    /**
     * Spawn construction particles
     */
    private fun spawnConstructionParticles(world: ServerWorld, pos: BlockPos) {
        world.spawnParticles(
            ParticleTypes.SMOKE,
            pos.x + 0.5,
            pos.y + 0.5,
            pos.z + 0.5,
            5,
            0.2,
            0.2,
            0.2,
            0.01
        )
    }
    
    /**
     * Play construction sound
     */
    private fun playConstructionSound(world: ServerWorld, pos: BlockPos) {
        val sounds = listOf(
            SoundEvents.BLOCK_WOOD_PLACE,
            SoundEvents.BLOCK_STONE_PLACE,
            SoundEvents.ENTITY_ITEM_FRAME_PLACE
        )
        
        val sound = sounds.random()
        val pitch = 0.8f + world.random.nextFloat() * 0.4f
        
        world.playSound(
            null,
            pos,
            sound,
            SoundCategory.BLOCKS,
            0.6f,
            pitch
        )
    }
    
    /**
     * Spawn completion celebration effects
     */
    private fun spawnCompletionEffects(world: ServerWorld, pos: BlockPos) {
        // Happy villager particles
        for (i in 0..10) {
            world.spawnParticles(
                ParticleTypes.HAPPY_VILLAGER,
                pos.x + 0.5,
                pos.y + 1.0,
                pos.z + 0.5,
                1,
                0.3,
                0.3,
                0.3,
                0.0
            )
        }
        
        // Play success sound
        world.playSound(
            null,
            pos,
            SoundEvents.ENTITY_VILLAGER_CELEBRATE,
            SoundCategory.NEUTRAL,
            1.0f,
            1.0f
        )
    }
}
