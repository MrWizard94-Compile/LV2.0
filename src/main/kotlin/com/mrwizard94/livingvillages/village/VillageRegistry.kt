package com.mrwizard94.livingvillages.village

import com.mrwizard94.livingvillages.LivingVillages
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Village Registry
 * 
 * In-memory tracking of all villages in the world.
 * Provides fast lookup and management of village data.
 * 
 * This is a singleton that persists for the lifetime of the server.
 * Village data is saved/loaded via VillageStorage.
 */
object VillageRegistry {
    
    // All villages keyed by UUID
    private val villages = ConcurrentHashMap<UUID, VillageData>()
    
    // Spatial index: chunk position -> village UUIDs in that chunk
    private val spatialIndex = ConcurrentHashMap<Long, MutableSet<UUID>>()
    
    /**
     * Register a new village
     * Adds to registry and spatial index
     */
    fun registerVillage(village: VillageData) {
        villages[village.uuid] = village
        indexVillage(village)
        LivingVillages.log("Registered village: ${village.name} at ${village.centerPos}")
    }
    
    /**
     * Unregister a village (e.g., if destroyed)
     */
    fun unregisterVillage(uuid: UUID) {
        val village = villages.remove(uuid)
        if (village != null) {
            unindexVillage(village)
            LivingVillages.log("Unregistered village: ${village.name}")
        }
    }
    
    /**
     * Get village by UUID
     */
    fun getVillage(uuid: UUID): VillageData? {
        return villages[uuid]
    }
    
    /**
     * Get all villages
     */
    fun getAllVillages(): Collection<VillageData> {
        return villages.values
    }
    
    /**
     * Find nearest village to a position
     * Returns null if no village within maxDistance
     */
    fun findNearestVillage(pos: BlockPos, maxDistance: Double = 256.0): VillageData? {
        var nearest: VillageData? = null
        var nearestDistance = maxDistance
        
        villages.values.forEach { village ->
            val distance = pos.getSquaredDistance(village.centerPos).let { Math.sqrt(it) }
            if (distance < nearestDistance) {
                nearest = village
                nearestDistance = distance
            }
        }
        
        return nearest
    }
    
    /**
     * Find village at exact position
     * Checks if position is within village radius
     */
    fun getVillageAt(pos: BlockPos): VillageData? {
        return villages.values.firstOrNull { village ->
            pos.isWithinDistance(village.centerPos, village.radius.toDouble())
        }
    }
    
    /**
     * Find all villages in a chunk
     * Uses spatial index for fast lookup
     */
    fun getVillagesInChunk(chunkX: Int, chunkZ: Int): List<VillageData> {
        val chunkKey = packChunkPos(chunkX, chunkZ)
        val uuids = spatialIndex[chunkKey] ?: return emptyList()
        
        return uuids.mapNotNull { uuid -> villages[uuid] }
    }
    
    /**
     * Check if a position is claimed by any village
     */
    fun isPositionClaimed(pos: BlockPos): Boolean {
        return getVillageAt(pos) != null
    }
    
    /**
     * Get village count
     */
    fun getVillageCount(): Int {
        return villages.size
    }
    
    /**
     * Clear all villages (called on world unload)
     */
    fun clear() {
        villages.clear()
        spatialIndex.clear()
        LivingVillages.log("Village registry cleared")
    }
    
    /**
     * Get villages by tier
     */
    fun getVillagesByTier(tier: Int): List<VillageData> {
        return villages.values.filter { it.tier == tier }
    }
    
    /**
     * Get villages by personality
     */
    fun getVillagesByPersonality(personality: VillagePersonality): List<VillageData> {
        return villages.values.filter { it.personality == personality }
    }
    
    // ========================================
    // Spatial Indexing (Internal)
    // ========================================
    
    /**
     * Add village to spatial index
     * Indexes all chunks within village radius
     */
    private fun indexVillage(village: VillageData) {
        val chunks = getChunksInRadius(village.centerPos, village.radius)
        chunks.forEach { chunkKey ->
            spatialIndex.computeIfAbsent(chunkKey) { mutableSetOf() }.add(village.uuid)
        }
    }
    
    /**
     * Remove village from spatial index
     */
    private fun unindexVillage(village: VillageData) {
        val chunks = getChunksInRadius(village.centerPos, village.radius)
        chunks.forEach { chunkKey ->
            spatialIndex[chunkKey]?.remove(village.uuid)
            if (spatialIndex[chunkKey]?.isEmpty() == true) {
                spatialIndex.remove(chunkKey)
            }
        }
    }
    
    /**
     * Get all chunks within radius of a position
     */
    private fun getChunksInRadius(center: BlockPos, radius: Int): Set<Long> {
        val chunks = mutableSetOf<Long>()
        val chunkRadius = (radius shr 4) + 1 // Convert block radius to chunk radius
        
        val centerChunkX = center.x shr 4
        val centerChunkZ = center.z shr 4
        
        for (dx in -chunkRadius..chunkRadius) {
            for (dz in -chunkRadius..chunkRadius) {
                chunks.add(packChunkPos(centerChunkX + dx, centerChunkZ + dz))
            }
        }
        
        return chunks
    }
    
    /**
     * Pack chunk coordinates into a single long
     */
    private fun packChunkPos(x: Int, z: Int): Long {
        return (x.toLong() shl 32) or (z.toLong() and 0xFFFFFFFFL)
    }
    
    // ========================================
    // Statistics & Debug
    // ========================================
    
    /**
     * Get registry statistics for debugging
     */
    fun getStatistics(): RegistryStats {
        val tierCounts = villages.values.groupingBy { it.tier }.eachCount()
        val personalityCounts = villages.values.groupingBy { it.personality }.eachCount()
        val totalPopulation = villages.values.sumOf { it.population }
        val totalBuildings = villages.values.sumOf { it.getBuildingCount() }
        
        return RegistryStats(
            totalVillages = villages.size,
            totalPopulation = totalPopulation,
            totalBuildings = totalBuildings,
            villagesByTier = tierCounts,
            villagesByPersonality = personalityCounts,
            spatialIndexSize = spatialIndex.size
        )
    }
    
    /**
     * Log registry statistics
     */
    fun logStatistics() {
        val stats = getStatistics()
        LivingVillages.log("=== Village Registry Statistics ===")
        LivingVillages.log("Total Villages: ${stats.totalVillages}")
        LivingVillages.log("Total Population: ${stats.totalPopulation}")
        LivingVillages.log("Total Buildings: ${stats.totalBuildings}")
        LivingVillages.log("Spatial Index Size: ${stats.spatialIndexSize} chunks")
        LivingVillages.log("Tiers: ${stats.villagesByTier}")
        LivingVillages.log("Personalities: ${stats.villagesByPersonality}")
        LivingVillages.log("===================================")
    }
}

/**
 * Registry Statistics
 */
data class RegistryStats(
    val totalVillages: Int,
    val totalPopulation: Int,
    val totalBuildings: Int,
    val villagesByTier: Map<Int, Int>,
    val villagesByPersonality: Map<VillagePersonality, Int>,
    val spatialIndexSize: Int
)
