package com.mrwizard94.livingvillages.village

import com.mrwizard94.livingvillages.building.BuildingRegistry
import com.mrwizard94.livingvillages.building.BuildingSize
import com.mrwizard94.livingvillages.building.BuildingTemplate
import com.mrwizard94.livingvillages.config.LVConfig
import net.minecraft.util.math.BlockPos
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class VillageExpansionEngineIntegrationTest {

    @BeforeEach
    fun setup() {
        BuildingRegistry.clear()
    }

    @AfterEach
    fun teardown() {
        BuildingRegistry.clear()
    }

    @Test
    fun `testTick queues construction when interval reached`() {
        val template = BuildingTemplate(
            templateId = "living_villages:int_house",
            name = "Integration House",
            category = "house",
            biomes = listOf("plains"),
            tierRequirement = 0,
            size = BuildingSize(3, 3, 3),
            beds = 1,
            blocks = emptyList(),
            terrainAdaptation = "none"
        )

        BuildingRegistry.registerTemplate(template.templateId, template)

        val village = VillageData(
            uuid = UUID.randomUUID(),
            name = "IntegrationVillage",
            biomeType = "plains",
            centerPos = BlockPos(0, 64, 0),
            radius = 32,
            population = 2
        )

        // Ensure village meets resource requirements
        village.resources.wood = 128
        village.resources.stone = 128
        village.resources.food = 64

        // Set expansionTimer to one less than interval so testTick will trigger
        village.expansionTimer = LVConfig.expansion.expansionIntervalTicks - 1

        VillageExpansionEngine.testTick(
            village = village,
            currentTime = 777L,
            random = java.util.Random(0L)
        ) { _, _ -> BlockPos(8, 64, 8) }

        assertEquals(1, village.constructionQueue.size)
        val task = village.constructionQueue.first()
        assertEquals(template.templateId, task.templateId)
        assertEquals(BlockPos(8, 64, 8), task.targetPos)
        assertEquals(777L, village.lastExpansionTime)
        assertEquals(0, village.expansionTimer)
    }

    @Test
    fun `testTick does not queue when cannot expand`() {
        val template = BuildingTemplate(
            templateId = "living_villages:int_house2",
            name = "Integration House 2",
            category = "house",
            biomes = listOf("plains"),
            tierRequirement = 0,
            size = BuildingSize(3, 3, 3),
            beds = 1,
            blocks = emptyList(),
            terrainAdaptation = "none"
        )

        BuildingRegistry.registerTemplate(template.templateId, template)

        val village = VillageData(
            uuid = UUID.randomUUID(),
            name = "UnsafeVillage",
            biomeType = "plains",
            centerPos = BlockPos(0, 64, 0),
            radius = 32,
            population = 2,
            safetyLevel = 0.1 // too low
        )

        village.expansionTimer = LVConfig.expansion.expansionIntervalTicks - 1

        VillageExpansionEngine.testTick(
            village = village,
            currentTime = 888L,
            random = java.util.Random(0L)
        ) { _, _ -> BlockPos(9, 64, 9) }

        assertEquals(0, village.constructionQueue.size)
        assertEquals(0, village.expansionTimer)
        assertEquals(0L, village.lastExpansionTime)
    }
}