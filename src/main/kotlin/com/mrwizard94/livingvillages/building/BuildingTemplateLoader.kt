package com.mrwizard94.livingvillages.building

import com.mrwizard94.livingvillages.LivingVillages
import kotlinx.serialization.json.Json
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

/**
 * Building Template Loader
 * 
 * Loads building templates from JSON files.
 * Templates are located in: data/living_villages/templates/
 * 
 * Based on: LIVING_VILLAGES_CODEX.md - Building Template System
 */
object BuildingTemplateLoader : SimpleResourceReloadListener<Map<String, BuildingTemplate>> {
    
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    override fun getFabricId(): Identifier {
        return Identifier.tryParse("${LivingVillages.MOD_ID}:building_templates")!!
    }
    
    override fun load(
        manager: ResourceManager,
        profiler: Profiler,
        executor: Executor
    ): CompletableFuture<Map<String, BuildingTemplate>> {
        return CompletableFuture.supplyAsync({
            val templates = mutableMapOf<String, BuildingTemplate>()
            
            profiler.push("load_building_templates")
            
            try {
                // Find all template JSON files
                val resources = manager.findResources("templates") { path ->
                    path.path.endsWith(".json")
                }
                
                resources.forEach { (id, resource) ->
                    try {
                        val templateId = id.path
                            .removePrefix("templates/")
                            .removeSuffix(".json")
                            .replace("/", ":")
                        
                        val jsonText = resource.inputStream.bufferedReader().use { it.readText() }
                        val template = json.decodeFromString<BuildingTemplate>(jsonText)
                        
                        // Ensure template ID matches
                        val fullId = "${LivingVillages.MOD_ID}:$templateId"
                        templates[fullId] = template.copy(templateId = fullId)
                        
                        LivingVillages.log("Loaded building template: $fullId")
                    } catch (e: Exception) {
                        LivingVillages.error("Failed to load template: $id", e)
                    }
                }
                
                LivingVillages.log("Loaded ${templates.size} building templates")
                
            } catch (e: Exception) {
                LivingVillages.error("Failed to load building templates", e)
            } finally {
                profiler.pop()
            }
            
            templates
        }, executor)
    }
    
    override fun apply(
        data: Map<String, BuildingTemplate>,
        manager: ResourceManager,
        profiler: Profiler,
        executor: Executor
    ): CompletableFuture<Void> {
        return CompletableFuture.runAsync({
            profiler.push("register_building_templates")
            
            try {
                BuildingRegistry.clear()
                data.forEach { (id, template) ->
                    BuildingRegistry.registerTemplate(id, template)
                }
                
                LivingVillages.log("Registered ${data.size} building templates")
                BuildingRegistry.logStatistics()
                
            } catch (e: Exception) {
                LivingVillages.error("Failed to register building templates", e)
            } finally {
                profiler.pop()
            }
        }, executor).thenApply { null }
    }
    
    /**
     * Register the loader with Fabric
     */
    fun register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(this)
        LivingVillages.log("Building template loader registered")
    }
}
