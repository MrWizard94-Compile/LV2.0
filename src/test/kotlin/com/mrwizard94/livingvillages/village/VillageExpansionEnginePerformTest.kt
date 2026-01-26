package com.mrwizard94.livingvillages.village

import com.mrwizard94.livingvillages.building.BuildingRegistry
import com.mrwizard94.livingvillages.building.BuildingSize
import com.mrwizard94.livingvillages.building.BuildingTemplate
import net.minecraft.util.math.BlockPos
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class VillageExpansionEnginePerformTest {

    @BeforeEach
    fun setup() {
        BuildingRegistry.clear()
    }

    @AfterEach
    fun teardown() {
        BuildingRegistry.clear()
    }

    @Test
    fun `performExpansion enqueues task and sets lastExpansionTime`() {
        val template = BuildingTemplate(
            templateId = "living_villages:test_perform",
            name = "Perform House",
            category = "house",
            biomes = listOf("plains"),
            tierRequirement = 0,
            size = BuildingSize(3, 3, 3),
            beds = 1,
            blocks = emptyList(),
            terrainAdaptation = "none"
        )

        val village = VillageData(
            uuid = UUID.randomUUID(),
            name = "PerformVillage",
            biomeType = "plains",
            centerPos = BlockPos(0, 64, 0),
            radius = 16
        )

        val time = 424242L
        VillageExpansionEngine.performExpansion(village, template, BlockPos(5, 64, 5), time, null)

        assertEquals(1, village.constructionQueue.size)
        assertEquals(time, village.lastExpansionTime)
    }
}