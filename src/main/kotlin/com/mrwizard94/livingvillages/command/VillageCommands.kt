package com.mrwizard94.livingvillages.command

import com.mrwizard94.livingvillages.LivingVillages
import com.mrwizard94.livingvillages.village.VillageDetector
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

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
                                LivingVillages.log("Village detection invoked by ${'$'}{source.name}")
                                return@executes 1
                            } catch (e: Exception) {
                                LivingVillages.error("Village detection failed", e)
                                return@executes 0
                            }
                        }
                )
                .then(
                    CommandManager.literal("list")
                        .executes { context ->
                            val src = context.source
                            val villages = com.mrwizard94.livingvillages.village.VillageRegistry.getAllVillages()

                            // Header
                            src.sendFeedback(java.util.function.Supplier { Text.literal("§6=== Villages (${villages.size}) ===") }, false)

                            // Per-village detail lines
                            villages.forEach { village ->
                                val line = buildString {
                                    append("§e")
                                    append(village.name)
                                    append(" §7(")
                                    append("UUID: ")
                                    append(village.uuid)
                                    append(", Tier: ")
                                    append(village.tier)
                                    append(", Pop: ")
                                    append(village.population)
                                    append(")")
                                }
                                src.sendFeedback(java.util.function.Supplier { Text.literal(line) }, false)
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
                                        src.sendError(Text.literal("§cInvalid UUID: ${'$'}uuidStr"))
                                        return@executes 0
                                    }

                                    val village = com.mrwizard94.livingvillages.village.VillageRegistry.getVillage(uuid)
                                    if (village == null) {
                                        src.sendError(Text.literal("§cVillage not found: ${'$'}uuid"))
                                        return@executes 0
                                    }

                                    displayVillageInfo(src, village)
                                    return@executes 1
                                }
                        )
                )
        )

    }

    private fun displayVillageInfo(source: net.minecraft.server.command.ServerCommandSource, village: com.mrwizard94.livingvillages.village.VillageData) {
        source.sendFeedback(java.util.function.Supplier { Text.literal("§6=== ${'$'}{village.name} ===") }, false)
        source.sendFeedback(java.util.function.Supplier { Text.literal("§7UUID: §f${'$'}{village.uuid}") }, false)
        source.sendFeedback(java.util.function.Supplier { Text.literal("§7Tier: §f${'$'}{village.tier}") }, false)
        source.sendFeedback(java.util.function.Supplier { Text.literal("§7Population: §f${'$'}{village.population}") }, false)
        source.sendFeedback(java.util.function.Supplier { Text.literal("§7Buildings: §f${'$'}{village.getBuildingCount()}") }, false)
        source.sendFeedback(java.util.function.Supplier { Text.literal("§7Prosperity: §f${String.format("%.1f", village.prosperityScore)}") }, false)
        source.sendFeedback(java.util.function.Supplier { Text.literal("§7Safety: §f${String.format("%.1f%%", village.safetyLevel * 100)}") }, false)

        // Resources
        source.sendFeedback(java.util.function.Supplier { Text.literal("§7Resources:") }, false)
        source.sendFeedback(java.util.function.Supplier { Text.literal("  §7Food: §f${'$'}{village.resources.food}") }, false)
        source.sendFeedback(java.util.function.Supplier { Text.literal("  §7Wood: §f${'$'}{village.resources.wood}") }, false)
        source.sendFeedback(java.util.function.Supplier { Text.literal("  §7Stone: §f${'$'}{village.resources.stone}") }, false)
    }
}

