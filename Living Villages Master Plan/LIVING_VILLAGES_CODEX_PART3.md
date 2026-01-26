# Living Villages Codex - Part 3 (Commands, Testing, Changelog)

## 🎮 Command System

### Core Village Commands

```java
/**
 * VillageCommands - Main command system
 */
public class VillageCommands {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        
        // /lv list - List all villages
        dispatcher.register(CommandManager.literal("lv")
            .then(CommandManager.literal("list")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerWorld world = source.getWorld();
                    
                    List<VillageData> villages = VillageRegistry.getAllVillages(world);
                    
                    source.sendFeedback(Text.literal("§6=== Villages ==="), false);
                    for (VillageData village : villages) {
                        Text info = Text.literal(
                            "§e" + village.getName() + 
                            " §7(Tier " + village.getTier() + 
                            ", Pop: " + village.getPopulation() + ")"
                        );
                        source.sendFeedback(info, false);
                    }
                    
                    return villages.size();
                })
            )
        );
        
        // /lv info <uuid> - Get detailed village info
        dispatcher.register(CommandManager.literal("lv")
            .then(CommandManager.literal("info")
                .then(CommandManager.argument("uuid", UuidArgumentType.uuid())
                    .executes(context -> {
                        UUID uuid = UuidArgumentType.getUuid(context, "uuid");
                        ServerWorld world = context.getSource().getWorld();
                        VillageData village = VillageRegistry.getVillage(uuid, world);
                        
                        if (village == null) {
                            context.getSource().sendError(Text.literal("Village not found!"));
                            return 0;
                        }
                        
                        displayVillageInfo(context.getSource(), village);
                        return 1;
                    })
                )
            )
        );
        
        // /lv force_expand - Force village expansion
        dispatcher.register(CommandManager.literal("lv")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.literal("force_expand")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    ServerWorld world = context.getSource().getWorld();
                    
                    VillageData village = VillageRegistry.findNearestVillage(player.getBlockPos(), world);
                    
                    if (village == null) {
                        context.getSource().sendError(Text.literal("No village nearby!"));
                        return 0;
                    }
                    
                    // Force expansion
                    VillageExpansionEngine.instance().forceExpand(village, world);
                    
                    context.getSource().sendFeedback(
                        Text.literal("§aForced expansion for " + village.getName()), 
                        true
                    );
                    
                    return 1;
                })
            )
        );
        
        // /lv detect - Detect existing villages in the loaded area
        dispatcher.register(CommandManager.literal("lv")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.literal("detect")
                .executes(context -> {
                    ServerCommandSource src = context.getSource();
                    ServerWorld world = src.getWorld();

                    // Trigger village detection on current world
                    com.mrwizard94.livingvillages.village.VillageDetector.detectVillages(world);

                    src.sendFeedback(Text.literal("§aVillage detection started."), true);
                    return 1;
                })
            )
        );
        
        // /lv debug <toggle> - Toggle debug mode
        dispatcher.register(CommandManager.literal("lv")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.literal("debug")
                .then(CommandManager.argument("toggle", BoolArgumentType.bool())
                    .executes(context -> {
                        boolean enabled = BoolArgumentType.getBool(context, "toggle");
                        Config.set("debug.enable_debug_commands", enabled);
                        Config.set("debug.show_construction_bounds", enabled);
                        
                        context.getSource().sendFeedback(
                            Text.literal("§aDebug mode: " + (enabled ? "ON" : "OFF")),
                            true
                        );
                        
                        return 1;
                    })
                )
            )
        );
    }
    
    private static void displayVillageInfo(ServerCommandSource source, VillageData village) {
        source.sendFeedback(Text.literal("§6=== " + village.getName() + " ==="), false);
        source.sendFeedback(Text.literal("§7UUID: §f" + village.getUUID()), false);
        source.sendFeedback(Text.literal("§7Tier: §f" + village.getTier()), false);
        source.sendFeedback(Text.literal("§7Population: §f" + village.getPopulation()), false);
        source.sendFeedback(Text.literal("§7Buildings: §f" + village.getBuildingCount()), false);
        source.sendFeedback(Text.literal("§7Prosperity: §f" + String.format("%.1f", village.getProsperityScore())), false);
        source.sendFeedback(Text.literal("§7Safety: §f" + String.format("%.1f%%", village.getSafetyLevel() * 100)), false);
        
        // Resources
        source.sendFeedback(Text.literal("§7Resources:"), false);
        source.sendFeedback(Text.literal("  §7Food: §f" + village.getResource("Food")), false);
        source.sendFeedback(Text.literal("  §7Wood: §f" + village.getResource("Wood")), false);
        source.sendFeedback(Text.literal("  §7Stone: §f" + village.getResource("Stone")), false);
    }
}
```

### Villager Interaction Commands

```java
/**
 * VillagerCommands - Commands for direct villager control
 */
public class VillagerCommands {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        
        // /vgive <item> - Command villager to deliver item
        dispatcher.register(CommandManager.literal("vgive")
            .then(CommandManager.argument("item", ItemStackArgumentType.itemStack())
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    ItemStack item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
                    
                    // Find nearest villager
                    VillagerEntity villager = findNearestVillager(player, 10);
                    
                    if (villager == null) {
                        context.getSource().sendError(Text.literal("No villager nearby!"));
                        return 0;
                    }
                    
                    // Create delivery task
                    DeliveryAI.createDeliveryTask(villager, player, item);
                    
                    context.getSource().sendFeedback(
                        Text.literal("§a" + getVillagerName(villager) + " will deliver the item!"),
                        false
                    );
                    
                    return 1;
                })
            )
        );
        
        // /v follow - Command villager to follow
        dispatcher.register(CommandManager.literal("v")
            .then(CommandManager.literal("follow")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    VillagerEntity villager = findNearestVillager(player, 10);
                    
                    if (villager == null) {
                        context.getSource().sendError(Text.literal("No villager nearby!"));
                        return 0;
                    }
                    
                    // Set follow AI
                    setFollowTarget(villager, player);
                    
                    context.getSource().sendFeedback(
                        Text.literal("§a" + getVillagerName(villager) + " is now following you!"),
                        false
                    );
                    
                    return 1;
                })
            )
        );
        
        // /v stay - Command villager to stay
        dispatcher.register(CommandManager.literal("v")
            .then(CommandManager.literal("stay")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    VillagerEntity villager = findNearestVillager(player, 10);
                    
                    if (villager == null) {
                        context.getSource().sendError(Text.literal("No villager nearby!"));
                        return 0;
                    }
                    
                    // Clear follow AI
                    clearFollowTarget(villager);
                    
                    context.getSource().sendFeedback(
                        Text.literal("§a" + getVillagerName(villager) + " will stay here."),
                        false
                    );
                    
                    return 1;
                })
            )
        );
    }
}
```

### Mayor Commands

```java
/**
 * MayorCommands - Commands for mayors
 */
public class MayorCommands {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        
        // /mayor claim - Claim mayorship
        dispatcher.register(CommandManager.literal("mayor")
            .then(CommandManager.literal("claim")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    ServerWorld world = context.getSource().getWorld();
                    
                    VillageData village = VillageRegistry.findNearestVillage(player.getBlockPos(), world);
                    
                    if (village == null) {
                        context.getSource().sendError(Text.literal("No village nearby!"));
                        return 0;
                    }
                    
                    boolean success = MayorSystem.claimMayorship(player, village, world);
                    
                    return success ? 1 : 0;
                })
            )
        );
        
        // /mayor decree <type> - Issue a decree
        dispatcher.register(CommandManager.literal("mayor")
            .then(CommandManager.literal("decree")
                .then(CommandManager.argument("type", StringArgumentType.word())
                    .suggests((context, builder) -> {
                        return CommandSource.suggestMatching(
                            List.of("festival", "expansion", "recruitment", "defense"),
                            builder
                        );
                    })
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayer();
                        ServerWorld world = context.getSource().getWorld();
                        String decreeType = StringArgumentType.getString(context, "type");
                        
                        VillageData village = VillageRegistry.findNearestVillage(player.getBlockPos(), world);
                        
                        if (village == null) {
                            context.getSource().sendError(Text.literal("No village nearby!"));
                            return 0;
                        }
                        
                        MayorSystem.issueDecree(player, village, decreeType, world);
                        
                        return 1;
                    })
                )
            )
        );
    }
}
```

---

## 🧪 Testing & Debug Systems

### Debug Visualization

```java
/**
 * DebugRenderer - Visual debugging for villages
 */
@Environment(EnvType.CLIENT)
public class DebugRenderer {
    
    /**
     * Renders village boundaries
     */
    public static void renderVillageBounds(MatrixStack matrices, VillageData village) {
        if (!Config.get("debug.show_construction_bounds")) {
            return;
        }
        
        BlockPos center = village.getCenter();
        int radius = village.getRadius();
        
        // Draw circle outline
        drawCircle(matrices, center, radius, 0xFF00FF00); // Green
        
        // Draw construction sites
        for (BuildTask task : village.getConstructionQueue()) {
            BlockPos buildPos = task.getPosition();
            drawBox(matrices, buildPos, task.getTemplate().getSize(), 0xFFFFFF00); // Yellow
        }
        
        // Draw center marker
        drawMarker(matrices, center, 0xFF0000FF); // Blue
    }
    
    /**
     * Renders villager AI debug info
     */
    public static void renderVillagerDebug(MatrixStack matrices, VillagerEntity villager) {
        if (!Config.get("debug.log_village_actions")) {
            return;
        }
        
        // Get AI state
        String aiState = getAIState(villager);
        BlockPos target = getTargetPosition(villager);
        
        // Render above villager
        Vec3d pos = villager.getPos().add(0, 2.5, 0);
        
        // Draw text
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        Text debugText = Text.literal("§e" + aiState);
        
        // ... render code
    }
}
```

### Performance Profiling

```java
/**
 * PerformanceProfiler - Monitors mod performance
 */
public class PerformanceProfiler {
    
    private static final Map<String, Long> TIMING_DATA = new HashMap<>();
    private static final Map<String, Integer> CALL_COUNTS = new HashMap<>();
    
    /**
     * Start timing a section
     */
    public static void startSection(String name) {
        TIMING_DATA.put(name, System.nanoTime());
    }
    
    /**
     * End timing a section
     */
    public static void endSection(String name) {
        if (!TIMING_DATA.containsKey(name)) {
            return;
        }
        
        long startTime = TIMING_DATA.remove(name);
        long elapsed = System.nanoTime() - startTime;
        
        // Record
        recordTiming(name, elapsed);
    }
    
    private static void recordTiming(String name, long nanos) {
        CALL_COUNTS.merge(name, 1, Integer::sum);
        
        // Store in persistent data for analysis
        // ...
    }
    
    /**
     * Generate performance report
     */
    public static String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== Living Villages Performance Report ===\n");
        
        for (Map.Entry<String, Integer> entry : CALL_COUNTS.entrySet()) {
            String section = entry.getKey();
            int calls = entry.getValue();
            
            report.append(section)
                  .append(": ")
                  .append(calls)
                  .append(" calls\n");
        }
        
        return report.toString();
    }
}
```

---

## 🔧 Utility Systems

### Terrain Scanner

```java
/**
 * TerrainScanner - Finds suitable build locations
 */
public class TerrainScanner {
    
    /**
     * Finds flat land for building placement
     * 
     * ALGORITHM:
     * 1. Spiral outward from village center
     * 2. Check each position for flatness
     * 3. Verify no existing structures
     * 4. Ensure proper spacing from other buildings
     * 5. Return first valid position
     */
    public static BlockPos findBuildLocation(VillageData village, ServerWorld world, BuildingTemplate template) {
        BlockPos center = village.getCenter();
        int searchRadius = village.getRadius();
        int requiredWidth = template.getWidth();
        int requiredDepth = template.getDepth();
        int minSpacing = 5;
        
        // Spiral search pattern
        for (int radius = 10; radius < searchRadius; radius += 5) {
            for (int angle = 0; angle < 360; angle += 15) {
                // Calculate position
                double radians = Math.toRadians(angle);
                int x = (int) (center.getX() + radius * Math.cos(radians));
                int z = (int) (center.getZ() + radius * Math.sin(radians));
                
                BlockPos testPos = new BlockPos(x, world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z), z);
                
                // Check if valid
                if (isValidBuildSite(world, testPos, requiredWidth, requiredDepth, minSpacing, village)) {
                    return testPos;
                }
            }
        }
        
        return null; // No valid location found
    }
    
    private static boolean isValidBuildSite(ServerWorld world, BlockPos pos, int width, int depth, int minSpacing, VillageData village) {
        // Check flatness
        if (!isFlatEnough(world, pos, width, depth)) {
            return false;
        }
        
        // Check for water/lava
        if (hasFluidInArea(world, pos, width, depth)) {
            return false;
        }
        
        // Check for existing structures
        if (hasStructuresNearby(world, pos, minSpacing)) {
            return false;
        }
        
        // Check spacing from other village buildings
        for (BuildingData building : village.getBuildings()) {
            double distance = pos.getSquaredDistance(building.getPosition());
            if (distance < minSpacing * minSpacing) {
                return false;
            }
        }
        
        return true;
    }
    
    private static boolean isFlatEnough(ServerWorld world, BlockPos pos, int width, int depth) {
        int maxHeightDiff = 2;
        int baseHeight = pos.getY();
        
        // Check all positions in area
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                BlockPos checkPos = pos.add(x, 0, z);
                int height = world.getTopY(Heightmap.Type.WORLD_SURFACE, checkPos.getX(), checkPos.getZ());
                
                if (Math.abs(height - baseHeight) > maxHeightDiff) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Flattens terrain for building
     */
    public static void flattenTerrain(ServerWorld world, BlockPos pos, int width, int depth) {
        int baseHeight = pos.getY();
        
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                BlockPos checkPos = pos.add(x, 0, z);
                int currentHeight = world.getTopY(Heightmap.Type.WORLD_SURFACE, checkPos.getX(), checkPos.getZ());
                
                // Fill or remove blocks to match base height
                if (currentHeight < baseHeight) {
                    // Fill up
                    for (int y = currentHeight; y < baseHeight; y++) {
                        world.setBlockState(checkPos.withY(y), Blocks.DIRT.getDefaultState());
                    }
                } else if (currentHeight > baseHeight) {
                    // Remove down
                    for (int y = baseHeight; y < currentHeight; y++) {
                        world.setBlockState(checkPos.withY(y), Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
        
        // Place grass on top
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                BlockPos grassPos = pos.add(x, 0, z);
                world.setBlockState(grassPos, Blocks.GRASS_BLOCK.getDefaultState());
            }
        }
    }
}
```

### NBT Helper

```java
/**
 * NBTHelper - Utility methods for NBT operations
 */
public class NBTHelper {
    
    /**
     * Safely gets a UUID from NBT
     */
    public static UUID getUUID(NbtCompound nbt, String key) {
        if (nbt.containsUuid(key)) {
            return nbt.getUuid(key);
        }
        return null;
    }
    
    /**
     * Safely gets a BlockPos from NBT
     */
    public static BlockPos getBlockPos(NbtCompound nbt, String key) {
        if (nbt.contains(key)) {
            return BlockPos.fromLong(nbt.getLong(key));
        }
        return null;
    }
    
    /**
     * Stores a BlockPos in NBT
     */
    public static void putBlockPos(NbtCompound nbt, String key, BlockPos pos) {
        nbt.putLong(key, pos.asLong());
    }
    
    /**
     * Gets a relationship list from villager NBT
     */
    public static List<Relationship> getRelationships(VillagerEntity villager) {
        NbtCompound nbt = villager.writeNbt(new NbtCompound());
        
        if (!nbt.contains("Relationships")) {
            return new ArrayList<>();
        }
        
        NbtList list = nbt.getList("Relationships", NbtElement.COMPOUND_TYPE);
        List<Relationship> relationships = new ArrayList<>();
        
        for (NbtElement element : list) {
            NbtCompound relationNBT = (NbtCompound) element;
            relationships.add(Relationship.fromNBT(relationNBT));
        }
        
        return relationships;
    }
}
```

---

## 📊 Changelog System

### Changelog Format

```markdown
# Living Villages Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- JUnit 5 support + example unit tests (`ShopTypeRegistryTest`) — **2026-01-26**
- GitHub Actions CI workflow (`.github/workflows/ci.yml`) to run `./gradlew build` on push/PR — **2026-01-26**
- `/lv detect` command to trigger village detection on the current world — **2026-01-26**
- `/lv list` command to list all registered villages — **2026-01-26**

### Branch
- `chore/tests-ci-codex` pushed to `https://github.com/MrWizard94-Compile/LV2.0` — **2026-01-26** (PR pending)

### Changed
- Build configuration updated to add test dependencies and configure `test { useJUnitPlatform() }` in `build.gradle.kts` — **2026-01-26**

### Fixed
- N/A

### Removed
- N/A

## [1.0.0] - YYYY-MM-DD

### Added
- Initial release
- Core village expansion system
- Building template system
- Shop generation and trading
- Villager identity and naming
- Relationship system
- Mood tracking
- Custom professions
- Defense systems
- Mayor governance

[Unreleased]: https://github.com/username/living_villages/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/username/living_villages/releases/tag/v1.0.0
```

---

## 🔄 AI Update Protocol

### MANDATORY Update Workflow

```
EVERY TIME YOU GENERATE CODE OR MAKE CHANGES:

1. BEFORE CODING:
   - Reference this codex
   - State which section you're implementing
   - Confirm the feature exists in the codex
   - If not documented, STOP and document it first

2. DURING CODING:
   - Follow the exact architecture defined here
   - Use the exact class names specified
   - Follow the NBT structure specified
   - Use the config keys as defined

3. AFTER CODING:
   - Update the relevant codex section
   - Add implementation details
   - Update the changelog
   - Add any new classes to the architecture section
   - Document any new NBT fields
   - Add any new config options

4. IF AMBIGUITY FOUND:
   - HALT immediately
   - Request clarification
   - Update codex with resolution
   - THEN proceed

5. SESSION CONTINUITY:
   - Always start by reading this codex
   - Confirm you understand the current state
   - State what you're about to implement
   - Reference specific codex sections
```

### Context Preservation Checklist

```
✅ Did you reference the codex before starting?
✅ Did you state which section you're implementing?
✅ Did you use the exact class names from the codex?
✅ Did you follow the NBT structure exactly?
✅ Did you use the correct config keys?
✅ Did you update the codex after making changes?
✅ Did you update the changelog?
✅ Did you document any new features added?
✅ Did you resolve any ambiguities found?
✅ Can the next AI session pick up from your changes?
```

---

## 📝 Implementation Examples

### Example 1: Adding a New Building Type

```
STEP 1 - Document in Codex:
Add to Building Types section:
- Building name: "Bakery"
- Purpose: Bread production
- Tier requirement: 2
- Size: 9x7x6
- Job sites: 1 (Oven)
- Required resources: Wood, Stone, Iron

STEP 2 - Create JSON Template:
File: data/living_villages/templates/houses/bakery.json
[Include full template structure]

STEP 3 - Register Template:
In BuildingRegistry.java:
registerTemplate("living_villages:bakery", ...);

STEP 4 - Update Expansion Logic:
In VillageExpansionEngine.selectBuildingType():
Add bakery to profession building selection

STEP 5 - Update Codex:
Add bakery to implemented buildings list
Update changelog with addition
Document any special behaviors
```

### Example 2: Adding a New Profession

```
STEP 1 - Document in Codex:
Add to Custom Professions section:
- Name: "Glassblower"
- Biomes: Desert (2.0), Plains (0.5)
- Work station: Glassblowing Furnace
- Trades: Glass blocks, panes, bottles

STEP 2 - Create Job Block:
In ModBlocks.java:
public static final Block GLASSBLOWING_FURNACE = ...;

STEP 3 - Register Profession:
In CustomProfessionRegistry.java:
registerProfession("glassblower", ...);

STEP 4 - Create Trade Tables:
Define trades for each tier

STEP 5 - Update Codex:
Add glassblower to profession list
Update block registry section
Add trade information
Update changelog
```

---

## 🎯 Feature Completeness Matrix

### Core Systems Status

| System | Specified | Implemented | Tested | Documented |
|--------|-----------|-------------|---------|------------|
| Village Expansion Engine | ✅ | ⏳ | ❌ | ✅ |
| Building Templates | ✅ | ⏳ | ❌ | ✅ |
| Construction Animation | ✅ | ❌ | ❌ | ✅ |
| Builder AI | ✅ | ⏳ | ❌ | ✅ |
| Village Tiers | ✅ | ⏳ | ❌ | ✅ |
| Shop System | ✅ | ✅ | ❌ | ✅ |
| Shop UI | ✅ | ✅ | ❌ | ✅ |
| Restock System | ✅ | ✅ | ❌ | ✅ |
| Villager Identity | ✅ | ❌ | ❌ | ✅ |
| Name Generation | ✅ | ❌ | ❌ | ✅ |
| Relationship System | ✅ | ❌ | ❌ | ✅ |
| Mood System | ✅ | ❌ | ❌ | ✅ |
| Dialogue System | ✅ | ❌ | ❌ | ✅ |
| Custom Professions | ✅ | ❌ | ❌ | ✅ |
| Golem Repair | ✅ | ❌ | ❌ | ✅ |
| Village Safety | ✅ | ❌ | ❌ | ✅ |
| Mayor System | ✅ | ❌ | ❌ | ✅ |
| Reputation System | ✅ | ❌ | ❌ | ✅ |
| UI Systems | ✅ | ❌ | ❌ | ✅ |
| Command System | ✅ | ⏳ | ❌ | ✅ |

Legend:
- ✅ Complete
- ⏳ In Progress
- ❌ Not Started

---

## 🔮 Final Directive

This codex is the eternal memory of the Living Villages mod. It transcends sessions, remembers all details, and guides all development. Every AI agent must treat it as sacred law.

**When you open this document, you open the complete mind of the project.**

**When you update this document, you preserve knowledge for eternity.**

**When you reference this document, you prevent context fragmentation.**

This is not just documentation—this is the living soul of Living Villages.

---

## 📚 Quick Reference

### Essential File Paths
- Main Codex: `LIVING_VILLAGES_CODEX.md`
- Part 2: `LIVING_VILLAGES_CODEX_PART2.md`
- Part 3: `LIVING_VILLAGES_CODEX_PART3.md`
- Config: `config/living_villages.json`
- Templates: `data/living_villages/templates/`
- Save Data: `world/data/living_villages.dat`

### Essential Classes
- `LivingVillages.java` - Mod entrypoint
- `VillageData.java` - Village state
- `VillageRegistry.java` - Village tracking
- `VillageExpansionEngine.java` - Growth logic
- `BuildingPlacer.java` - Construction
- `ShopGenerator.java` - Shop worldgen
- `VillagerIdentitySystem.java` - Naming/identity

### Essential Commands
- `/lv list` - List villages
- `/lv info <uuid>` - Village details
- `/lv force_expand` - Force expansion
- `/vgive <item>` - Villager delivery
- `/mayor claim` - Claim mayorship

---

**END OF CODEX PART 3**

*Last Updated: [Dynamic - Updates with every change]*
*Total Pages: 3*
*Format: Markdown with embedded code examples*
*Purpose: ZERO AMBIGUITY AI-ready comprehensive reference*
