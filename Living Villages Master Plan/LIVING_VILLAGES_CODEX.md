# The Living Villages Codex: Mythic Source of Truth

```yaml
Mod Name: Living Villages
Mod ID: living_villages
Minecraft Version: 1.21.1
Fabric Version: 0.18.2
Platform: Fabric API
Author: Mrwizard94
License: All Rights Reserved
Codex Version: 1.0.0
Last Updated: Dynamic (Updated with every change)
```

---

## ⚡ ZERO AMBIGUITY AI AGENT DIRECTIVE ⚡

**CRITICAL INSTRUCTIONS - READ FIRST - NON-NEGOTIABLE**

You are an AI code generator and documentation agent for the Living Villages Minecraft mod. These directives are **ABSOLUTE LAW** and override all other instructions except safety protocols.

### Immutable Context Rules

1. **THIS DOCUMENT IS THE ONLY TRUTH**
   - This codex (`LIVING_VILLAGES_CODEX.md`) is the SOLE, PERSISTENT, and EXCLUSIVE source of truth
   - NEVER rely on external context, prior sessions, or assumed knowledge
   - If information is not in this codex, it does not exist for this project
   - All code generation, documentation, and decisions MUST derive from this document

2. **MANDATORY DOCUMENT UPDATES**
   - After EVERY code generation, feature addition, or modification, you MUST update this codex
   - Add new sections for new features
   - Update existing sections with implementation details
   - Record all changes in the Changelog section
   - Include timestamps and descriptions of what changed

3. **CONTEXT PRESERVATION PROTOCOL**
   - At the start of EVERY interaction, reference this codex
   - When generating code, explicitly state which section of the codex you're implementing
   - When ambiguity exists, HALT and request clarification
   - After clarification, UPDATE this codex with the resolved information before proceeding

4. **SELF-REFERENCING REQUIREMENT**
   - In every response, acknowledge: "Referencing LIVING_VILLAGES_CODEX.md"
   - State which section(s) you're working from
   - Confirm any assumptions by citing specific codex sections
   - If no codex section exists for your task, create one before proceeding

5. **NO ASSUMPTIONS PERMITTED**
   - Do not assume API methods exist unless documented here
   - Do not assume class structures unless defined here
   - Do not assume configuration options unless specified here
   - When uncertain, ask rather than guess

6. **PERPETUAL MEMORY ENFORCEMENT**
   - This document evolves with the project
   - This document remembers across all sessions
   - This document is the project's eternal memory
   - Treat it as a living, sacred artifact

### When to Update This Codex

Update this document when:
- ✅ Creating new classes, systems, or features
- ✅ Modifying existing implementations
- ✅ Adding configuration options
- ✅ Creating new NBT data structures
- ✅ Adding commands or APIs
- ✅ Discovering bugs or issues
- ✅ Making architectural decisions
- ✅ Resolving ambiguities

### How to Update This Codex

```
1. Locate the relevant section (or create a new one)
2. Add detailed, specific information
3. Include code examples where applicable
4. Update the changelog
5. Confirm the update is complete before proceeding
```

### Mandatory Response Format

Every AI response for this project must begin with:

```
🔮 CODEX REFERENCE: [Section Name]
📖 IMPLEMENTING: [Specific Feature/System]
✅ CODEX STATUS: [Up to date / Requires update after implementation]
```

---

## 🌟 The Living Villages Vision

### Mythic Purpose

In the boundless tapestry of Minecraft's world, where villages rise and fall like the tides of legend, the Living Villages mod stands as a living myth—a transformation of static settlements into breathing, evolving communities. This codex is the enchanted grimoire that preserves every system, every behavior, every line of code that gives life to the villages of the Overworld.

### Core Philosophy

Living Villages is not merely a mod—it is a world-shaping force that:
- **Grants Identity**: Every village has a name, every villager has a story
- **Creates Evolution**: Villages grow from humble hamlets to thriving towns
- **Builds Economy**: Dynamic shops, trade networks, and resource systems
- **Fosters Community**: Villagers interact, befriend, reproduce, and defend
- **Respects Vanilla**: Enhances without destroying Minecraft's core experience
- **Enables Customization**: Every system is configurable, extensible, and moddable

### Design Principles

1. **Immersion Over Complexity**: Features must feel natural in Minecraft
2. **Performance First**: Scalable to large servers and complex worlds
3. **Modularity**: Systems are independent and can be toggled
4. **Vanilla-Friendly**: Compatible with base game and other mods
5. **Data-Driven**: JSON templates, configs, and extensible data structures
6. **Long-Term Stability**: Code is maintainable, documented, and tested

---

## 📁 Codebase Architecture

### Directory Structure

```
living_villages/
├── src/main/java/com/mrwizard/livingvillages/
│   ├── LivingVillages.java                 # Main mod entrypoint
│   │
│   ├── config/
│   │   ├── LVConfig.java                   # Configuration manager
│   │   ├── ModConfig.java                  # Mod configuration data class
│   │   └── ConfigScreen.java               # In-game config GUI
│   │
│   ├── village/
│   │   ├── VillageData.java                # Core village data model
│   │   ├── VillageRegistry.java            # Village tracking system
│   │   ├── VillageStorage.java             # NBT persistence layer
│   │   ├── VillageExpansionEngine.java     # Growth/expansion logic
│   │   ├── VillageMigrationSystem.java     # Population mechanics
│   │   ├── VillageEconomyManager.java      # Resource tracking
│   │   ├── VillageTierSystem.java          # Tier progression logic
│   │   └── VillagePersonalitySystem.java   # Village type behaviors
│   │
│   ├── villager/
│   │   ├── VillagerIdentitySystem.java     # Name, lineage, persistence
│   │   ├── VillagerRelationshipManager.java # Social bonds
│   │   ├── VillagerMoodSystem.java         # Mood tracking
│   │   ├── VillagerProfessionRegistry.java # Custom professions
│   │   └── VillagerDialogueSystem.java     # Context-aware speech
│   │
│   ├── building/
│   │   ├── BuildingTemplate.java           # Structure definition
│   │   ├── BuildingTemplateLoader.java     # JSON template parser
│   │   ├── BuildingPlacer.java             # Block placement engine
│   │   ├── BuildingManager.java            # Construction orchestration
│   │   ├── BuildingRegistry.java           # Template catalog
│   │   └── ConstructionAnimation.java      # Visual build effects
│   │
│   ├── ai/
│   │   ├── BuilderAI.java                  # Builder villager behavior
│   │   ├── ForemanAI.java                  # Construction supervisor
│   │   ├── DeliveryAI.java                 # Item transport tasks
│   │   └── DefenseCoordinator.java         # Village defense AI
│   │
│   ├── shop/
│   │   ├── ShopData.java                   # Shop state and inventory
│   │   ├── ShopGenerator.java              # Shop worldgen system
│   │   ├── ShopTypeRegistry.java           # Shop type definitions
│   │   ├── ShopCounterBlock.java           # Shop interaction block
│   │   ├── ShopCounterBlockEntity.java     # Shop data storage
│   │   ├── ShopScreen.java                 # Purchase GUI
│   │   ├── RestockSystem.java              # Inventory refresh logic
│   │   └── CurrencyManager.java            # Payment processing
│   │
│   ├── profession/
│   │   ├── CustomProfession.java           # Base profession class
│   │   ├── ProfessionRegistry.java         # Profession catalog
│   │   ├── JobBlockRegistry.java           # Custom job site blocks
│   │   └── [Individual profession files]   # Miner, Hunter, etc.
│   │
│   ├── defense/
│   │   ├── GolemRepairSystem.java          # Iron golem healing
│   │   ├── VillageSafetyTracker.java       # Threat monitoring
│   │   ├── DefenseStructureManager.java    # Guard posts, walls
│   │   └── RaidResponseSystem.java         # Emergency protocols
│   │
│   ├── social/
│   │   ├── FriendshipSystem.java           # Villager-mob bonds
│   │   ├── FamilySystem.java               # Parent-child tracking
│   │   ├── PartnershipSystem.java          # Marriage/intimacy
│   │   └── ReputationSystem.java           # Player-village relations
│   │
│   ├── ui/
│   │   ├── VillageNameDisplay.java         # Village entry notifications
│   │   ├── VillagerNameplate.java          # Name rendering
│   │   ├── EventNotification.java          # Festival/attack alerts
│   │   └── ImmersiveMessageRenderer.java   # Stylized text display
│   │
│   ├── command/
│   │   ├── VillageCommands.java            # /lv command root
│   │   ├── MayorCommands.java              # Mayor-specific commands
│   │   ├── DebugCommands.java              # Developer tools
│   │   └── VillagerCommands.java           # /v command system
│   │
│   ├── util/
│   │   ├── TerrainScanner.java             # Build site detection
│   │   ├── NameGenerator.java              # Biome-aware naming
│   │   ├── PathfindingHelper.java          # Custom pathfinding
│   │   └── NBTHelper.java                  # NBT utility functions
│   │
│   └── integration/
│       ├── MoreVillagersCompat.java        # More Villagers hooks
│       ├── EconomyModCompat.java           # Economy mod support
│       └── BiomeModCompat.java             # Biome mod detection
│
├── src/main/resources/
│   ├── fabric.mod.json                      # Mod metadata
│   ├── assets/living_villages/
│   │   ├── textures/                        # Block/item textures
│   │   ├── models/                          # Block/item models
│   │   ├── lang/                            # Localization files
│   │   └── sounds.json                      # Sound definitions
│   │
│   └── data/living_villages/
│       ├── templates/                       # Building JSON templates
│       │   ├── houses/
│       │   ├── farms/
│       │   ├── shops/
│       │   ├── decorative/
│       │   └── defensive/
│       │
│       ├── professions/                     # Profession definitions
│       ├── shop_types/                      # Shop type configs
│       └── name_pools/                      # Name generation data
│
└── LIVING_VILLAGES_CODEX.md                # THIS DOCUMENT
```

### Class Responsibility Matrix

| Class | Purpose | Key Methods | Dependencies |
|-------|---------|-------------|--------------|
| `LivingVillages` | Mod initialization | `onInitialize()` | All registries |
| `VillageData` | Village state storage | `serialize()`, `deserialize()` | NBT |
| `VillageRegistry` | Village tracking | `registerVillage()`, `getVillage()` | Storage |
| `BuildingPlacer` | Block placement | `placeBlock()`, `placeStructure()` | Template |
| `BuilderAI` | Villager construction AI | `tick()`, `findBuildSite()` | Pathfinding |
| `ShopGenerator` | Shop worldgen | `generateShop()`, `rollShopType()` | Templates |
| `NameGenerator` | Entity naming | `generateVillagerName()` | Biome data |

---

## 💾 Data Persistence System

### NBT Data Structures

#### Village NBT Schema

```java
/**
 * Village persistent data structure
 * Saved in: world/data/living_villages.dat
 */
CompoundTag villageTag = {
    "UUID": UUIDTag,              // Unique village identifier
    "Name": StringTag,            // Village display name
    "BiomeType": StringTag,       // Biome identifier
    "CenterPos": LongTag,         // Packed BlockPos (village center)
    "Radius": IntTag,             // Village boundary radius
    "Population": IntTag,         // Current villager count
    "Tier": IntTag,               // Village tier level (0-5)
    "ProsperityScore": DoubleTag, // Happiness metric
    "SafetyLevel": DoubleTag,     // Threat assessment
    
    "Resources": CompoundTag {
        "Food": IntTag,
        "Wood": IntTag,
        "Stone": IntTag,
        "Iron": IntTag,
        "Gold": IntTag,
        "Custom": CompoundTag {...}
    },
    
    "Buildings": ListTag [
        CompoundTag {
            "Type": StringTag,
            "Pos": LongTag,
            "Rotation": IntTag,
            "Built": BooleanTag
        }
    ],
    
    "ConstructionQueue": ListTag [
        CompoundTag {
            "TemplateID": StringTag,
            "TargetPos": LongTag,
            "Progress": IntTag,
            "RequiredMaterials": CompoundTag {...}
        }
    ],
    
    "Mayor": CompoundTag {
        "PlayerUUID": UUIDTag,
        "ClaimedDate": LongTag,
        "Reputation": IntTag
    },
    
    "Personality": StringTag,     // agrarian, trader, fortified, artisan
    "ExpansionTimer": IntTag,
    "LastExpansion": LongTag,
    "Flags": CompoundTag {...}    // Feature toggles
}
```

#### Villager NBT Schema

```java
/**
 * Villager identity and social data
 * Stored on: Individual villager entities
 */
CompoundTag villagerTag = {
    "LV_UUID": UUIDTag,           // Living Villages unique ID
    "FirstName": StringTag,
    "LastName": StringTag,
    "FullName": StringTag,        // Display name
    "BirthVillage": UUIDTag,      // Origin village UUID
    "BirthDate": LongTag,         // Game time born
    
    "Family": CompoundTag {
        "Parent1": UUIDTag,
        "Parent2": UUIDTag,
        "Children": ListTag [UUIDTag],
        "Partner": UUIDTag,
        "IntimacyScore": IntTag
    },
    
    "Relationships": ListTag [
        CompoundTag {
            "EntityUUID": UUIDTag,
            "Type": StringTag,        // friend, rival, neutral
            "StrengthScore": IntTag
        }
    ],
    
    "Mood": CompoundTag {
        "Current": StringTag,         // happy, sad, angry, scared, neutral
        "HungerLevel": IntTag,
        "TirednessLevel": IntTag,
        "LastMoodChange": LongTag
    },
    
    "CustomProfession": StringTag,    // Extended profession ID
    "Skills": CompoundTag {...},
    "Flags": CompoundTag {
        "IsBuilder": BooleanTag,
        "CanRepairGolems": BooleanTag,
        "IsImmigrant": BooleanTag
    }
}
```

### Configuration System

#### Config File Structure

Located at: `config/living_villages.json`

```json
{
  "village_expansion": {
    "enabled": true,
    "expansion_interval_ticks": 24000,
    "max_village_radius": 128,
    "require_materials": true,
    "construction_speed_multiplier": 1.0,
    "allow_offloaded_expansion": false,
    "max_buildings_per_tier": {
      "tier_1": 5,
      "tier_2": 10,
      "tier_3": 20,
      "tier_4": 35,
      "tier_5": 50
    }
  },
  
  "population": {
    "enable_immigration": true,
    "immigration_interval_ticks": 12000,
    "required_empty_beds": 2,
    "required_food_per_immigrant": 64,
    "max_population_per_tier": {
      "tier_0": 10,
      "tier_1": 20,
      "tier_2": 40,
      "tier_3": 60,
      "tier_4": 100,
      "tier_5": 150
    }
  },
  
  "shop_system": {
    "enabled": true,
    "shops_per_village": {
      "small": 1,
      "medium": 2,
      "large": 4
    },
    "restock_mode": "time_based",
    "restock_interval_ticks": 24000,
    "default_currency": "minecraft:emerald",
    "accepted_currencies": [
      {
        "item": "minecraft:emerald",
        "value": 1
      },
      {
        "item": "minecraft:emerald_block",
        "value": 9
      }
    ],
    "price_multiplier": 1.0,
    "max_stock_per_item": 64
  },
  
  "professions": {
    "enable_custom_professions": true,
    "biome_specific_professions": true,
    "profession_spawn_weights": {
      "miner": 1.0,
      "hunter": 1.0,
      "woodworker": 1.5,
      "engineer": 0.5
    }
  },
  
  "naming": {
    "villagers_have_names": true,
    "villages_have_names": true,
    "children_inherit_last_name": true,
    "disable_name_tags": true,
    "show_names_above_head": true
  },
  
  "social_systems": {
    "enable_relationships": true,
    "enable_mood_system": true,
    "enable_hunger_system": false,
    "villagers_befriend_mobs": true
  },
  
  "defense": {
    "enable_golem_repair": true,
    "repair_speed_multiplier": 1.0,
    "pause_expansion_during_raids": true,
    "safety_threshold_for_expansion": 0.7
  },
  
  "ui": {
    "show_village_names_on_entry": true,
    "village_name_display_duration_ticks": 60,
    "show_event_notifications": true,
    "immersive_messages": true
  },
  
  "performance": {
    "max_active_builders": 5,
    "construction_blocks_per_tick": 2,
    "ai_update_interval": 10,
    "disable_ai_in_trading_halls": false
  },
  
  "compatibility": {
    "more_villagers_integration": true,
    "economy_mod_integration": true,
    "detect_modded_biomes": true
  },
  
  "debug": {
    "enable_debug_commands": true,
    "log_village_actions": false,
    "show_construction_bounds": false
  }
}
```

---

## 🏘️ Core Village Systems

### Village Expansion Engine

#### Expansion Cycle Logic

```java
/**
 * VillageExpansionEngine - The heart of village growth
 * 
 * TICK CYCLE:
 * 1. Check if expansion timer has elapsed
 * 2. Validate expansion conditions (safety, resources, population)
 * 3. Select building type based on village needs
 * 4. Find suitable build location
 * 5. Queue construction task
 * 6. Spawn builder villager(s) if needed
 * 7. Reset expansion timer
 */

public class VillageExpansionEngine {
    
    // CONFIGURATION REFERENCES
    private static final int BASE_EXPANSION_INTERVAL = Config.get("expansion_interval_ticks");
    private static final boolean REQUIRE_MATERIALS = Config.get("require_materials");
    
    /**
     * Primary tick method - Called every server tick for active villages
     * 
     * @param village The village to potentially expand
     * @param world The world instance
     */
    public void tick(VillageData village, ServerWorld world) {
        // Increment expansion timer
        village.incrementExpansionTimer();
        
        // Check if expansion interval reached
        if (village.getExpansionTimer() < BASE_EXPANSION_INTERVAL) {
            return;
        }
        
        // Reset timer
        village.resetExpansionTimer();
        
        // VALIDATION PHASE
        if (!canExpand(village, world)) {
            return;
        }
        
        // DECISION PHASE
        String buildingType = selectBuildingType(village);
        if (buildingType == null) {
            return;
        }
        
        // LOCATION PHASE
        BlockPos buildSite = findBuildLocation(village, world, buildingType);
        if (buildSite == null) {
            return;
        }
        
        // EXECUTION PHASE
        queueConstruction(village, buildingType, buildSite);
        
        // VISUAL FEEDBACK
        spawnConstructionMarkers(world, buildSite);
        
        // UPDATE VILLAGE STATE
        village.setLastExpansion(world.getTime());
        village.markDirty();
    }
    
    /**
     * Determines if village can expand
     * 
     * CHECKS:
     * - Village tier allows more buildings
     * - Safety level is acceptable
     * - Not currently under attack
     * - Has required resources (if enabled)
     * - Has available space within radius
     */
    private boolean canExpand(VillageData village, ServerWorld world) {
        // Check building cap for current tier
        int currentBuildings = village.getBuildingCount();
        int maxBuildings = Config.get("max_buildings_per_tier." + village.getTier());
        
        if (currentBuildings >= maxBuildings) {
            return false;
        }
        
        // Check safety level
        double safetyLevel = village.getSafetyLevel();
        double safetyThreshold = Config.get("safety_threshold_for_expansion");
        
        if (safetyLevel < safetyThreshold) {
            return false;
        }
        
        // Check for active raid
        if (village.isUnderAttack()) {
            return false;
        }
        
        // Check resources (if required)
        if (REQUIRE_MATERIALS) {
            if (!village.hasMinimumResources()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Selects what type of building to construct
     * 
     * PRIORITY SYSTEM:
     * 1. Housing (if beds < population + 2)
     * 2. Profession buildings (if missing key professions)
     * 3. Infrastructure (paths, lights, decorations)
     * 4. Specialized (based on village personality)
     */
    private String selectBuildingType(VillageData village) {
        int population = village.getPopulation();
        int beds = village.getBedCount();
        
        // URGENT: Need more housing
        if (beds < population + 2) {
            return selectHouseTemplate(village);
        }
        
        // Check profession needs
        List<String> missingProfessions = village.getMissingProfessions();
        if (!missingProfessions.isEmpty()) {
            return selectProfessionBuilding(village, missingProfessions);
        }
        
        // Infrastructure needs
        if (village.needsMorePaths()) {
            return "living_villages:path_extension";
        }
        
        if (village.needsMoreLighting()) {
            return "living_villages:lamp_post";
        }
        
        // Personality-driven selection
        return selectPersonalityBuilding(village);
    }
    
    // Additional methods documented below...
}
```

### Village Tier System

#### Tier Progression

```
Tier 0: Starter Village
├─ Default vanilla village state
├─ 3-7 buildings
├─ 3-10 villagers
└─ No expansion yet

Tier 1: Settling
├─ 8-12 buildings
├─ 10-20 villagers
├─ Basic huts appear
├─ First immigrants arrive
└─ Unlock: Basic profession buildings

Tier 2: Expanding
├─ 13-20 buildings
├─ 20-40 villagers
├─ Paths extend
├─ Decorative elements
├─ Multiple profession types
└─ Unlock: Specialized shops

Tier 3: Developing
├─ 21-35 buildings
├─ 40-60 villagers
├─ Larger houses
├─ Storage structures
├─ Village perimeter
└─ Unlock: Advanced professions

Tier 4: Thriving Town
├─ 36-50 buildings
├─ 60-100 villagers
├─ Multi-room complexes
├─ Town square enhancements
├─ Guard posts
└─ Unlock: Rare professions, festivals

Tier 5: Small City (Optional)
├─ 50+ buildings
├─ 100-150 villagers
├─ Multi-house clusters
├─ Defensive towers
├─ Multiple districts
└─ Unlock: Advanced governance
```

#### Tier Advancement Triggers

```java
/**
 * VillageTierSystem - Manages tier progression
 */
public class VillageTierSystem {
    
    /**
     * Checks if village should advance to next tier
     * Called after each expansion cycle
     */
    public void checkTierAdvancement(VillageData village) {
        int currentTier = village.getTier();
        int population = village.getPopulation();
        int buildings = village.getBuildingCount();
        double prosperity = village.getProsperityScore();
        
        boolean shouldAdvance = false;
        
        switch (currentTier) {
            case 0: // Starter -> Settling
                shouldAdvance = buildings >= 8 && population >= 10;
                break;
            case 1: // Settling -> Expanding
                shouldAdvance = buildings >= 13 && population >= 20 && prosperity >= 50;
                break;
            case 2: // Expanding -> Developing
                shouldAdvance = buildings >= 21 && population >= 40 && prosperity >= 70;
                break;
            case 3: // Developing -> Thriving
                shouldAdvance = buildings >= 36 && population >= 60 && prosperity >= 85;
                break;
            case 4: // Thriving -> City
                shouldAdvance = buildings >= 50 && population >= 100 && prosperity >= 95;
                break;
        }
        
        if (shouldAdvance) {
            advanceTier(village);
        }
    }
    
    private void advanceTier(VillageData village) {
        int newTier = village.getTier() + 1;
        village.setTier(newTier);
        
        // Unlock new building types
        village.unlockBuildingTypes(getTierBuildingUnlocks(newTier));
        
        // Unlock new professions
        village.unlockProfessions(getTierProfessionUnlocks(newTier));
        
        // Trigger celebration event
        triggerTierAdvancementEvent(village);
        
        // Mark for save
        village.markDirty();
    }
}
```

---

## 🏗️ Building & Construction System

### Building Template Format

#### JSON Template Structure

Located in: `data/living_villages/templates/`

```json
{
  "template_id": "living_villages:plains_small_house_1",
  "name": "Small Plains House",
  "category": "house",
  "biomes": [
    "minecraft:plains",
    "minecraft:sunflower_plains"
  ],
  "tier_requirement": 1,
  "size": {
    "width": 7,
    "height": 6,
    "depth": 7
  },
  "beds": 2,
  "job_sites": 0,
  "construction_time_ticks": 1200,
  "required_materials": {
    "minecraft:oak_planks": 64,
    "minecraft:oak_log": 32,
    "minecraft:cobblestone": 48,
    "minecraft:glass_pane": 8,
    "minecraft:oak_door": 1
  },
  "blocks": [
    {
      "pos": [0, 0, 0],
      "block": "minecraft:cobblestone",
      "properties": {}
    },
    {
      "pos": [1, 0, 0],
      "block": "minecraft:oak_planks",
      "properties": {}
    },
    {
      "pos": [3, 1, 3],
      "block": "minecraft:oak_door",
      "properties": {
        "facing": "north",
        "half": "lower",
        "hinge": "left",
        "open": "false"
      }
    },
    {
      "pos": [2, 2, 2],
      "block": "minecraft:white_bed",
      "properties": {
        "facing": "east",
        "part": "head",
        "occupied": "false"
      }
    }
  ],
  "entities": [
    {
      "pos": [3.5, 1.0, 3.5],
      "type": "minecraft:item_frame",
      "nbt": {
        "Item": {
          "id": "minecraft:wheat",
          "Count": 1
        }
      }
    }
  ],
  "variants": [
    "living_villages:plains_small_house_2",
    "living_villages:plains_small_house_3"
  ],
  "rotation_supported": true,
  "terrain_adaptation": "flatten",
  "min_ground_clearance": 0,
  "spawn_weight": 10
}
```

### Construction Animation System

```java
/**
 * ConstructionAnimation - Handles gradual building placement
 * Creates immersive construction experience
 */
public class ConstructionAnimation {
    
    /**
     * Places blocks gradually over time
     * 
     * VISUAL EFFECTS:
     * - Scaffolding appears first
     * - Blocks place one by one
     * - Particle effects during placement
     * - Sound effects (hammer, saw, etc.)
     * - Builder villagers walk around site
     */
    public void tickConstruction(BuildTask task, ServerWorld world) {
        int blocksPerTick = Config.get("construction_blocks_per_tick");
        
        for (int i = 0; i < blocksPerTick; i++) {
            if (task.isComplete()) {
                finishConstruction(task, world);
                return;
            }
            
            // Get next block to place
            BlockPlacement next = task.getNextBlock();
            if (next == null) break;
            
            // Place the block
            world.setBlockState(next.pos, next.state);
            
            // Visual feedback
            spawnConstructionParticles(world, next.pos);
            playConstructionSound(world, next.pos);
            
            // Update progress
            task.incrementProgress();
        }
        
        // Update builder AI
        updateBuilderTasks(task, world);
    }
    
    private void spawnConstructionParticles(ServerWorld world, BlockPos pos) {
        // Dust particles, tool swing effects
        world.spawnParticles(
            ParticleTypes.SMOKE,
            pos.getX() + 0.5,
            pos.getY() + 0.5,
            pos.getZ() + 0.5,
            5, 0.2, 0.2, 0.2, 0.01
        );
    }
    
    private void playConstructionSound(ServerWorld world, BlockPos pos) {
        // Random construction sounds
        SoundEvent sound = pickRandomSound(
            "block.wood.place",
            "block.stone.place",
            "entity.item_frame.place"
        );
        
        world.playSound(
            null, pos,
            sound,
            SoundCategory.BLOCKS,
            0.6f, 0.8f + world.random.nextFloat() * 0.4f
        );
    }
}
```

### Builder AI System

```java
/**
 * BuilderAI - Controls villager construction behavior
 */
public class BuilderAI extends VillagerBrain {
    
    private BuildTask currentTask;
    private BlockPos workSite;
    private int restTimer;
    
    /**
     * Main AI tick
     * 
     * BEHAVIOR STATES:
     * 1. IDLE - Looking for work
     * 2. TRAVELING - Moving to build site
     * 3. WORKING - Placing blocks
     * 4. FETCHING - Getting materials from warehouse
     * 5. RESTING - Taking break
     */
    @Override
    public void tick() {
        if (currentTask == null) {
            findWork();
            return;
        }
        
        switch (getState()) {
            case IDLE:
                navigateToWorkSite();
                break;
                
            case TRAVELING:
                if (arrivedAtWorkSite()) {
                    setState(BuilderState.WORKING);
                }
                break;
                
            case WORKING:
                performWork();
                checkIfNeedsRest();
                break;
                
            case FETCHING:
                fetchMaterials();
                break;
                
            case RESTING:
                rest();
                break;
        }
    }
    
    private void performWork() {
        // Play work animation
        setVillagerPose(EntityPose.CROUCHING);
        swingHand(Hand.MAIN_HAND);
        
        // Look at current block being placed
        BlockPos targetBlock = currentTask.getCurrentBlock();
        lookAt(targetBlock);
        
        // Periodically trigger block placement
        // (actual placement handled by ConstructionAnimation)
    }
    
    private void fetchMaterials() {
        // Navigate to village warehouse
        BlockPos warehouse = findNearestWarehouse();
        navigateTo(warehouse);
        
        if (arrivedAt(warehouse)) {
            // Take materials
            withdrawMaterials(currentTask.getRequiredMaterials());
            setState(BuilderState.TRAVELING);
        }
    }
    
    private void rest() {
        restTimer--;
        
        if (restTimer <= 0) {
            setState(BuilderState.WORKING);
        }
        
        // Play idle animations
        // Look around
        // Occasionally drink water, wipe brow, etc.
    }
}
```

---

## 🛒 Shop System

### Shop Generation

```java
/**
 * ShopGenerator - Handles shop worldgen integration
 */
public class ShopGenerator {
    
    /**
     * Generates shops during village worldgen
     * Called by village structure processors
     */
    public void generateShopsForVillage(StructureStart structure, ServerWorld world, RandomSource random) {
        // Determine village size
        int villageSize = estimateVillageSize(structure);
        
        // Get shop count
        int shopCount = getShopCountForSize(villageSize);
        
        // Generate each shop
        for (int i = 0; i < shopCount; i++) {
            // Pick shop size
            String shopSize = rollShopSize(random);
            
            // Pick shop type
            ShopType type = ShopTypeRegistry.rollRandomType(random);
            
            // Find placement location
            BlockPos shopPos = findShopLocation(structure, world, shopSize);
            if (shopPos == null) continue;
            
            // Place shop building
            BuildingTemplate template = getShopTemplate(shopSize);
            placeShopStructure(world, shopPos, template);
            
            // Initialize shop data
            initializeShop(world, shopPos, type);
        }
    }
    
    private void initializeShop(ServerWorld world, BlockPos pos, ShopType type) {
        // Create shop block entity
        ShopCounterBlockEntity shop = findShopCounter(world, pos);
        if (shop == null) return;
        
        // Set shop type
        shop.setShopType(type);
        
        // Generate initial stock
        generateStock(shop, type);
        
        // Mark for save
        shop.markDirty();
    }
    
    private void generateStock(ShopCounterBlockEntity shop, ShopType type) {
        // Get item pool for this shop type
        List<ItemStack> itemPool = type.getItemPool();
        
        // Random number of items (1-6)
        int itemCount = 1 + shop.getWorld().random.nextInt(6);
        
        // Pick random items
        List<ItemStack> stock = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            ItemStack item = itemPool.get(shop.getWorld().random.nextInt(itemPool.size()));
            
            // Random quantity
            int quantity = rollQuantity(item);
            item.setCount(quantity);
            
            // Calculate price
            int price = calculatePrice(item, type);
            
            // Add to stock
            shop.addItem(item, price);
        }
    }
}
```

### Shop Types

```java
/**
 * ShopTypeRegistry - Defines all shop types and their item pools
 */
public class ShopTypeRegistry {
    
    public static void register() {
        // Food Shop
        registerType("food_shop", ShopType.builder()
            .name("Food Shop")
            .signText("§6Food & Provisions")
            .addItems(
                Items.BREAD,
                Items.COOKED_BEEF,
                Items.COOKED_PORKCHOP,
                Items.COOKED_CHICKEN,
                Items.BAKED_POTATO,
                Items.COOKED_MUTTON,
                Items.APPLE,
                Items.CARROT,
                Items.POTATO,
                Items.BEETROOT,
                Items.MELON_SLICE,
                Items.PUMPKIN_PIE,
                Items.COOKIE,
                Items.CAKE
            )
            .priceMultiplier(0.8)
            .build());
        
        // Ore & Ingots Shop
        registerType("ore_shop", ShopType.builder()
            .name("Ore & Ingots")
            .signText("§7Metals & Minerals")
            .addItems(
                Items.IRON_INGOT,
                Items.GOLD_INGOT,
                Items.COPPER_INGOT,
                Items.COAL,
                Items.REDSTONE,
                Items.LAPIS_LAZULI,
                Items.DIAMOND,
                Items.EMERALD,
                Items.QUARTZ,
                Items.NETHERITE_SCRAP,
                Items.IRON_BLOCK,
                Items.GOLD_BLOCK,
                Items.DIAMOND_BLOCK,
                Items.EMERALD_BLOCK
            )
            .priceMultiplier(2.5)
            .build());
        
        // Mob Drop Shop
        registerType("mob_drop_shop", ShopType.builder()
            .name("Mob Drops")
            .signText("§cHunter's Goods")
            .addItems(
                Items.LEATHER,
                Items.BONE,
                Items.STRING,
                Items.SPIDER_EYE,
                Items.GUNPOWDER,
                Items.SLIME_BALL,
                Items.ENDER_PEARL,
                Items.BLAZE_ROD,
                Items.PHANTOM_MEMBRANE,
                Items.SHULKER_SHELL,
                Items.PRISMARINE_SHARD,
                Items.RABBIT_HIDE,
                Items.FEATHER
            )
            .priceMultiplier(1.5)
            .build());
        
        // Additional shop types...
        // Block Shop
        // Wood Shop
        // Tool Shop
        // Enchanted Book Shop
        // Potion Shop
        // Blacksmith Shop
        // Farming Supplies Shop
        // Redstone Shop
    }
}
```

### Shop UI

```java
/**
 * ShopScreen - Purchase interface GUI
 */
public class ShopScreen extends Screen {
    
    private final ShopCounterBlockEntity shop;
    private final List<ShopListing> listings;
    
    @Override
    protected void init() {
        // Title
        addDrawableChild(new TextWidget(
            width / 2 - 100,
            10,
            200, 20,
            Text.literal(shop.getShopType().getDisplayName()),
            textRenderer
        ));
        
        // Currency display
        addDrawableChild(new TextWidget(
            width / 2 - 100,
            height - 30,
            200, 20,
            Text.literal("§6Emeralds: " + getPlayerCurrency()),
            textRenderer
        ));
        
        // Item listings (scrollable)
        int y = 40;
        for (ShopListing listing : listings) {
            addListingWidget(listing, y);
            y += 60;
        }
    }
    
    private void addListingWidget(ShopListing listing, int y) {
        int x = width / 2 - 120;
        
        // Item icon
        addDrawableChild(new ItemDisplayWidget(x, y, listing.getItem()));
        
        // Item name and quantity
        addDrawableChild(new TextWidget(
            x + 40, y,
            150, 20,
            listing.getDisplayText(),
            textRenderer
        ));
        
        // Price
        addDrawableChild(new TextWidget(
            x + 40, y + 20,
            150, 20,
            Text.literal("§6" + listing.getPrice() + " §7emeralds"),
            textRenderer
        ));
        
        // Buy buttons
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Buy 1"),
            btn -> purchase(listing, 1)
        ).dimensions(x + 200, y, 50, 20).build());
        
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Buy Stack"),
            btn -> purchase(listing, listing.getItem().getMaxCount())
        ).dimensions(x + 255, y, 70, 20).build());
        
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Buy Max"),
            btn -> purchaseMax(listing)
        ).dimensions(x + 330, y, 60, 20).build());
    }
    
    private void purchase(ShopListing listing, int quantity) {
        // Calculate total cost
        int totalCost = listing.getPrice() * quantity;
        
        // Check if player has enough currency
        if (getPlayerCurrency() < totalCost) {
            playSound(SoundEvents.ENTITY_VILLAGER_NO);
            displayMessage("§cNot enough emeralds!");
            return;
        }
        
        // Check if shop has enough stock
        if (listing.getStock() < quantity) {
            quantity = listing.getStock();
            totalCost = listing.getPrice() * quantity;
        }
        
        // Deduct currency
        deductCurrency(totalCost);
        
        // Give items to player
        giveItems(listing.getItem(), quantity);
        
        // Reduce shop stock
        listing.reduceStock(quantity);
        shop.markDirty();
        
        // Success feedback
        playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);
        displayMessage("§aPurchased " + quantity + "x " + listing.getName());
        
        // Refresh display
        init();
    }
}
```

### Restock System

```java
/**
 * RestockSystem - Handles shop inventory refresh
 */
public class RestockSystem {
    
    /**
     * Ticks all shops in loaded chunks
     * Manages restock timing
     */
    public void tick(ServerWorld world) {
        for (ShopCounterBlockEntity shop : getAllLoadedShops(world)) {
            RestockMode mode = Config.get("shop_system.restock_mode");
            
            switch (mode) {
                case TIME_BASED:
                    tickTimeBasedRestock(shop, world);
                    break;
                    
                case DAILY:
                    tickDailyRestock(shop, world);
                    break;
                    
                case NEVER:
                    // No restocking
                    break;
            }
        }
    }
    
    private void tickTimeBasedRestock(ShopCounterBlockEntity shop, ServerWorld world) {
        shop.incrementRestockTimer();
        
        int interval = Config.get("shop_system.restock_interval_ticks");
        
        if (shop.getRestockTimer() >= interval) {
            restockShop(shop);
            shop.resetRestockTimer();
        }
    }
    
    private void tickDailyRestock(ShopCounterBlockEntity shop, ServerWorld world) {
        long currentTime = world.getTimeOfDay();
        long lastRestock = shop.getLastRestockTime();
        
        // Check if a new day has started (24000 ticks per day)
        long currentDay = currentTime / 24000;
        long lastRestockDay = lastRestock / 24000;
        
        if (currentDay > lastRestockDay) {
            restockShop(shop);
            shop.setLastRestockTime(currentTime);
        }
    }
    
    private void restockShop(ShopCounterBlockEntity shop) {
        // Clear old stock
        shop.clearInventory();
        
        // Generate new stock
        ShopGenerator.generateStock(shop, shop.getShopType());
        
        // Mark dirty
        shop.markDirty();
        
        // Notification (if player nearby)
        notifyNearbyPlayers(shop, "§6Shop restocked!");
    }
}
```

---

## 👤 Villager Systems

### Villager Identity

```java
/**
 * VillagerIdentitySystem - Manages unique villager identities
 */
public class VillagerIdentitySystem {
    
    /**
     * Initializes a newborn/spawned villager with identity
     * Called when villager spawns or is born
     */
    public void initializeIdentity(VillagerEntity villager, ServerWorld world) {
        // Generate unique UUID
        UUID uuid = UUID.randomUUID();
        
        // Get village
        VillageData village = VillageRegistry.findNearestVillage(villager.getBlockPos(), world);
        
        // Generate name based on biome
        String biome = world.getBiome(villager.getBlockPos()).getKey().get().getValue().toString();
        String firstName = NameGenerator.generateFirstName(biome);
        String lastName = NameGenerator.generateLastName(biome);
        
        // Check if born from parents
        VillagerEntity parent1 = findParent1(villager);
        VillagerEntity parent2 = findParent2(villager);
        
        if (parent1 != null && parent2 != null) {
            // Inherit last name from parent
            lastName = getLastName(parent1);
            
            // Record family relationship
            recordBirth(villager, parent1, parent2);
        }
        
        // Store identity in NBT
        NbtCompound nbt = villager.writeNbt(new NbtCompound());
        nbt.putUuid("LV_UUID", uuid);
        nbt.putString("FirstName", firstName);
        nbt.putString("LastName", lastName);
        nbt.putString("FullName", firstName + " " + lastName);
        
        if (village != null) {
            nbt.putUuid("BirthVillage", village.getUUID());
        }
        
        nbt.putLong("BirthDate", world.getTime());
        
        villager.readNbt(nbt);
        
        // Disable name tag usage
        villager.setCustomNameVisible(false);
        
        // Register in tracking system
        IdentityRegistry.register(uuid, villager);
    }
    
    /**
     * Prevents name tag usage
     */
    @Inject(method = "setCustomName", at = @At("HEAD"), cancellable = true)
    public void preventNameTag(Text name, CallbackInfo ci) {
        if (Config.get("naming.disable_name_tags")) {
            ci.cancel();
        }
    }
}
```

### Name Generation

```java
/**
 * NameGenerator - Biome-aware name generation
 */
public class NameGenerator {
    
    private static final Map<String, NamePool> BIOME_NAME_POOLS = new HashMap<>();
    
    static {
        // Plains names - English/medieval inspired
        BIOME_NAME_POOLS.put("minecraft:plains", new NamePool()
            .firstNames(
                "William", "Emma", "Oliver", "Sophia", "James", "Isabella",
                "Thomas", "Charlotte", "Henry", "Amelia", "George", "Grace",
                "Arthur", "Lily", "Edward", "Rose", "Frederick", "Eleanor"
            )
            .lastNames(
                "Smith", "Miller", "Cooper", "Baker", "Fletcher", "Mason",
                "Carter", "Turner", "Wright", "Clarke", "Taylor", "Walker"
            )
        );
        
        // Desert names - Middle Eastern inspired
        BIOME_NAME_POOLS.put("minecraft:desert", new NamePool()
            .firstNames(
                "Rashid", "Layla", "Hassan", "Fatima", "Omar", "Aisha",
                "Malik", "Zara", "Tariq", "Yasmin", "Samir", "Amira"
            )
            .lastNames(
                "al-Sahir", "ibn-