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
                            val count = com.mrwizard94.livingvillages.village.VillageRegistry.getAllVillages().size
                            LivingVillages.log("lv list invoked by ${'$'}{context.source.name} (count=${'$'}count)")
                            return@executes count
                        }
                )
        )
    }
}
