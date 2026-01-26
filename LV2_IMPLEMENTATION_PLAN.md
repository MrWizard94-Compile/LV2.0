# Living Villages 2.0 - Implementation Plan
**Generated:** December 21, 2024  
**Status:** Architecture Planning Phase  
**Based On:** Living Villages Codex v1.0

---

## 🎯 Executive Summary

Living Villages 2.0 is a **comprehensive village enhancement mod** for Minecraft 1.21.1 (Fabric). The codex defines 8 major systems with ~40 classes. This document provides a **phased implementation strategy** to build the mod incrementally and avoid the pitfalls of v1.

### Key Insight from V1 Failure:
**Building generation was broken** - likely due to:
- Complex interdependencies between systems
- Insufficient testing of core placement logic
- Missing foundation before building features

### V2 Strategy:
**Build from the bottom up:**
1. Foundation first (data structures, persistence)
2. Core village tracking (before expansion)
3. Simple building placement (before animation)
4. Test each layer before adding the next

---

## 📊 System Architecture Overview

### Core Systems (Priority Order)

```
Layer 1: FOUNDATION (Week 1-2)
├── Configuration System
├── NBT Data Structures  
├── Village Data Model
└── Village Registry & Storage

Layer 2: CORE MECHANICS (Week 3-4)
├── Village Detection & Tracking
├── Basic Building Templates
├── Building Placement Engine (NO ANIMATION YET)
└── Terrain Scanner

Layer 3: EXPANSION (Week 5-6)
├── Village Expansion Logic
├── Tier System
├── Resource Management
└── Building Queue

Layer 4: CONSTRUCTION (Week 7-8)
├── Construction Animation
├── Builder AI
├── Material Requirements
└── Visual Feedback

Layer 5: VILLAGER SYSTEMS (Week 9-10)
├── Villager Identity
├── Name Generation
├── Custom Professions
└── Job Site Blocks

Layer 6: ADVANCED FEATURES (Week 11-12)
├── Shop System
├── Reputation System
├── Golem Repair
└── UI Notifications

Layer 7: SOCIAL & POLISH (Week 13+)
├── Relationships
├── Mood System
├── Dialogue System
└── Final polish
```

---

## 🏗️ Phase 1: Foundation (Critical - Do This First!)

### Goal: Get data persistence and village tracking working

### Classes to Implement:

**1. Configuration System** (`config/`)
```kotlin
LVConfig.kt                  // Configuration manager
ModConfig.kt                 // Data class for config values
```

**Purpose:**
- Load/save config from `config/living_villages.json`
- Provide global access to settings
- Handle config validation

**Implementation Priority:** ⭐⭐⭐⭐⭐ (Do First!)

**Complexity:** Low
**Risk:** Low
**Dependencies:** None

---

**2. NBT Data Structures** (`village/`)
```kotlin
VillageData.kt              // Core village state
```

**Purpose:**
- Represent a village in memory
- Serialize/deserialize to NBT
- Track all village properties (name, tier, population, buildings, etc.)

**Key Fields:**
```kotlin
data class VillageData(
    val uuid: UUID,
    var name: String,
    var biomeType: String,
    var centerPos: BlockPos,
    var radius: Int,
    var population: Int,
    var tier: Int,
    var prosperityScore: Double,
    var safetyLevel: Double,
    val buildings: MutableList<Building>,
    val constructionQueue: MutableList<BuildTask>,
    // ... etc
)
```

**Implementation Priority:** ⭐⭐⭐⭐⭐ (Do First!)

**Complexity:** Medium
**Risk:** Low
**Dependencies:** None

---

**3. Village Registry & Storage** (`village/`)
```kotlin
VillageRegistry.kt          // In-memory village tracking
VillageStorage.kt           // NBT persistence layer
```

**Purpose:**
- Track all villages in the world
- Save/load village data to `world/data/living_villages.dat`
- Provide lookup methods (get village by UUID, find nearest village, etc.)

**Implementation Priority:** ⭐⭐⭐⭐⭐ (Do First!)

**Complexity:** Medium
**Risk:** Medium (NBT corruption = data loss)
**Dependencies:** VillageData

---

### Phase 1 Completion Criteria:
✅ Config loads from JSON  
✅ VillageData serializes/deserializes perfectly  
✅ Villages persist across world saves  
✅ Can create/register/retrieve villages  
✅ **TEST:** Create village, save world, reload, village still exists with correct data

**Estimated Time:** 3-5 days
**Testing Priority:** CRITICAL

---

## 🔍 Phase 2: Village Detection & Basic Building (Fix V1's Core Issue!)

### Goal: Detect vanilla villages and place simple buildings correctly

### Classes to Implement:

**4. Village Detection**
```kotlin
VillageDetector.kt          // Find vanilla villages
VillageInitializer.kt       // Convert vanilla → LV village
```

**Purpose:**
- Scan world for vanilla village structures
- Create VillageData for each one
- Determine village center, radius, tier
- Register with VillageRegistry

**Implementation Priority:** ⭐⭐⭐⭐⭐

**Complexity:** Medium
**Risk:** Low
**Dependencies:** VillageData, VillageRegistry

---

**5. Building Template System** (`building/`)
```kotlin
BuildingTemplate.kt         // Structure definition
BuildingTemplateLoader.kt   // JSON parser
BuildingRegistry.kt         // Template catalog
```

**Purpose:**
- Load building templates from JSON
- Parse block lists, rotations, variants
- Provide templates for building placement

**JSON Format (from codex):**
```json
{
  "template_id": "livingvillages:plains_house_1",
  "size": { "width": 7, "height": 6, "depth": 7 },
  "blocks": [
    { "pos": [0, 0, 0], "block": "minecraft:cobblestone" }
  ]
}
```

**Implementation Priority:** ⭐⭐⭐⭐⭐

**Complexity:** Medium
**Risk:** Low
**Dependencies:** None

---

**6. Building Placement Engine** (`building/`)
```kotlin
BuildingPlacer.kt           // Core placement logic
TerrainScanner.kt           // Build site validation
```

**Purpose:**
- Place buildings block-by-block
- Handle rotations correctly
- Flatten/adapt terrain
- **THIS IS WHERE V1 FAILED - GET THIS RIGHT!**

**Critical Features:**
- Accurate block placement
- Proper rotation handling
- Terrain validation
- Collision detection

**Implementation Priority:** ⭐⭐⭐⭐⭐ (CRITICAL!)

**Complexity:** **HIGH**
**Risk:** **HIGH** (This was broken in V1)
**Dependencies:** BuildingTemplate, TerrainScanner

**Testing Strategy:**
1. Start with tiny 3x3x3 building
2. Test all 4 rotations
3. Test on flat terrain
4. Test on hills
5. Test near water
6. Test near other buildings
7. **Only move on when 100% working**

---

### Phase 2 Completion Criteria:
✅ Vanilla villages detected and registered  
✅ Building templates load from JSON  
✅ Can place a simple house correctly  
✅ Rotation works (all 4 directions)  
✅ Terrain adaptation works  
✅ **TEST:** Place 10 buildings manually - all look correct  
✅ **TEST:** Buildings don't overlap or generate in invalid spots

**Estimated Time:** 5-7 days
**Testing Priority:** CRITICAL (Don't skip this!)

---

## 🌱 Phase 3: Village Expansion Logic

### Goal: Villages grow automatically

### Classes to Implement:

**7. Expansion Engine** (`village/`)
```kotlin
VillageExpansionEngine.kt   // Growth logic
VillageTierSystem.kt        // Tier progression
```

**Purpose:**
- Tick villages periodically
- Check expansion conditions (safety, resources, timer)
- Select building type to construct
- Add to construction queue

**Algorithm (from codex):**
```kotlin
fun tick(village: VillageData, world: ServerWorld) {
    village.incrementExpansionTimer()
    
    if (village.expansionTimer < expansionInterval) return
    village.resetExpansionTimer()
    
    if (!canExpand(village, world)) return
    
    val buildingType = selectBuildingType(village)
    val buildSite = findBuildLocation(village, world, buildingType)
    
    if (buildSite != null) {
        queueConstruction(village, buildingType, buildSite)
    }
}
```

**Implementation Priority:** ⭐⭐⭐⭐

**Complexity:** Medium
**Risk:** Medium
**Dependencies:** VillageData, BuildingPlacer, BuildingRegistry

---

**8. Resource & Economy** (`village/`)
```kotlin
VillageEconomyManager.kt    // Resource tracking
VillagePersonalitySystem.kt // Village types (agrarian, trader, etc.)
```

**Purpose:**
- Track village resources (food, wood, stone, etc.)
- Consume resources for construction (if enabled)
- Implement village personalities (affects building priorities)

**Implementation Priority:** ⭐⭐⭐

**Complexity:** Low-Medium
**Risk:** Low
**Dependencies:** VillageData

---

### Phase 3 Completion Criteria:
✅ Villages expand automatically over time  
✅ Tier system progresses correctly  
✅ Building selection makes sense (housing first, then professions)  
✅ Resource system works (if enabled)  
✅ **TEST:** AFK near village for 1 hour - should see 3-5 new buildings  
✅ **TEST:** Buildings placed in valid locations, no overlaps

**Estimated Time:** 4-6 days

---

## 🎬 Phase 4: Construction Animation (Polish, Not Critical)

### Goal: Make building placement look cool

### Classes to Implement:

**9. Construction Animation** (`building/`)
```kotlin
ConstructionAnimation.kt    // Gradual placement
BuildingManager.kt          // Construction orchestration
```

**Purpose:**
- Place blocks gradually (not all at once)
- Spawn particles during construction
- Play sound effects
- Track construction progress

**Implementation Priority:** ⭐⭐ (Nice to have, not critical)

**Complexity:** Medium
**Risk:** Low
**Dependencies:** BuildingPlacer

---

**10. Builder AI** (`ai/`)
```kotlin
BuilderAI.kt               // Builder villager behavior
```

**Purpose:**
- Villagers walk to build site
- Play animations (hammering, placing blocks)
- Fetch materials from storage
- Rest periodically

**Implementation Priority:** ⭐ (Polish, can skip initially)

**Complexity:** High
**Risk:** Low
**Dependencies:** BuildingManager, Villager AI system

---

### Phase 4 Completion Criteria:
✅ Buildings appear gradually, not instantly  
✅ Particles and sounds play  
✅ Builder villagers pathfind to site  
✅ **TEST:** Construction looks immersive and satisfying

**Estimated Time:** 3-5 days

---

## 👤 Phase 5: Villager Identity System

### Goal: Named villagers with persistence

### Classes to Implement:

**11. Villager Identity** (`villager/`)
```kotlin
VillagerIdentitySystem.kt   // Name, lineage, persistence
NameGenerator.kt            // Biome-aware naming
```

**Purpose:**
- Assign unique names to villagers
- Track family relationships
- Persist identity across sessions
- Disable name tags

**Implementation Priority:** ⭐⭐⭐⭐

**Complexity:** Medium
**Risk:** Low
**Dependencies:** None (standalone)

---

**12. Custom Professions** (`profession/`)
```kotlin
CustomProfession.kt        // Base class
ProfessionRegistry.kt      // Catalog
JobBlockRegistry.kt        // Custom job sites
```

**Purpose:**
- Add new professions (Miner, Hunter, Engineer, etc.)
- Create custom job site blocks
- Integrate with vanilla profession system

**Implementation Priority:** ⭐⭐⭐

**Complexity:** High (Minecraft profession system is complex)
**Risk:** Medium
**Dependencies:** Fabric API profession hooks

---

### Phase 5 Completion Criteria:
✅ Villagers have names from appropriate biome pool  
✅ Names persist across reloads  
✅ Children inherit last names  
✅ Custom professions spawn correctly  
✅ **TEST:** Breed villagers, child has correct last name

**Estimated Time:** 4-6 days

---

## 🛒 Phase 6: Shop System

### Goal: Functional shops with purchase UI

### Classes to Implement:

**13. Shop Generation** (`shop/`)
```kotlin
ShopGenerator.kt           // Worldgen integration
ShopData.kt               // Shop state
ShopCounterBlock.kt       // Shop block
ShopCounterBlockEntity.kt // Shop data storage
```

**Purpose:**
- Generate shops during village worldgen
- Store shop data (type, inventory, prices)
- Handle block interactions

**Implementation Priority:** ⭐⭐⭐⭐

**Complexity:** Medium-High
**Risk:** Medium
**Dependencies:** Building system

---

**14. Shop UI** (`shop/`)
```kotlin
ShopScreen.kt             // Purchase GUI
RestockSystem.kt          // Inventory refresh
CurrencyManager.kt        // Payment processing
```

**Purpose:**
- Display shop inventory
- Handle purchases
- Restock periodically

**Implementation Priority:** ⭐⭐⭐

**Complexity:** Medium
**Risk:** Low
**Dependencies:** ShopData, Fabric Screen API

---

### Phase 6 Completion Criteria:
✅ Shops generate in villages  
✅ Can open shop GUI  
✅ Can purchase items with emeralds  
✅ Shops restock over time  
✅ **TEST:** Buy all items, wait, shop restocks

**Estimated Time:** 5-7 days

---

## 🛡️ Phase 7: Defense & Polish

### Goal: Additional features and polish

### Classes to Implement:

**15. Golem Repair** (`defense/`)
```kotlin
GolemRepairSystem.kt      // Iron golem healing
```

**16. Reputation System** (`social/`)
```kotlin
ReputationSystem.kt       // Player-village relations
```

**17. UI Systems** (`ui/`)
```kotlin
VillageNameDisplay.kt     // Entry notifications
EventNotification.kt      // Alerts
```

**Implementation Priority:** ⭐⭐

**Complexity:** Low-Medium
**Risk:** Low
**Dependencies:** Various

---

## 🎯 Recommended Implementation Order (Day-by-Day)

### Week 1: Foundation
**Days 1-2:** Config system + basic project structure  
**Days 3-4:** VillageData + NBT serialization  
**Days 5-7:** VillageRegistry + VillageStorage + testing

### Week 2: Building Basics
**Days 8-9:** BuildingTemplate + JSON loader  
**Days 10-11:** TerrainScanner  
**Days 12-14:** BuildingPlacer + extensive testing

### Week 3: Village Detection
**Days 15-16:** Village detection system  
**Days 17-18:** Village initialization  
**Days 19-21:** Integration testing + bug fixes

### Week 4: Expansion
**Days 22-24:** VillageExpansionEngine  
**Days 25-26:** VillageTierSystem  
**Days 27-28:** Resource management + testing

### Week 5-6: Construction Animation (Optional)
Can skip if want to move faster

### Week 7-8: Villager Identity
**Days 43-45:** VillagerIdentitySystem + NameGenerator  
**Days 46-49:** Custom professions  
**Days 50-52:** Testing + polish

### Week 9-10: Shop System
**Days 53-56:** Shop generation + data structures  
**Days 57-60:** Shop UI + currency system  
**Days 61-63:** Restock system + testing

### Week 11+: Polish
Remaining features, bug fixes, optimization

---

## ⚠️ Critical Success Factors

### 1. **Test Each Layer Before Moving On**
- Don't add new features until current ones work
- V1 failed because too much was built on broken foundation

### 2. **Start Simple, Add Complexity Later**
- First building: 3x3 dirt hut
- First expansion: One house every 5 minutes
- First villager: Just a name, no relationships

### 3. **Use Your Codex**
- Update it as you implement
- Document what actually works vs what's planned
- Keep implementation notes

### 4. **Focus on Building Placement**
- This was V1's Achilles heel
- Spend extra time testing rotation, terrain, collisions
- Test with dozens of buildings before moving on

### 5. **Don't Do Everything At Once**
- You have 8 systems planned
- Implement 1-2 systems fully before starting another
- Resist feature creep

---

## 🚫 What NOT To Do (Lessons from V1)

❌ Don't implement all systems simultaneously  
❌ Don't skip testing phases  
❌ Don't assume building placement "just works"  
❌ Don't add animation before basic placement works  
❌ Don't overcomplicate early features  
❌ Don't let the codex become outdated

---

## ✅ Definition of "Done" for Each Phase

**Phase 1 (Foundation):**
- Config loads correctly
- Villages save and load without corruption
- Can create/retrieve villages programmatically
- **Manual Test:** Create village, save, restart game, village exists with correct data

**Phase 2 (Building Placement):**
- Can place any building from template
- All 4 rotations work correctly
- Terrain adaptation works
- No overlapping buildings
- **Manual Test:** Place 20 buildings of various types, all look correct

**Phase 3 (Expansion):**
- Villages expand automatically
- Building selection is logical
- Tiers progress naturally
- **Manual Test:** AFK for 2 hours, village grows from 5 to 15 buildings

**Phase 4+ (Polish):**
- Features work as designed
- No crashes
- Performance is acceptable
- **Manual Test:** All systems working together smoothly

---

## 📈 Progress Tracking

```
Foundation:         [ 20% ]  ██░░░░░░░░
Building System:    [  0% ]  ░░░░░░░░░░
Expansion:          [  0% ]  ░░░░░░░░░░
Construction Anim:  [  0% ]  ░░░░░░░░░░
Villager Identity:  [  0% ]  ░░░░░░░░░░
Shop System:        [  0% ]  ░░░░░░░░░░
Defense:            [  0% ]  ░░░░░░░░░░
Polish:             [  0% ]  ░░░░░░░░░░

OVERALL:            [  8% ]  █░░░░░░░░░
```

---

## 🎯 Your Next Step

**START WITH:** Configuration System + VillageData

These are:
- Self-contained (no dependencies)
- Low risk
- Essential foundation
- Easy to test
- Build confidence

**Estimated time:** 2-3 days  
**Files to create:**
- `src/main/kotlin/com/mrwizard94/livingvillages/config/LVConfig.kt`
- `src/main/kotlin/com/mrwizard94/livingvillages/config/ModConfig.kt`
- `src/main/kotlin/com/mrwizard94/livingvillages/village/VillageData.kt`
- `config/living_villages.json` (default config)

---

**Ready to start Phase 1?** 🚀
