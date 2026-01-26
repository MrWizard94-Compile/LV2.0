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
                                LivingVillages.log("Village detection invoked by ${source.name}")
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
                append(village.uuid)
                append(", Tier: ")
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
        out.add(Text.literal("§7Tier: §f${village.tier}"))
        out.add(Text.literal("§7Population: §f${village.population}"))
        out.add(Text.literal("§7Buildings: §f${village.getBuildingCount()}"))
        out.add(Text.literal("§7Prosperity: §f${String.format("%.1f", village.prosperityScore)}"))
        out.add(Text.literal("§7Safety: §f${String.format("%.1f%%", village.safetyLevel * 100)}"))
        out.add(Text.literal("§7Resources:"))
        out.add(Text.literal("  §7Food: §f${village.resources.food}"))
        out.add(Text.literal("  §7Wood: §f${village.resources.wood}"))
        out.add(Text.literal("  §7Stone: §f${village.resources.stone}"))
        return out
    }
}

