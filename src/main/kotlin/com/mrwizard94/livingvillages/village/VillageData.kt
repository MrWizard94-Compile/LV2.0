package com.mrwizard94.livingvillages.village

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtLong
import net.minecraft.nbt.NbtString
import net.minecraft.util.math.BlockPos
import java.util.UUID

/**
 * Village Data Model
 * 
 * Represents all persistent state for a Living Village.
 * This is the core data structure that gets saved to world data.
 * 
 * Based on: LIVING_VILLAGES_CODEX.md - NBT Data Structures
 */
data class VillageData(
    val uuid: UUID,
    var name: String,
    var biomeType: String,
    var centerPos: BlockPos,
    var radius: Int,
    var population: Int = 0,
    var tier: Int = 0,
    var prosperityScore: Double = 50.0,
    var safetyLevel: Double = 1.0,
    val resources: VillageResources = VillageResources(),
    val buildings: MutableList<VillageBuilding> = mutableListOf(),
    val constructionQueue: MutableList<BuildTask> = mutableListOf(),
    var mayorData: MayorData? = null,
    var personality: VillagePersonality = VillagePersonality.BALANCED,
    var expansionTimer: Int = 0,
    var lastExpansionTime: Long = 0,
    val flags: MutableMap<String, Boolean> = mutableMapOf()
) {
    
    /**
     * Serialize village data to NBT
     * Must be 100% reversible (deserialize should produce identical object)
     */
    fun toNbt(): NbtCompound {
        val nbt = NbtCompound()
        
        // Core identity
        nbt.putUuid("UUID", uuid)
        nbt.putString("Name", name)
        nbt.putString("BiomeType", biomeType)
        nbt.putLong("CenterPos", centerPos.asLong())
        nbt.putInt("Radius", radius)
        
        // Stats
        nbt.putInt("Population", population)
        nbt.putInt("Tier", tier)
        nbt.putDouble("ProsperityScore", prosperityScore)
        nbt.putDouble("SafetyLevel", safetyLevel)
        
        // Resources
        nbt.put("Resources", resources.toNbt())
        
        // Buildings
        val buildingsList = NbtList()
        buildings.forEach { building ->
            buildingsList.add(building.toNbt())
        }
        nbt.put("Buildings", buildingsList)
        
        // Construction queue
        val queueList = NbtList()
        constructionQueue.forEach { task ->
            queueList.add(task.toNbt())
        }
        nbt.put("ConstructionQueue", queueList)
        
        // Mayor (optional)
        mayorData?.let {
            nbt.put("Mayor", it.toNbt())
        }
        
        // Personality
        nbt.putString("Personality", personality.name)
        
        // Timers
        nbt.putInt("ExpansionTimer", expansionTimer)
        nbt.putLong("LastExpansion", lastExpansionTime)
        
        // Flags
        val flagsNbt = NbtCompound()
        flags.forEach { (key, value) ->
            flagsNbt.putBoolean(key, value)
        }
        nbt.put("Flags", flagsNbt)
        
        return nbt
    }
    
    /**
     * Village state management
     */
    
    fun incrementExpansionTimer() {
        expansionTimer++
    }
    
    fun resetExpansionTimer() {
        expansionTimer = 0
    }
    
    fun getBuildingCount(): Int = buildings.size
    
    fun getBedCount(): Int {
        return buildings.sumOf { it.bedCount }
    }
    
    fun isUnderAttack(): Boolean {
        return flags["under_attack"] ?: false
    }
    
    fun hasMinimumResources(): Boolean {
        return resources.wood >= 64 && 
               resources.stone >= 64 && 
               resources.food >= 32
    }
    
    fun getMissingProfessions(): List<String> {
        // TODO: Implement profession tracking
        return emptyList()
    }
    
    fun needsMorePaths(): Boolean {
        // TODO: Implement path density calculation
        return false
    }
    
    fun needsMoreLighting(): Boolean {
        // TODO: Implement lighting coverage calculation
        return false
    }
    
    /**
     * Mark village as dirty (needs saving)
     * This is a no-op for now, but can be used to track changes
     */
    fun markDirty() {
        // Village will be saved automatically via PersistentState
        // This method exists for API consistency
    }
    
    companion object {
        /**
         * Deserialize village data from NBT
         * Must handle missing/malformed data gracefully
         */
        fun fromNbt(nbt: NbtCompound): VillageData {
            val uuid = nbt.getUuid("UUID")
            val name = nbt.getString("Name")
            val biomeType = nbt.getString("BiomeType")
            val centerPos = BlockPos.fromLong(nbt.getLong("CenterPos"))
            val radius = nbt.getInt("Radius")
            
            val village = VillageData(
                uuid = uuid,
                name = name,
                biomeType = biomeType,
                centerPos = centerPos,
                radius = radius
            )
            
            // Stats
            if (nbt.contains("Population")) village.population = nbt.getInt("Population")
            if (nbt.contains("Tier")) village.tier = nbt.getInt("Tier")
            if (nbt.contains("ProsperityScore")) village.prosperityScore = nbt.getDouble("ProsperityScore")
            if (nbt.contains("SafetyLevel")) village.safetyLevel = nbt.getDouble("SafetyLevel")
            
            // Resources
            if (nbt.contains("Resources")) {
                val resourcesNbt = nbt.getCompound("Resources")
                village.resources.wood = resourcesNbt.getInt("Wood")
                village.resources.stone = resourcesNbt.getInt("Stone")
                village.resources.iron = resourcesNbt.getInt("Iron")
                village.resources.gold = resourcesNbt.getInt("Gold")
                village.resources.food = resourcesNbt.getInt("Food")
            }
            
            // Buildings
            if (nbt.contains("Buildings")) {
                val buildingsList = nbt.getList("Buildings", 10) // 10 = NBT_COMPOUND
                buildingsList.forEach { buildingNbt ->
                    village.buildings.add(VillageBuilding.fromNbt(buildingNbt as NbtCompound))
                }
            }
            
            // Construction queue
            if (nbt.contains("ConstructionQueue")) {
                val queueList = nbt.getList("ConstructionQueue", 10)
                queueList.forEach { taskNbt ->
                    village.constructionQueue.add(BuildTask.fromNbt(taskNbt as NbtCompound))
                }
            }
            
            // Mayor
            if (nbt.contains("Mayor")) {
                village.mayorData = MayorData.fromNbt(nbt.getCompound("Mayor"))
            }
            
            // Personality
            if (nbt.contains("Personality")) {
                village.personality = try {
                    VillagePersonality.valueOf(nbt.getString("Personality"))
                } catch (e: IllegalArgumentException) {
                    VillagePersonality.BALANCED
                }
            }
            
            // Timers
            if (nbt.contains("ExpansionTimer")) village.expansionTimer = nbt.getInt("ExpansionTimer")
            if (nbt.contains("LastExpansion")) village.lastExpansionTime = nbt.getLong("LastExpansion")
            
            // Flags
            if (nbt.contains("Flags")) {
                val flagsNbt = nbt.getCompound("Flags")
                flagsNbt.keys.forEach { key ->
                    village.flags[key] = flagsNbt.getBoolean(key)
                }
            }
            
            return village
        }
    }
}

/**
 * Village Resources
 * Tracks available materials for construction
 */
data class VillageResources(
    var food: Int = 0,
    var wood: Int = 0,
    var stone: Int = 0,
    var iron: Int = 0,
    var gold: Int = 0
) {
    fun toNbt(): NbtCompound {
        val nbt = NbtCompound()
        nbt.putInt("Food", food)
        nbt.putInt("Wood", wood)
        nbt.putInt("Stone", stone)
        nbt.putInt("Iron", iron)
        nbt.putInt("Gold", gold)
        return nbt
    }
}

/**
 * Building record
 * Represents a placed building in the village
 */
data class VillageBuilding(
    val templateId: String,
    val position: BlockPos,
    val rotation: Int,
    var isBuilt: Boolean = false,
    val bedCount: Int = 0,
    val jobSiteCount: Int = 0
) {
    fun toNbt(): NbtCompound {
        val nbt = NbtCompound()
        nbt.putString("Type", templateId)
        nbt.putLong("Pos", position.asLong())
        nbt.putInt("Rotation", rotation)
        nbt.putBoolean("Built", isBuilt)
        nbt.putInt("Beds", bedCount)
        nbt.putInt("JobSites", jobSiteCount)
        return nbt
    }
    
    companion object {
        fun fromNbt(nbt: NbtCompound): VillageBuilding {
            return VillageBuilding(
                templateId = nbt.getString("Type"),
                position = BlockPos.fromLong(nbt.getLong("Pos")),
                rotation = nbt.getInt("Rotation"),
                isBuilt = nbt.getBoolean("Built"),
                bedCount = if (nbt.contains("Beds")) nbt.getInt("Beds") else 0,
                jobSiteCount = if (nbt.contains("JobSites")) nbt.getInt("JobSites") else 0
            )
        }
    }
}

/**
 * Build Task
 * Represents a building in the construction queue
 */
data class BuildTask(
    val templateId: String,
    val targetPos: BlockPos,
    var progress: Int = 0,
    val totalBlocks: Int = 0,
    val requiredMaterials: Map<String, Int> = emptyMap()
) {
    fun toNbt(): NbtCompound {
        val nbt = NbtCompound()
        nbt.putString("TemplateID", templateId)
        nbt.putLong("TargetPos", targetPos.asLong())
        nbt.putInt("Progress", progress)
        nbt.putInt("TotalBlocks", totalBlocks)
        
        val materialsNbt = NbtCompound()
        requiredMaterials.forEach { (material, count) ->
            materialsNbt.putInt(material, count)
        }
        nbt.put("RequiredMaterials", materialsNbt)
        
        return nbt
    }
    
    fun incrementProgress() {
        progress++
    }
    
    fun isComplete(): Boolean = progress >= totalBlocks
    
    companion object {
        fun fromNbt(nbt: NbtCompound): BuildTask {
            val materials = mutableMapOf<String, Int>()
            if (nbt.contains("RequiredMaterials")) {
                val materialsNbt = nbt.getCompound("RequiredMaterials")
                materialsNbt.keys.forEach { key ->
                    materials[key] = materialsNbt.getInt(key)
                }
            }
            
            return BuildTask(
                templateId = nbt.getString("TemplateID"),
                targetPos = BlockPos.fromLong(nbt.getLong("TargetPos")),
                progress = nbt.getInt("Progress"),
                totalBlocks = nbt.getInt("TotalBlocks"),
                requiredMaterials = materials
            )
        }
    }
}

/**
 * Mayor Data
 * Tracks village leadership
 */
data class MayorData(
    val playerUuid: UUID,
    val claimedDate: Long,
    var reputation: Int = 0
) {
    fun toNbt(): NbtCompound {
        val nbt = NbtCompound()
        nbt.putUuid("PlayerUUID", playerUuid)
        nbt.putLong("ClaimedDate", claimedDate)
        nbt.putInt("Reputation", reputation)
        return nbt
    }
    
    companion object {
        fun fromNbt(nbt: NbtCompound): MayorData {
            return MayorData(
                playerUuid = nbt.getUuid("PlayerUUID"),
                claimedDate = nbt.getLong("ClaimedDate"),
                reputation = if (nbt.contains("Reputation")) nbt.getInt("Reputation") else 0
            )
        }
    }
}

/**
 * Village Personality Types
 * Affects building priorities and expansion behavior
 */
enum class VillagePersonality {
    AGRARIAN,      // Focuses on farms and food production
    TRADER,        // Focuses on shops and commerce
    FORTIFIED,     // Focuses on defense structures
    ARTISAN,       // Focuses on profession buildings
    BALANCED       // Equal distribution
}
