package com.mrwizard94.livingvillages.village

import com.mrwizard94.livingvillages.LivingVillages
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtList
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.PersistentState
import net.minecraft.world.PersistentStateManager
import java.io.File
import java.io.IOException

/**
 * Village Storage
 * 
 * Handles persistent storage of village data to world save files.
 * Villages are saved to: world/data/living_villages.dat
 * 
 * This uses Minecraft's PersistentState system to automatically
 * save when the world saves and load when the world loads.
 */
class VillageStorage : PersistentState() {
    
    companion object {
        private const val DATA_NAME = "living_villages"
        
        /**
         * Get or create village storage for a world
         * Called when world loads
         */
        fun getOrCreate(server: MinecraftServer): VillageStorage {
            val persistentStateManager = server.overworld.persistentStateManager
            
            val type = PersistentState.Type<VillageStorage>(
                ::VillageStorage,
                ::fromNbt,
                null
            )
            
            return persistentStateManager.getOrCreate(type, DATA_NAME)
        }
        
        /**
         * Load village data from NBT
         */
        private fun fromNbt(nbt: NbtCompound, lookup: net.minecraft.registry.RegistryWrapper.WrapperLookup): VillageStorage {
            val storage = VillageStorage()
            
            try {
                val version = nbt.getInt("Version")
                if (version != 1) {
                    LivingVillages.error("Unsupported village data version: $version")
                    return storage
                }
                
                val villagesList = nbt.getList("Villages", 10) // 10 = NBT_COMPOUND
                var loadedCount = 0
                var errorCount = 0
                
                villagesList.forEach { villageNbt ->
                    try {
                        val village = VillageData.fromNbt(villageNbt as NbtCompound)
                        VillageRegistry.registerVillage(village)
                        loadedCount++
                    } catch (e: Exception) {
                        LivingVillages.error("Failed to load village from NBT", e)
                        errorCount++
                    }
                }
                
                LivingVillages.log("Loaded $loadedCount villages from world data")
                if (errorCount > 0) {
                    LivingVillages.error("Failed to load $errorCount villages")
                }
                
                // Log statistics
                VillageRegistry.logStatistics()
                
            } catch (e: Exception) {
                LivingVillages.error("Failed to load village storage", e)
            }
            
            return storage
        }
    }
    
    /**
     * Save village data to NBT
     * Called automatically when world saves
     */
    override fun writeNbt(nbt: NbtCompound, registryLookup: net.minecraft.registry.RegistryWrapper.WrapperLookup): NbtCompound {
        try {
            // Version for future compatibility
            nbt.putInt("Version", 1)
            
            // Serialize all villages
            val villagesList = NbtList()
            var savedCount = 0
            
            VillageRegistry.getAllVillages().forEach { village ->
                try {
                    villagesList.add(village.toNbt())
                    savedCount++
                } catch (e: Exception) {
                    LivingVillages.error("Failed to save village: ${village.name}", e)
                }
            }
            
            nbt.put("Villages", villagesList)
            
            LivingVillages.log("Saved $savedCount villages to world data")
            
        } catch (e: Exception) {
            LivingVillages.error("Failed to save village storage", e)
        }
        
        return nbt
    }
    
    /**
     * Initialize village storage for a world
     * Called when world loads
     */
    fun initialize(server: MinecraftServer) {
        LivingVillages.log("Initializing village storage...")
        
        // Clear existing registry
        VillageRegistry.clear()
        
        // Storage is loaded automatically via getOrCreate
        // This method is just for logging and initialization
        
        LivingVillages.log("Village storage initialized")
    }
    
    /**
     * Save villages immediately
     * Can be called manually for testing or crash recovery
     */
    fun saveNow(server: MinecraftServer) {
        try {
            markDirty() // Mark as needing save
            val persistentStateManager = server.overworld.persistentStateManager
            persistentStateManager.save()
            LivingVillages.log("Villages saved successfully")
        } catch (e: Exception) {
            LivingVillages.error("Failed to save villages", e)
        }
    }
    
    /**
     * Cleanup on world unload
     */
    fun cleanup() {
        LivingVillages.log("Cleaning up village storage...")
        VillageRegistry.clear()
        LivingVillages.log("Village storage cleaned up")
    }
}

/**
 * Village Storage Manager
 * 
 * Manages village storage lifecycle for server events
 */
object VillageStorageManager {
    
    private var storage: VillageStorage? = null
    
    /**
     * Called when server starts/world loads
     */
    fun onServerStart(server: MinecraftServer) {
        try {
            LivingVillages.log("Loading village data...")
            storage = VillageStorage.getOrCreate(server)
            storage?.initialize(server)
            LivingVillages.log("Village data loaded successfully")
            
            // Detect villages after loading saved data
            // Delay to ensure world is fully loaded
            server.execute {
                server.overworld?.let { world ->
                    com.mrwizard94.livingvillages.village.VillageDetector.detectVillages(world)
                }
            }
        } catch (e: Exception) {
            LivingVillages.error("Failed to load village data", e)
            storage = null
        }
    }
    
    /**
     * Called when server stops/world unloads
     */
    fun onServerStop(server: MinecraftServer) {
        try {
            LivingVillages.log("Saving and unloading village data...")
            
            // Save one last time
            storage?.saveNow(server)
            
            // Cleanup
            storage?.cleanup()
            storage = null
            
            LivingVillages.log("Village data unloaded successfully")
        } catch (e: Exception) {
            LivingVillages.error("Failed to unload village data", e)
        }
    }
    
    /**
     * Get current storage instance
     */
    fun getStorage(): VillageStorage? {
        return storage
    }
    
    /**
     * Force save villages
     */
    fun forceSave(server: MinecraftServer) {
        storage?.saveNow(server)
    }
}
