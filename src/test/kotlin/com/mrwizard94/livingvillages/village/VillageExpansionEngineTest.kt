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

class VillageExpansionEngineTest {

    @BeforeEach
    fun setup() {
        // Ensure fresh registry
        BuildingRegistry.clear()
    }

    @AfterEach
    fun teardown() {
        BuildingRegistry.clear()
    }

    @Test
    fun `canExpand returns true when conditions met`() {
        val village = VillageData(
            uuid = UUID.randomUUID(),
            name = "TestVillage",
            biomeType = "plains",
            centerPos = BlockPos(0, 64, 0),
            radius = 16,
            population = 5,
            tier = 0,
            safetyLevel = 0.9
        )
        // Provide minimum resources to satisfy requireMaterials
        village.resources.wood = 128
        village.resources.stone = 128
        village.resources.food = 64

        // Ensure config expects materials (default true)
        assertTrue(LVConfig.expansion.requireMaterials)

        val can = VillageExpansionEngine.canExpand(village)
        assertTrue(can, "Expected village to be allowed to expand when requirements met")
    }

    @Test
    fun `canExpand returns false when safety low`() {
        val village = VillageData(
            uuid = UUID.randomUUID(),
            name = "UnsafeVillage",
            biomeType = "plains",
            centerPos = BlockPos(0, 64, 0),
            radius = 16,
            population = 5,
            tier = 0,
            safetyLevel = 0.2
        )
        village.resources.wood = 128
        village.resources.stone = 128
        village.resources.food = 64

        val can = VillageExpansionEngine.canExpand(village)
        assertFalse(can, "Expected expansion to be blocked when safety is below threshold")
    }

    @Test
    fun `queueConstruction adds build task to village`() {
        val village = VillageData(
            uuid = UUID.randomUUID(),
            name = "QueueTown",
            biomeType = "plains",
            centerPos = BlockPos(10, 64, 10),
            radius = 20
        )

        val template = BuildingTemplate(
            templateId = "living_villages:test_house",
            name = "Test House",
            category = "house",
            biomes = listOf("plains"),
            tierRequirement = 0,
            size = BuildingSize(5, 5, 5),
            beds = 2,
            jobSites = 0,
            blocks = emptyList()
        )

        // Queue a construction
        VillageExpansionEngine.queueConstruction(village, template, BlockPos(15, 64, 15), /* world not needed */ null)

        assertEquals(1, village.constructionQueue.size)
        val task = village.constructionQueue.first()
        assertEquals(template.templateId, task.templateId)
        assertEquals(BlockPos(15, 64, 15), task.targetPos)
    }

    @Test
    fun `selectBuildingType picks house when beds low`() {
        val village = VillageData(
            uuid = UUID.randomUUID(),
            name = "HouseNeed",
            biomeType = "plains",
            centerPos = BlockPos(0, 64, 0),
            radius = 20,
            population = 4
        )

        val template = BuildingTemplate(
            templateId = "living_villages:test_house",
            name = "Test House",
            category = "house",
            biomes = listOf("plains"),
            tierRequirement = 0,
            size = BuildingSize(5, 5, 5),
            beds = 2,
            blocks = emptyList()
        )

        BuildingRegistry.registerTemplate(template.templateId, template)

        // No beds in village yet, so houses should be selected
        val chosen = VillageExpansionEngine.selectBuildingType(village, null)
        assertNotNull(chosen)
        assertEquals("house", chosen!!.category)
    }
}
