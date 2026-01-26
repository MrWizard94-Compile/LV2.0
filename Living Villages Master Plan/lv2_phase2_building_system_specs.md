# Living Villages 2.0 - Phase 2 Building System Specifications

**🎯 CRITICAL ZONE - THIS IS WHERE V1 FAILED**

---

## 📋 Phase 2 Overview

**Goal:** Implement building placement system that **WORKS PERFECTLY** before moving forward.

**Components:**
1. Building Template System (JSON format)
2. Building Placement Engine (THE CRITICAL PART)
3. Terrain Scanner (Build site validation)
4. Village Detection (Find vanilla villages)

**Success Criteria:**
- ✅ Can place ANY building from template
- ✅ ALL 4 rotations work correctly (0°, 90°, 180°, 270°)
- ✅ Terrain adaptation works
- ✅ NO overlapping buildings
- ✅ Buildings look correct every time

---

## 🏗️ Building Template System

### JSON Template Format

**Location:** `src/main/resources/data/living_villages/templates/`

**Directory Structure:**
```
templates/
├── houses/
│   ├── small_house_1.json
│   ├── small_house_2.json
│   └── large_house_1.json
├── farms/
│   ├── wheat_farm.json
│   └── animal_pen.json
├── shops/
│   ├── food_shop.json
│   └── tool_shop.json
├── decorative/
│   ├── fountain.json
│   └── lamp_post.json
└── defensive/
    ├── guard_tower.json
    └── wall_segment.json
```

### Complete Template Schema

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

### Field Definitions

| Field | Type | Description |
|-------|------|-------------|
| `template_id` | String | Unique identifier (namespace:id format) |
| `name` | String | Display name |
| `category` | String | house, farm, shop, decorative, defensive |
| `biomes` | Array | Compatible biome IDs |
| `tier_requirement` | Int | Minimum village tier (0-5) |
| `size` | Object | {width, height, depth} in blocks |
| `beds` | Int | Number of beds in structure |
| `job_sites` | Int | Number of job site blocks |
| `construction_time_ticks` | Int | Time to build (if animated) |
| `required_materials` | Object | {block_id: count} map |
| `blocks` | Array | Block placement data |
| `entities` | Array | Entity placement data (optional) |
| `variants` | Array | Alternative template IDs |
| `rotation_supported` | Boolean | Can rotate 90°, 180°, 270° |
| `terrain_adaptation` | String | "flatten", "pillar", "none" |
| `min_ground_clearance` | Int | Minimum flat ground needed |
| `spawn_weight` | Int | Selection probability weight |

### Block Entry Format

```json
{
  "pos": [x, y, z],           // Relative to origin (0,0,0)
  "block": "namespace:id",    // Block identifier
  "properties": {             // Block state properties
    "facing": "north",
    "half": "lower",
    "waterlogged": "false"
  }
}
```

**Important Notes:**
- Position [0, 0, 0] is the **northwest bottom corner**
- Y=0 is ground level
- Coordinates are relative, not absolute
- Properties must match Minecraft's block state names EXACTLY

---

## 🔨 Building Placement Engine

### Core Algorithm (From Codex)

```kotlin
fun placeBuilding(
    template: BuildingTemplate,
    targetPos: BlockPos,
    rotation: Int,  // 0, 90, 180, or 270
    world: ServerWorld,
    instant: Boolean = true
): Boolean {
    
    // STEP 1: Validate placement location
    if (!isValidLocation(template, targetPos, world)) {
        return false
    }
    
    // STEP 2: Check for collisions
    if (hasCollision(template, targetPos, rotation, world)) {
        return false
    }
    
    // STEP 3: Terrain preparation
    if (template.terrainAdaptation == "flatten") {
        flattenTerrain(template, targetPos, world)
    } else if (template.terrainAdaptation == "pillar") {
        buildPillars(template, targetPos, world)
    }
    
    // STEP 4: Place blocks
    if (instant) {
        placeAllBlocksInstant(template, targetPos, rotation, world)
    } else {
        queueGradualPlacement(template, targetPos, rotation, world)
    }
    
    return true
}
```

### Rotation Mathematics

**The Critical Part - This is where V1 likely broke!**

```kotlin
fun rotateBlockPos(
    localPos: BlockPos,     // Position in template
    rotation: Int,          // 0, 90, 180, 270
    templateSize: Vec3i     // Template dimensions
): BlockPos {
    
    val x = localPos.x
    val y = localPos.y
    val z = localPos.z
    
    return when (rotation) {
        0 -> BlockPos(x, y, z)                                    // No rotation
        90 -> BlockPos(templateSize.z - 1 - z, y, x)             // 90° clockwise
        180 -> BlockPos(templateSize.x - 1 - x, y, templateSize.z - 1 - z)  // 180°
        270 -> BlockPos(z, y, templateSize.x - 1 - x)            // 270° clockwise
        else -> BlockPos(x, y, z)
    }
}
```

**Rotation Formula Explanation:**

| Rotation | Formula | Effect |
|----------|---------|--------|
| 0° | (x, y, z) | Original position |
| 90° | (width-1-z, y, x) | Swap X/Z, mirror Z |
| 180° | (width-1-x, y, depth-1-z) | Mirror both X and Z |
| 270° | (z, y, depth-1-x) | Swap X/Z, mirror X |

**Block State Rotation:**

Certain blocks need their properties rotated (doors, stairs, beds, etc.)

```kotlin
fun rotateBlockState(
    state: BlockState,
    rotation: Int
): BlockState {
    
    // Handle facing property
    if (state.contains(Properties.HORIZONTAL_FACING)) {
        val currentFacing = state.get(Properties.HORIZONTAL_FACING)
        val newFacing = rotateFacing(currentFacing, rotation)
        state = state.with(Properties.HORIZONTAL_FACING, newFacing)
    }
    
    // Handle other directional properties
    // axis, half, hinge, shape, etc.
    
    return state
}

fun rotateFacing(facing: Direction, rotation: Int): Direction {
    return when (rotation) {
        0 -> facing
        90 -> facing.rotateYClockwise()
        180 -> facing.opposite
        270 -> facing.rotateYCounterclockwise()
        else -> facing
    }
}
```

### Collision Detection

```kotlin
fun hasCollision(
    template: BuildingTemplate,
    targetPos: BlockPos,
    rotation: Int,
    world: ServerWorld
): Boolean {
    
    // Get all existing buildings in village
    val village = VillageRegistry.findNearestVillage(targetPos, world) ?: return false
    val existingBuildings = village.buildings
    
    // Get bounding box for new building
    val newBounds = getBoundingBox(template, targetPos, rotation)
    
    // Check against each existing building
    for (existing in existingBuildings) {
        val existingTemplate = BuildingRegistry.getTemplate(existing.templateId)
        val existingBounds = getBoundingBox(existingTemplate, existing.position, existing.rotation)
        
        // Add spacing buffer (minimum 2 blocks between buildings)
        val expandedBounds = existingBounds.expand(2.0, 0.0, 2.0)
        
        if (newBounds.intersects(expandedBounds)) {
            return true  // Collision detected!
        }
    }
    
    // Check for water/lava
    if (containsLiquid(newBounds, world)) {
        return true
    }
    
    return false
}
```

---

## 🌍 Terrain Scanner

### Build Site Validation

```kotlin
/**
 * TerrainScanner - Finds valid building locations
 */
class TerrainScanner {
    
    /**
     * Find suitable location for building near a position
     */
    fun findBuildLocation(
        village: VillageData,
        template: BuildingTemplate,
        world: ServerWorld,
        searchRadius: Int = 32
    ): BlockPos? {
        
        val centerPos = village.centerPos
        val villageRadius = village.radius
        
        // Try multiple random positions
        repeat(50) {
            val offsetX = world.random.nextInt(villageRadius * 2) - villageRadius
            val offsetZ = world.random.nextInt(villageRadius * 2) - villageRadius
            
            val testPos = centerPos.add(offsetX, 0, offsetZ)
            
            // Get ground level at this position
            val groundPos = getGroundLevel(testPos, world)
            
            if (isValidBuildSite(template, groundPos, world)) {
                return groundPos
            }
        }
        
        return null  // No valid location found
    }
    
    /**
     * Check if location is suitable for building
     */
    fun isValidBuildSite(
        template: BuildingTemplate,
        pos: BlockPos,
        world: ServerWorld
    ): Boolean {
        
        // Check 1: Must be on solid ground
        if (!isOnSolidGround(pos, world)) {
            return false
        }
        
        // Check 2: Check flatness
        val flatnessScore = calculateFlatness(template, pos, world)
        if (flatnessScore < 0.7) {
            return false
        }
        
        // Check 3: Check for obstructions
        if (hasObstructions(template, pos, world)) {
            return false
        }
        
        // Check 4: Not in water/lava
        if (isInLiquid(template, pos, world)) {
            return false
        }
        
        return true
    }
    
    /**
     * Calculate how flat an area is (0.0 to 1.0)
     */
    fun calculateFlatness(
        template: BuildingTemplate,
        pos: BlockPos,
        world: ServerWorld
    ): Double {
        
        val size = template.size
        val heightDifferences = mutableListOf<Int>()
        
        // Sample height at multiple points
        for (x in 0 until size.width) {
            for (z in 0 until size.depth) {
                val samplePos = pos.add(x, 0, z)
                val groundLevel = getGroundLevel(samplePos, world)
                heightDifferences.add(abs(groundLevel.y - pos.y))
            }
        }
        
        // Calculate average height difference
        val avgDiff = heightDifferences.average()
        
        // Convert to score (0-2 blocks = perfect, 5+ blocks = terrible)
        return max(0.0, 1.0 - (avgDiff / 5.0))
    }
    
    /**
     * Get actual ground level at position
     */
    fun getGroundLevel(pos: BlockPos, world: ServerWorld): BlockPos {
        var checkPos = pos.withY(world.topY)
        
        // Scan downward until solid block
        while (checkPos.y > world.bottomY) {
            val state = world.getBlockState(checkPos)
            
            if (state.isSolidBlock(world, checkPos)) {
                return checkPos.up()  // Return position above solid block
            }
            
            checkPos = checkPos.down()
        }
        
        return pos  // Fallback
    }
    
    /**
     * Check if building footprint has obstructions
     */
    fun hasObstructions(
        template: BuildingTemplate,
        pos: BlockPos,
        world: ServerWorld
    ): Boolean {
        
        val size = template.size
        
        for (x in 0 until size.width) {
            for (y in 0 until size.height) {
                for (z in 0 until size.depth) {
                    val checkPos = pos.add(x, y, z)
                    val state = world.getBlockState(checkPos)
                    
                    // Trees, structures, existing buildings
                    if (isObstruction(state)) {
                        return true
                    }
                }
            }
        }
        
        return false
    }
    
    fun isObstruction(state: BlockState): Boolean {
        // Logs, leaves, other village buildings, structures
        return state.isIn(BlockTags.LOGS) ||
               state.isIn(BlockTags.LEAVES) ||
               state.block is ChestBlock ||
               state.block is BedBlock
    }
}
```

### Terrain Adaptation

```kotlin
/**
 * Flatten terrain for building placement
 */
fun flattenTerrain(
    template: BuildingTemplate,
    pos: BlockPos,
    world: ServerWorld
) {
    val size = template.size
    val baseY = pos.y
    
    for (x in -1 until size.width + 1) {  // +1 block border
        for (z in -1 until size.depth + 1) {
            val flattenPos = pos.add(x, 0, z)
            
            // Fill below to base level
            var fillPos = flattenPos.withY(baseY - 1)
            while (fillPos.y > world.bottomY && world.getBlockState(fillPos).isAir) {
                world.setBlockState(fillPos, Blocks.DIRT.defaultState)
                fillPos = fillPos.down()
            }
            
            // Clear above to base level
            for (y in 0 until size.height + 3) {
                val clearPos = flattenPos.withY(baseY + y)
                val state = world.getBlockState(clearPos)
                
                // Remove grass, flowers, saplings
                if (state.isIn(BlockTags.REPLACEABLE) || 
                    state.block is PlantBlock ||
                    state.block is SaplingBlock) {
                    world.setBlockState(clearPos, Blocks.AIR.defaultState)
                }
            }
        }
    }
    
    // Place grass on top layer
    for (x in -1 until size.width + 1) {
        for (z in -1 until size.depth + 1) {
            val grassPos = pos.add(x, -1, z)
            if (world.getBlockState(grassPos).isOf(Blocks.DIRT)) {
                world.setBlockState(grassPos, Blocks.GRASS_BLOCK.defaultState)
            }
        }
    }
}
```

---

## 🧪 Testing Strategy (CRITICAL!)

### Phase 2 Testing Checklist

**Before moving to Phase 3, you MUST verify:**

#### Level 1: Basic Placement
- [ ] Place a 3x3x3 dirt cube (simplest possible structure)
- [ ] Verify exact block positions
- [ ] No crashes

#### Level 2: Rotation
- [ ] Place simple house at 0° rotation - looks correct
- [ ] Place simple house at 90° rotation - looks correct
- [ ] Place simple house at 180° rotation - looks correct  
- [ ] Place simple house at 270° rotation - looks correct
- [ ] Door facing matches rotation
- [ ] Bed orientation matches rotation
- [ ] Stairs/slabs oriented correctly

#### Level 3: Terrain
- [ ] Place on perfectly flat ground - works
- [ ] Place on slight slope - terrain flattens correctly
- [ ] Place on hill - terrain adapts properly
- [ ] Place near water - doesn't overlap water
- [ ] Place in forest - clears trees properly

#### Level 4: Collision Detection
- [ ] Place two buildings next to each other - proper spacing
- [ ] Attempt to place overlapping - correctly rejected
- [ ] Place 10 buildings in village - no overlaps
- [ ] Buildings maintain minimum 2-block spacing

#### Level 5: Complex Structures
- [ ] Place house with door - door works
- [ ] Place house with bed - bed functional
- [ ] Place house with chest - chest accessible
- [ ] Place multi-story building - all floors correct

### Test Building Templates

**Create these test templates first:**

1. **test_cube_3x3.json** - Solid dirt cube
2. **test_simple_house.json** - 5x5 oak planks box with door
3. **test_rotation.json** - Asymmetric structure (stairs in one corner)
4. **test_complex.json** - Multi-material, multi-story structure

---

## 🚨 Common Pitfalls (Lessons from V1)

### DON'T Do These:

1. ❌ **Assume rotation works** - Test EVERY angle with EVERY structure
2. ❌ **Skip terrain flattening** - You'll get floating buildings
3. ❌ **Ignore block states** - Doors/stairs/beds need property rotation
4. ❌ **Place without collision check** - Buildings will overlap
5. ❌ **Use complex structures first** - Start with 3x3 cubes!
6. ❌ **Add animation before basic placement works** - Walk before you run
7. ❌ **Move to Phase 3 without testing** - This is THE critical phase

### DO These:

1. ✅ **Start with instant placement** - Get basic working first
2. ✅ **Test rotation exhaustively** - 4 rotations × 10 structures = 40 tests
3. ✅ **Use small test structures** - 3x3, 5x5, 7x7 before larger
4. ✅ **Verify visually IN-GAME** - Screenshots of each placement
5. ✅ **Check edge cases** - Water, lava, steep hills, forests
6. ✅ **Add debug visualization** - Show bounding boxes, collision zones
7. ✅ **Log everything** - Every placement attempt, success/failure

---

## 📊 Implementation Order

### Week 1: Template System (Days 1-2)

**Classes to Create:**
- `BuildingTemplate.kt` - Data class
- `BuildingTemplateLoader.kt` - JSON parser
- `BuildingRegistry.kt` - Template catalog

**Goal:** Load templates from JSON, validate format

### Week 1: Basic Placement (Days 3-4)

**Classes to Create:**
- `BuildingPlacer.kt` - Core placement logic
- Start with INSTANT placement, NO rotation

**Goal:** Place a 3x3x3 cube at exact position

### Week 1: Rotation Support (Days 5-7)

**Update:**
- `BuildingPlacer.kt` - Add rotation math
- Add block state rotation

**Goal:** All 4 rotations work perfectly

### Week 2: Terrain Scanner (Days 8-9)

**Classes to Create:**
- `TerrainScanner.kt` - Site validation

**Goal:** Find valid flat locations automatically

### Week 2: Terrain Adaptation (Days 10-11)

**Update:**
- `BuildingPlacer.kt` - Add terrain flattening

**Goal:** Buildings adapt to slopes

### Week 2: Collision Detection (Days 12-14)

**Update:**
- `BuildingPlacer.kt` - Add collision checks

**Goal:** No overlapping buildings, proper spacing

### Week 2: Integration & Testing

- Create 5-10 test templates
- Place 50+ buildings
- Test all rotations
- Test all terrain types
- Fix ALL bugs before Phase 3

---

## 🎯 Phase 2 Success Definition

**Phase 2 is COMPLETE when:**

1. ✅ Can load building templates from JSON
2. ✅ Can place ANY template at ANY position
3. ✅ Rotation works perfectly for all 4 angles
4. ✅ Terrain flattens/adapts correctly
5. ✅ Collision detection prevents overlaps
6. ✅ Placed 100+ test buildings with ZERO issues
7. ✅ All test cases pass
8. ✅ Code is clean, documented, tested

**DO NOT** move to Phase 3 (Village Expansion) until ALL above criteria are met.

---

## 💡 Next Steps After Phase 2

Once building placement is bulletproof:

**Phase 3: Village Expansion**
- Auto-growth over time
- Building selection AI
- Resource management

**Phase 4: Construction Animation**
- Gradual block placement
- Particles and sounds
- Builder villagers

**But NOT before Phase 2 is perfect!**

---

**Remember:** V1 failed because building placement was broken. Don't repeat that mistake. Get this right, test thoroughly, then move forward.
