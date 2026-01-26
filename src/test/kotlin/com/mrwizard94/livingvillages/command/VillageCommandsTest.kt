package com.mrwizard94.livingvillages.command

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.ServerCommandSource
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class VillageCommandsTest {
    @Test
    fun `registers lv commands`() {
        val dispatcher = CommandDispatcher<ServerCommandSource>()
        VillageCommands.register(dispatcher)

        val lvNode = dispatcher.root.getChild("lv")
        assertNotNull(lvNode, "Expected 'lv' command to be registered")

        val detectNode = lvNode.getChild("detect")
        assertNotNull(detectNode, "Expected 'lv detect' subcommand to be registered")

        val listNode = lvNode.getChild("list")
        assertNotNull(listNode, "Expected 'lv list' subcommand to be registered")
    }
}
