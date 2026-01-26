package com.mrwizard94.livingvillages.command

import com.mrwizard94.livingvillages.LivingVillages
import com.mrwizard94.livingvillages.building.BuildingPlacer
import com.mrwizard94.livingvillages.building.BuildingRegistry
import com.mrwizard94.livingvillages.village.VillageDetector
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos

object VillageCommands {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("lv")
                .then(
                    CommandManager.literal("detect")
                        .requires { source -> source.hasPermissionLevel(2) }
                        .executes { context ->
                            val source = context.source
                            try {
                                VillageDetector.detectVillages(source.world)
                                source.sendFeedback(java.util.function.Supplier { 
                                    Text.literal("§aVillage detection complete! Check logs for results.")
                                }, true)
                                return@executes 1
                            } catch (e: Exception) {
                                LivingVillages.error("Village detection failed", e)
                                source.sendError(Text.literal("§cVillage detection failed: ${e.message}"))
                                return@executes 0
                            }
                        }
                )
                .then(
                    CommandManager.literal("list")
                        .executes { context ->
                            val src = context.source
                            val villages = com.mrwizard94.livingvillages.village.VillageRegistry.getAllVillages()

                            val lines = formatVillageList(villages)
                            lines.forEach { line ->
                                src.sendFeedback(java.util.function.Supplier { line }, false)
                            }

                            return@executes villages.size
                        }
                )
                .then(
                    CommandManager.literal("info")
                        .then(
                            CommandManager.argument("uuid", com.mojang.brigadier.arguments.StringArgumentType.word())
                                .executes { context ->
                                    val src = context.source
                                    val uuidStr = com.mojang.brigadier.arguments.StringArgumentType.getString(context, "uuid")
                                    val uuid = try {
                                        java.util.UUID.fromString(uuidStr)
                                    } catch (e: IllegalArgumentException) {
                                        src.sendError(Text.literal("§cInvalid UUID: ${uuidStr}"))
                                        return@executes 0
                                    }

                                    val village = com.mrwizard94.livingvillages.village.VillageRegistry.getVillage(uuid)
                                    if (village == null) {
                                        src.sendError(Text.literal("§cVillage not found: ${uuid}"))
                                        return@executes 0
                                    }

                                    displayVillageInfo(src, village)
                                    return@executes 1
                                }
                        )
                )
                .then(
                    CommandManager.literal("test")
                        .requires { source -> source.hasPermissionLevel(2) }
                        .then(
                            CommandManager.literal("placement")
                                .executes { context ->
                                    val src = context.source
                                    val player = src.player ?: run {
                                        src.sendError(Text.literal("§cMust be a player"))
                                        return@executes 0
                                    }
                                    
                                    try {
                                        // Get first available template
                                        val template = BuildingRegistry.getAllTemplates().firstOrNull()
                                        if (template == null) {
                                            src.sendError(Text.literal("§cNo templates loaded"))
                                            return@executes 0
                                        }
                                        
                                        // Place at player position
                                        val pos = player.blockPos.add(5, 0, 0)
                                        val placed = BuildingPlacer.placeStructure(src.world, template, pos, 0)
                                        
                                        src.sendFeedback(java.util.function.Supplier {
                                            Text.literal("§aPlaced ${template.templateId} at $pos ($placed blocks)")
                                        }, true)
                                        
                                        return@executes 1
                                    } catch (e: Exception) {
                                        LivingVillages.error("Test placement failed", e)
                                        src.sendError(Text.literal("§cPlacement failed: ${e.message}"))
                                        return@executes 0
                                    }
                                }
                        )
                        .then(
                            CommandManager.literal("rotation")
                                .executes { context ->
                                    val src = context.source
                                    val player = src.player ?: run {
                                        src.sendError(Text.literal("§cMust be a player"))
                                        return@executes 0
                                    }
                                    
                                    try {
                                        // Get first available template
                                        val template = BuildingRegistry.getAllTemplates().firstOrNull()
                                        if (template == null) {
                                            src.sendError(Text.literal("§cNo templates loaded"))
                                            return@executes 0
                                        }
                                        
                                        val startPos = player.blockPos
                                        var totalPlaced = 0
                                        
                                        // Place in all 4 rotations with spacing
                                        listOf(0, 90, 180, 270).forEachIndexed { index, rotation ->
                                            val offset = index * 15 // Space them out
                                            val pos = startPos.add(offset, 0, 0)
                                            val placed = BuildingPlacer.placeStructure(src.world, template, pos, rotation)
                                            totalPlaced += placed
                                            
                                            src.sendFeedback(java.util.function.Supplier {
                                                Text.literal("§7${rotation}° rotation at $pos ($placed blocks)")
                                            }, false)
                                        }
                                        
                                        src.sendFeedback(java.util.function.Supplier {
                                            Text.literal("§aPlaced ${template.templateId} in 4 rotations ($totalPlaced total blocks)")
                                        }, true)
                                        
                                        return@executes 1
                                    } catch (e: Exception) {
                                        LivingVillages.error("Test rotation failed", e)
                                        src.sendError(Text.literal("§cRotation test failed: ${e.message}"))
                                        return@executes 0
                                    }
                                }
                        )
                )
                .then(
                    CommandManager.literal("expand")
                        .requires { source -> source.hasPermissionLevel(2) }
                        .then(
                            CommandManager.argument("uuid", com.mojang.brigadier.arguments.StringArgumentType.word())
                                .executes { context ->
                                    val src = context.source
                                    val uuidStr = com.mojang.brigadier.arguments.StringArgumentType.getString(context, "uuid")
                                    val uuid = try {
                                        java.util.UUID.fromString(uuidStr)
                                    } catch (e: IllegalArgumentException) {
                                        src.sendError(Text.literal("§cInvalid UUID: ${uuidStr}"))
                                        return@executes 0
                                    }

                                    val village = com.mrwizard94.livingvillages.village.VillageRegistry.getVillage(uuid)
                                    if (village == null) {
                                        src.sendError(Text.literal("§cVillage not found: ${uuid}"))
                                        return@executes 0
                                    }

                                    try {
                                        com.mrwizard94.livingvillages.village.VillageExpansionEngine.forceExpand(village, src.world)
                                        src.sendFeedback(java.util.function.Supplier {
                                            Text.literal("§aForced expansion for ${village.name}")
                                        }, true)
                                        return@executes 1
                                    } catch (e: Exception) {
                                        LivingVillages.error("Force expand failed", e)
                                        src.sendError(Text.literal("§cExpansion failed: ${e.message}"))
                                        return@executes 0
                                    }
                                }
                        )
                )
                .then(
                    CommandManager.literal("nearest")
                        .executes { context ->
                            val src = context.source
                            val player = src.player ?: run {
                                src.sendError(Text.literal("§cMust be a player"))
                                return@executes 0
                            }
                            
                            val nearest = com.mrwizard94.livingvillages.village.VillageRegistry.findNearestVillage(player.blockPos)
                            if (nearest == null) {
                                src.sendFeedback(java.util.function.Supplier {
                                    Text.literal("§eNo villages found nearby")
                                }, false)
                                return@executes 0
                            }
                            
                            val distance = kotlin.math.sqrt(player.blockPos.getSquaredDistance(nearest.centerPos))
                            src.sendFeedback(java.util.function.Supplier {
                                Text.literal("§aNearest village: §e${nearest.name} §7(${distance.toInt()} blocks away)")
                            }, false)
                            displayVillageInfo(src, nearest)
                            
                            return@executes 1
                        }
                )
                .then(
                    CommandManager.literal("stats")
                        .executes { context ->
                            val src = context.source
                            val stats = com.mrwizard94.livingvillages.village.VillageRegistry.getStatistics()
                            
                            src.sendFeedback(java.util.function.Supplier {
                                Text.literal("§6=== Living Villages Statistics ===")
                            }, false)
                            src.sendFeedback(java.util.function.Supplier {
                                Text.literal("§7Total Villages: §f${stats.totalVillages}")
                            }, false)
                            src.sendFeedback(java.util.function.Supplier {
                                Text.literal("§7Total Population: §f${stats.totalPopulation}")
                            }, false)
                            src.sendFeedback(java.util.function.Supplier {
                                Text.literal("§7Total Buildings: §f${stats.totalBuildings}")
                            }, false)
                            src.sendFeedback(java.util.function.Supplier {
                                Text.literal("§7Villages by Tier: §f${stats.villagesByTier}")
                            }, false)
                            
                            return@executes 1
                        }
                )
        )

    }

    private fun displayVillageInfo(source: net.minecraft.server.command.ServerCommandSource, village: com.mrwizard94.livingvillages.village.VillageData) {
        val lines = formatVillageInfo(village)
        lines.forEach { line ->
            source.sendFeedback(java.util.function.Supplier { line }, false)
        }
    }

    // Formatting helpers - return Text lines so they can be tested without needing a ServerCommandSource
    fun formatVillageList(villages: Collection<com.mrwizard94.livingvillages.village.VillageData>): List<Text> {
        val out = mutableListOf<Text>()
        out.add(Text.literal("§6=== Villages (${villages.size}) ==="))
        villages.forEach { village ->
            val line = buildString {
                append("§e")
                append(village.name)
                append(" §7(")
                append("UUID: ")
                append(village.uuid.toString().substring(0, 8))
                append("..., Tier: ")
                append(village.tier)
                append(", Pop: ")
                append(village.population)
                append(")")
            }
            out.add(Text.literal(line))
        }
        return out
    }

    fun formatVillageInfo(village: com.mrwizard94.livingvillages.village.VillageData): List<Text> {
        val out = mutableListOf<Text>()
        out.add(Text.literal("§6=== ${village.name} ==="))
        out.add(Text.literal("§7UUID: §f${village.uuid}"))
        out.add(Text.literal("§7Position: §f${village.centerPos}"))
        out.add(Text.literal("§7Radius: §f${village.radius} blocks"))
        out.add(Text.literal("§7Tier: §f${village.tier}"))
        out.add(Text.literal("§7Personality: §f${village.personality}"))
        out.add(Text.literal("§7Population: §f${village.population}"))
        out.add(Text.literal("§7Buildings: §f${village.getBuildingCount()}"))
        out.add(Text.literal("§7Beds: §f${village.getBedCount()}"))
        out.add(Text.literal("§7Prosperity: §f${String.format("%.1f", village.prosperityScore)}"))
        out.add(Text.literal("§7Safety: §f${String.format("%.1f%%", village.safetyLevel * 100)}"))
        out.add(Text.literal("§7Resources:"))
        out.add(Text.literal("  §7Food: §f${village.resources.food}"))
        out.add(Text.literal("  §7Wood: §f${village.resources.wood}"))
        out.add(Text.literal("  §7Stone: §f${village.resources.stone}"))
        out.add(Text.literal("§7Construction Queue: §f${village.constructionQueue.size} tasks"))
        return out
    }

    // Test-friendly helpers: allow tests to call command logic and capture Text outputs
    fun runList(collector: (Text) -> Unit): Int {
        val villages = com.mrwizard94.livingvillages.village.VillageRegistry.getAllVillages()
        val lines = formatVillageList(villages)
        lines.forEach { collector(it) }
        return villages.size
    }

    fun runInfo(uuid: java.util.UUID, collector: (Text) -> Unit, error: (Text) -> Unit): Int {
        val village = com.mrwizard94.livingvillages.village.VillageRegistry.getVillage(uuid)
        if (village == null) {
            error(Text.literal("§cVillage not found: ${uuid}"))
            return 0
        }
        val lines = formatVillageInfo(village)
        lines.forEach { collector(it) }
        return 1
    }
}
