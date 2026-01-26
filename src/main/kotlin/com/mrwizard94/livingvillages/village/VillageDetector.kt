package com.mrwizard94.livingvillages.village

import com.mrwizard94.livingvillages.LivingVillages
import com.mrwizard94.livingvillages.util.NameGenerator
import net.minecraft.block.BedBlock
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.gen.structure.Structure
import java.util.UUID

/**
 * Village Detector
 * 
 * Detects existing vanilla villages in the world and converts them
 * to Living Villages format. Scans for village structures, beds,
 * and villagers to determine village boundaries and properties.
 * 
 * Based on: LIVING_VILLAGES_CODEX.md - Village Detection System
 */
object VillageDetector {
    
    /**
     * Detect all villages in loaded chunks
     * Called on world load or periodically
     */
    fun detectVillages(world: ServerWorld) {
        LivingVillages.log("Starting village detection...")
        
        val detectedCount = mutableListOf<VillageData>()
        
        // Find villages by scanning for villager clusters within loaded area
        val center = world.spawnPos
        val scanRadius = 200.0
        val scanBox = Box(
            center.x - scanRadius,
            world.bottomY.toDouble(),
            center.z - scanRadius,
            center.x + scanRadius,
            world.topY.toDouble(),
            center.z + scanRadius
        )
        val villagers = world.getEntitiesByClass(VillagerEntity::class.java, scanBox) { true }
        val villageClusters = findVillageClusters(villagers, world)
        
        villageClusters.forEach { cluster ->
            try {
                val village = analyzeCluster(world, cluster)
                if (village != null) {
                    // Check if village already exists
                    val existing = VillageRegistry.findNearestVillage(village.centerPos, 50.0)
                    if (existing == null) {
                        detectedCount.add(village)
                        VillageRegistry.registerVillage(village)
                        LivingVillages.log("Detected village: ${village.name} at ${village.centerPos}")
                        
                        // Generate shops for detected village
                        com.mrwizard94.livingvillages.shop.ShopGenerator.generateShopsForVillage(village, world)
                    }
                }
            } catch (e: Exception) {
                LivingVillages.error("Failed to analyze village cluster", e)
            }
        }
        
        LivingVillages.log("Village detection complete: Found ${detectedCount.size} new villages")
    }
    
    /**
     * Find clusters of villagers that likely form villages
     */
    private fun findVillageClusters(
        villagers: List<VillagerEntity>,
        world: ServerWorld
    ): List<VillageCluster> {
        val clusters = mutableListOf<VillageCluster>()
        val processed = mutableSetOf<VillagerEntity>()
        
        villagers.forEach { villager ->
            if (villager in processed) return@forEach
            
            // Find all villagers within 64 blocks
            val nearbyVillagers = villagers.filter { other ->
                villager.squaredDistanceTo(other) < 64 * 64 && other !in processed
            }
            
            if (nearbyVillagers.size >= 3) { // Minimum for a village
                processed.addAll(nearbyVillagers)
                clusters.add(VillageCluster(nearbyVillagers, world))
            }
        }
        
        return clusters
    }
    
    /**
     * Village cluster data
     */
    private data class VillageCluster(
        val villagers: List<VillagerEntity>,
        val world: ServerWorld
    )
    
    /**
     * Analyze a villager cluster and create VillageData
     */
    private fun analyzeCluster(world: ServerWorld, cluster: VillageCluster): VillageData? {
        // Calculate bounding box from villager positions
        val positions = cluster.villagers.map { it.blockPos }
        val minX = positions.minOf { it.x } - 32
        val maxX = positions.maxOf { it.x } + 32
        val minZ = positions.minOf { it.z } - 32
        val maxZ = positions.maxOf { it.z } + 32
        
        val boundingBox = BlockBox(minX, world.bottomY, minZ, maxX, world.topY, maxZ)
        
        // Calculate center position
        val centerX = (boundingBox.minX + boundingBox.maxX) / 2
        val centerZ = (boundingBox.minZ + boundingBox.maxZ) / 2
        val centerY = world.getTopY(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, centerX, centerZ)
        val centerPos = BlockPos(centerX, centerY, centerZ)
        
        // Calculate radius (half the diagonal of bounding box)
        val width = boundingBox.maxX - boundingBox.minX
        val depth = boundingBox.maxZ - boundingBox.minZ
        val radius = (kotlin.math.sqrt((width * width + depth * depth).toDouble()) / 2).toInt()
        
        // Get biome at center (use default if registry access is not stable in current mappings)
        val biomeId = try {
            val biome = world.getBiome(centerPos)
            biome.toString()
        } catch (e: Exception) {
            "minecraft:plains"
        }
        
        // Count beds and villagers in area
        val bedCount = countBeds(world, boundingBox)
        val villagerCount = countVillagers(world, boundingBox)
        
        // Analyze buildings in area
        val buildings = analyzeBuildings(world, boundingBox)
        
        // Generate village name
        val villageName = NameGenerator.generateVillageName(biomeId)
        
        // Create village data
        val village = VillageData(
            uuid = UUID.randomUUID(),
            name = villageName,
            biomeType = biomeId,
            centerPos = centerPos,
            radius = radius.coerceAtLeast(32).coerceAtMost(128), // Clamp radius
            population = villagerCount,
            tier = calculateTier(bedCount, villagerCount),
            prosperityScore = 50.0, // Default starting prosperity
            safetyLevel = 1.0, // Assume safe initially
            resources = VillageResources(), // Start with no resources
            buildings = buildings.toMutableList(),
            constructionQueue = mutableListOf(),
            mayorData = null,
            personality = determinePersonality(biomeId, buildings),
            expansionTimer = 0,
            lastExpansionTime = world.time,
            flags = mutableMapOf()
        )
        
        return village
    }
    
    /**
     * Count beds in the village area
     */
    private fun countBeds(world: ServerWorld, boundingBox: BlockBox): Int {
        var count = 0
        
        for (x in boundingBox.minX..boundingBox.maxX) {
            for (z in boundingBox.minZ..boundingBox.maxZ) {
                for (y in boundingBox.minY..boundingBox.maxY) {
                    val pos = BlockPos(x, y, z)
                    val state = world.getBlockState(pos)
                    
                    if (state.block is BedBlock) {
                        // Only count head of bed (use string check to avoid mapping issues)
                        if (state.get(BedBlock.PART).toString().equals("head", ignoreCase = true)) {
                            count++
                        }
                    }
                }
            }
        }
        
        return count
    }
    
    /**
     * Count villagers in the village area
     */
    private fun countVillagers(world: ServerWorld, boundingBox: BlockBox): Int {
        val box = Box(
            boundingBox.minX.toDouble(),
            boundingBox.minY.toDouble(),
            boundingBox.minZ.toDouble(),
            boundingBox.maxX.toDouble(),
            boundingBox.maxY.toDouble(),
            boundingBox.maxZ.toDouble()
        )
        
        val villagers = world.getEntitiesByClass(VillagerEntity::class.java, box) { true }
        return villagers.size
    }
    
    /**
     * Analyze buildings in the village area
     * Scans for distinct building structures based on bed clusters
     */
    private fun analyzeBuildings(
        world: ServerWorld,
        boundingBox: BlockBox
    ): List<VillageBuilding> {
        val buildings = mutableListOf<VillageBuilding>()
        
        // Find bed clusters (each cluster = one building)
        val beds = findBedsInArea(world, boundingBox)
        val bedClusters = clusterBeds(beds, world)
        
        bedClusters.forEach { cluster ->
            val clusterBox = calculateClusterBox(cluster)
            
            // Determine building type
            val buildingType = determineBuildingType(world, clusterBox)
            
            // Calculate position (center of cluster)
            val centerX = (clusterBox.minX + clusterBox.maxX) / 2
            val centerZ = (clusterBox.minZ + clusterBox.maxZ) / 2
            val centerY = clusterBox.minY
            val position = BlockPos(centerX, centerY, centerZ)
            
            // Count beds and job sites in this building
            val bedCount = cluster.size
            val jobSites = countJobSitesInArea(world, clusterBox)
            
            val building = VillageBuilding(
                templateId = "minecraft:vanilla_${buildingType}",
                position = position,
                rotation = 0,
                isBuilt = true,
                bedCount = bedCount,
                jobSiteCount = jobSites
            )
            
            buildings.add(building)
        }
        
        return buildings
    }
    
    /**
     * Find all beds in an area
     */
    private fun findBedsInArea(world: ServerWorld, box: BlockBox): List<BlockPos> {
        val beds = mutableListOf<BlockPos>()
        
        for (x in box.minX..box.maxX) {
            for (z in box.minZ..box.maxZ) {
                for (y in box.minY..box.maxY) {
                    val pos = BlockPos(x, y, z)
                    val state = world.getBlockState(pos)
                    if (state.block is BedBlock && state.get(BedBlock.PART).toString().equals("head", ignoreCase = true)) {
                        beds.add(pos)
                    }
                }
            }
        }
        
        return beds
    }
    
    /**
     * Cluster beds that are close together (same building)
     */
    private fun clusterBeds(beds: List<BlockPos>, world: ServerWorld): List<List<BlockPos>> {
        val clusters = mutableListOf<List<BlockPos>>()
        val processed = mutableSetOf<BlockPos>()
        
        beds.forEach { bed ->
            if (bed in processed) return@forEach
            
            // Find all beds within 8 blocks (same building)
            val cluster = beds.filter { other ->
                bed.getSquaredDistance(other) < 64 && other !in processed
            }
            
            if (cluster.isNotEmpty()) {
                processed.addAll(cluster)
                clusters.add(cluster)
            }
        }
        
        return clusters
    }
    
    /**
     * Calculate bounding box for a bed cluster
     */
    private fun calculateClusterBox(cluster: List<BlockPos>): BlockBox {
        val minX = cluster.minOf { it.x } - 3
        val maxX = cluster.maxOf { it.x } + 3
        val minZ = cluster.minOf { it.z } - 3
        val maxZ = cluster.maxOf { it.z } + 3
        val minY = cluster.minOf { it.y } - 2
        val maxY = cluster.maxOf { it.y } + 5
        
        return BlockBox(minX, minY, minZ, maxX, maxY, maxZ)
    }
    
    /**
     * Determine building type from structure piece
     */
    private fun determineBuildingType(world: ServerWorld, box: BlockBox): String {
        // Simple heuristic: check for specific blocks
        var hasBed = false
        var hasCraftingTable = false
        var hasFurnace = false
        
        for (x in box.minX..box.maxX) {
            for (z in box.minZ..box.maxZ) {
                for (y in box.minY..box.maxY) {
                    val pos = BlockPos(x, y, z)
                    val state = world.getBlockState(pos)
                    
                    if (state.block is BedBlock) hasBed = true
                    if (state.block == Blocks.CRAFTING_TABLE) hasCraftingTable = true
                    if (state.block == Blocks.FURNACE || state.block == Blocks.BLAST_FURNACE || state.block == Blocks.SMOKER) {
                        hasFurnace = true
                    }
                }
            }
        }
        
        return when {
            hasBed && hasCraftingTable -> "house"
            hasFurnace -> "smithy"
            hasCraftingTable -> "workshop"
            else -> "building"
        }
    }
    
    /**
     * Count beds in a specific area
     */
    private fun countBedsInArea(world: ServerWorld, box: BlockBox): Int {
        var count = 0
        for (x in box.minX..box.maxX) {
            for (z in box.minZ..box.maxZ) {
                for (y in box.minY..box.maxY) {
                    val pos = BlockPos(x, y, z)
                    val state = world.getBlockState(pos)
                    if (state.block is BedBlock && state.get(BedBlock.PART).toString().equals("head", ignoreCase = true)) {
                        count++
                    }
                }
            }
        }
        return count
    }
    
    /**
     * Count job sites in a specific area
     */
    private fun countJobSitesInArea(world: ServerWorld, box: BlockBox): Int {
        var count = 0
        val jobSiteBlocks = setOf(
            Blocks.COMPOSTER,
            Blocks.BARREL,
            Blocks.BLAST_FURNACE,
            Blocks.SMOKER,
            Blocks.CARTOGRAPHY_TABLE,
            Blocks.FLETCHING_TABLE,
            Blocks.LECTERN,
            Blocks.LOOM,
            Blocks.STONECUTTER,
            Blocks.SMITHING_TABLE,
            Blocks.GRINDSTONE
        )
        
        for (x in box.minX..box.maxX) {
            for (z in box.minZ..box.maxZ) {
                for (y in box.minY..box.maxY) {
                    val pos = BlockPos(x, y, z)
                    val state = world.getBlockState(pos)
                    if (state.block in jobSiteBlocks) {
                        count++
                    }
                }
            }
        }
        return count
    }
    
    /**
     * Calculate village tier based on size
     */
    private fun calculateTier(bedCount: Int, villagerCount: Int): Int {
        return when {
            bedCount >= 50 && villagerCount >= 100 -> 5
            bedCount >= 36 && villagerCount >= 60 -> 4
            bedCount >= 21 && villagerCount >= 40 -> 3
            bedCount >= 13 && villagerCount >= 20 -> 2
            bedCount >= 8 && villagerCount >= 10 -> 1
            else -> 0
        }
    }
    
    /**
     * Determine village personality based on biome and buildings
     */
    private fun determinePersonality(biomeId: String, buildings: List<VillageBuilding>): VillagePersonality {
        // Count building types
        val houseCount = buildings.count { it.templateId.contains("house") }
        val farmCount = buildings.count { it.templateId.contains("farm") }
        val shopCount = buildings.count { it.templateId.contains("shop") }
        
        return when {
            farmCount > houseCount -> VillagePersonality.AGRARIAN
            shopCount > houseCount -> VillagePersonality.TRADER
            biomeId.contains("plains") || biomeId.contains("meadow") -> VillagePersonality.AGRARIAN
            biomeId.contains("desert") || biomeId.contains("savanna") -> VillagePersonality.TRADER
            biomeId.contains("taiga") || biomeId.contains("snow") -> VillagePersonality.FORTIFIED
            else -> VillagePersonality.BALANCED
        }
    }
}
