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

        val infoNode = lvNode.getChild("info")
        assertNotNull(infoNode, "Expected 'lv info' subcommand to be registered")
        // Argument node is not guaranteed to be a literal; at least check the info node has children
        assertTrue(infoNode.children.isNotEmpty(), "Expected 'lv info' to have an argument child (uuid)")
    }

    @Test
    fun `format village list output`() {
        val v = com.mrwizard94.livingvillages.village.VillageData(
            uuid = java.util.UUID.randomUUID(),
            name = "Hamlet",
            biomeType = "plains",
            centerPos = net.minecraft.util.math.BlockPos(0, 64, 0),
            radius = 8,
            population = 5,
            tier = 0
        )

        val lines = VillageCommands.formatVillageList(listOf(v))
        assertEquals(2, lines.size, "Expected header + one village line")
        assertTrue(lines[0].toString().contains("Villages (1)"), "Header should contain village count")
        assertTrue(lines[1].toString().contains("Hamlet"), "Village line should contain the village name")
        assertTrue(lines[1].toString().contains("Tier: 0"), "Village line should contain tier info")
    }

    @Test
    fun `format village info output`() {
        val v = com.mrwizard94.livingvillages.village.VillageData(
            uuid = java.util.UUID.randomUUID(),
            name = "Settlement",
            biomeType = "plains",
            centerPos = net.minecraft.util.math.BlockPos(10, 70, -5),
            radius = 12,
            population = 12,
            tier = 2,
            prosperityScore = 72.4,
            safetyLevel = 0.85
        )
        v.resources.food = 42
        v.resources.wood = 64
        v.resources.stone = 128

        val lines = VillageCommands.formatVillageInfo(v)
        val joined = lines.joinToString(separator = "\n") { it.toString() }
        assertTrue(lines.any { it.toString().contains("Settlement") }, "Should contain the village name. Lines:\n${joined}")
        assertTrue(lines.any { it.toString().contains(v.uuid.toString()) }, "Should contain the UUID. Lines:\n${joined}")
        assertTrue(lines.any { it.toString().contains("Prosperity") }, "Should contain prosperity line. Lines:\n${joined}")
        assertTrue(lines.any { it.toString().contains("Food") }, "Should contain food resource line. Lines:\n${joined}")
    }

    @Test
    fun `run list command collects messages`() {
        // Setup registry with one village
        val v = com.mrwizard94.livingvillages.village.VillageData(
            uuid = java.util.UUID.randomUUID(),
            name = "CollectorTown",
            biomeType = "plains",
            centerPos = net.minecraft.util.math.BlockPos(5, 65, 5),
            radius = 10
        )
        com.mrwizard94.livingvillages.village.VillageRegistry.registerVillage(v)

        val collected = mutableListOf<String>()
        val count = VillageCommands.runList { text -> collected.add(text.toString()) }

        assertEquals(1, count)
        assertTrue(collected.any { it.contains("CollectorTown") }, "Expected village name in output")

        // Cleanup
        com.mrwizard94.livingvillages.village.VillageRegistry.unregisterVillage(v.uuid)
    }

    @Test
    fun `run info command success and failure`() {
        val v = com.mrwizard94.livingvillages.village.VillageData(
            uuid = java.util.UUID.randomUUID(),
            name = "InfoTown",
            biomeType = "plains",
            centerPos = net.minecraft.util.math.BlockPos(2, 64, -2),
            radius = 6
        )
        com.mrwizard94.livingvillages.village.VillageRegistry.registerVillage(v)

        val out = mutableListOf<String>()
        val errs = mutableListOf<String>()

        val res = VillageCommands.runInfo(v.uuid, { text -> out.add(text.toString()) }, { err -> errs.add(err.toString()) })
        assertEquals(1, res)
        assertTrue(out.any { it.contains("InfoTown") }, "Info output should include village name")

        // Non-existent uuid
        val fake = java.util.UUID.randomUUID()
        out.clear(); errs.clear()
        val res2 = VillageCommands.runInfo(fake, { text -> out.add(text.toString()) }, { err -> errs.add(err.toString()) })
        assertEquals(0, res2)
        assertTrue(errs.any { it.contains("Village not found") }, "Error should indicate village missing")

        // Cleanup
        com.mrwizard94.livingvillages.village.VillageRegistry.unregisterVillage(v.uuid)
    }
}
