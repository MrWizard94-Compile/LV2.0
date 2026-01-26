package com.mrwizard94.livingvillages.util

import com.mrwizard94.livingvillages.LivingVillages
import java.util.Random

/**
 * Name Generator
 * 
 * Generates biome-aware names for villages and villagers.
 * 
 * Based on: LIVING_VILLAGES_CODEX_PART2.md - Name Generation System
 */
object NameGenerator {
    
    private val random = Random()
    
    // Village name prefixes and suffixes by biome
    private val villagePrefixes = mapOf(
        "minecraft:plains" to listOf(
            "Green", "Golden", "Meadow", "Sunny", "Bright", "Fair", "Peaceful",
            "Oak", "Willow", "River", "Hill", "Valley", "Field", "Brook"
        ),
        "minecraft:desert" to listOf(
            "Sand", "Oasis", "Golden", "Sun", "Dune", "Mirage", "Cactus",
            "Dust", "Wind", "Scorch", "Shimmer", "Nomad", "Caravan"
        ),
        "minecraft:taiga" to listOf(
            "Pine", "Frost", "Snow", "Evergreen", "Mountain", "Cold", "Winter",
            "Fir", "Spruce", "Ice", "Crystal", "Northern", "Boreal"
        ),
        "minecraft:savanna" to listOf(
            "Acacia", "Warm", "Sunset", "Grassland", "Wild", "Open", "Vast",
            "Prairie", "Steppe", "Dry", "Windswept", "Golden", "Amber"
        ),
        "minecraft:jungle" to listOf(
            "Jungle", "Vine", "Tropical", "Green", "Wild", "Dense", "Ancient",
            "Canopy", "Emerald", "Lush", "Verdant", "Thicket", "Overgrown"
        )
    )
    
    private val villageSuffixes = mapOf(
        "minecraft:plains" to listOf(
            "Village", "Town", "Hamlet", "Settlement", "Crossing", "Haven",
            "Fields", "Meadows", "Hills", "Valley", "Brook", "Bridge"
        ),
        "minecraft:desert" to listOf(
            "Oasis", "Sands", "Outpost", "Caravan", "Rest", "Well", "Spring",
            "Dunes", "Shade", "Refuge", "Trading Post", "Camp"
        ),
        "minecraft:taiga" to listOf(
            "Village", "Lodge", "Outpost", "Settlement", "Camp", "Haven",
            "Pines", "Frost", "Peak", "Valley", "Glade", "Refuge"
        ),
        "minecraft:savanna" to listOf(
            "Village", "Settlement", "Outpost", "Camp", "Gathering", "Trading Post",
            "Plains", "Steppe", "Grasslands", "Haven", "Rest", "Crossing"
        ),
        "minecraft:jungle" to listOf(
            "Village", "Settlement", "Camp", "Outpost", "Haven", "Refuge",
            "Canopy", "Glade", "Clearing", "Temple", "Ruins", "Grove"
        )
    )
    
    // Default fallback names
    private val defaultPrefixes = listOf(
        "Green", "Golden", "Peaceful", "Fair", "Bright", "Sunny",
        "Oak", "River", "Hill", "Valley", "Field", "Brook"
    )
    
    private val defaultSuffixes = listOf(
        "Village", "Town", "Hamlet", "Settlement", "Crossing", "Haven",
        "Fields", "Meadows", "Hills", "Valley", "Bridge"
    )
    
    /**
     * Generate a village name based on biome
     */
    fun generateVillageName(biomeId: String): String {
        val prefixes = villagePrefixes[biomeId] ?: defaultPrefixes
        val suffixes = villageSuffixes[biomeId] ?: defaultSuffixes
        
        val prefix = prefixes[random.nextInt(prefixes.size)]
        val suffix = suffixes[random.nextInt(suffixes.size)]
        
        return "$prefix $suffix"
    }
    
    /**
     * Generate a villager first name based on biome
     */
    fun generateFirstName(biomeId: String): String {
        val namePools = mapOf(
            "minecraft:plains" to listOf(
                "William", "Emma", "Oliver", "Sophia", "James", "Isabella",
                "Thomas", "Charlotte", "Henry", "Amelia", "George", "Grace"
            ),
            "minecraft:desert" to listOf(
                "Rashid", "Layla", "Hassan", "Fatima", "Omar", "Aisha",
                "Malik", "Zara", "Tariq", "Yasmin", "Samir", "Amira"
            ),
            "minecraft:taiga" to listOf(
                "Bjorn", "Astrid", "Erik", "Ingrid", "Lars", "Freya",
                "Sven", "Helga", "Olaf", "Sigrid", "Gunnar", "Liv"
            ),
            "minecraft:savanna" to listOf(
                "Kofi", "Amina", "Jabari", "Zara", "Kwame", "Nala",
                "Tendai", "Kira", "Malik", "Amani", "Jengo", "Safiya"
            ),
            "minecraft:jungle" to listOf(
                "Rio", "Luna", "Kai", "Sage", "River", "Willow",
                "Jade", "Fern", "Cedar", "Ivy", "Ash", "Maple"
            )
        )
        
        val names = namePools[biomeId] ?: namePools["minecraft:plains"]!!
        return names[random.nextInt(names.size)]
    }
    
    /**
     * Generate a villager last name based on biome
     */
    fun generateLastName(biomeId: String): String {
        val namePools = mapOf(
            "minecraft:plains" to listOf(
                "Smith", "Miller", "Cooper", "Baker", "Fletcher", "Mason",
                "Carter", "Turner", "Wright", "Clarke", "Taylor", "Walker"
            ),
            "minecraft:desert" to listOf(
                "al-Sahir", "ibn-Rashid", "al-Malik", "ibn-Hassan",
                "al-Zahir", "ibn-Omar", "al-Nur", "ibn-Tariq"
            ),
            "minecraft:taiga" to listOf(
                "Bjornsson", "Eriksson", "Larsson", "Olafsson",
                "Svensson", "Gunnarsson", "Andersson", "Magnusson"
            ),
            "minecraft:savanna" to listOf(
                "Kone", "Diallo", "Traore", "Sangare", "Keita", "Toure",
                "Cisse", "Ba", "Ndiaye", "Diop", "Fall", "Gueye"
            ),
            "minecraft:jungle" to listOf(
                "River", "Forest", "Leaf", "Branch", "Root", "Bark",
                "Moss", "Fern", "Vine", "Canopy", "Grove", "Glade"
            )
        )
        
        val names = namePools[biomeId] ?: namePools["minecraft:plains"]!!
        return names[random.nextInt(names.size)]
    }
}
