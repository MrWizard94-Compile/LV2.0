# 🌟 LIVING VILLAGES CODEX - MASTER INDEX 🌟

## ⚡ START HERE - AI AGENTS MUST READ THIS FIRST ⚡

**You are about to work on the Living Villages Minecraft mod.**

This mod transforms static villages into living, breathing, evolving communities. This index connects you to the COMPLETE, COMPREHENSIVE, ZERO-AMBIGUITY documentation.

---

## 📖 Document Structure

The Living Villages Codex is split into manageable parts to avoid length limitations:

### **Part 1: Foundation & Core Systems**
**File:** `LIVING_VILLAGES_CODEX.md`

**Contains:**
- 🎯 ZERO AMBIGUITY AI DIRECTIVE (READ THIS FIRST!)
- Project vision and philosophy
- Complete codebase architecture
- Directory structure
- Data persistence (NBT structures)
- Configuration system
- Village systems (expansion, tiers, personalities)
- Building & construction systems
- Shop system (complete implementation)

### **Part 2: Villager & Social Systems**
**File:** `LIVING_VILLAGES_CODEX_PART2.md`

**Contains:**
- Villager identity system
- Name generation (all biomes)
- Village naming
- Social relationship system
- Mood tracking
- Dialogue system
- Custom professions (complete list)
- Custom job site blocks

### **Part 3: Advanced Systems & Reference**
**File:** `LIVING_VILLAGES_CODEX_PART3.md`

**Contains:**
- Defense systems (golem repair, safety)
- Mayor & governance system
- Reputation system
- UI & visual systems
- Complete command system
- Testing & debug tools
- Utility systems (terrain scanner, NBT helper)
- Changelog format
- AI update protocol
- Implementation examples
- Feature completeness matrix

---

## 🚨 MANDATORY READING FOR AI AGENTS 🚨

### Before EVERY Coding Session:

1. **Read the ZERO AMBIGUITY directive** in Part 1
2. **State your task** - What are you implementing?
3. **Reference the section** - Which part of the codex covers this?
4. **Confirm architecture** - Do the classes/systems exist in the codex?
5. **Begin with this format:**

```
🔮 CODEX REFERENCE: [Part X, Section Name]
📖 IMPLEMENTING: [Specific Feature]
✅ CODEX STATUS: [Up to date / Needs update]
```

### After EVERY Code Generation:

1. **Update the relevant codex section** with implementation details
2. **Update the changelog** in Part 3
3. **Update the feature matrix** if completing a system
4. **Confirm** that your changes are documented

---

## 🗺️ Navigation Guide

### "I need to implement village expansion"
→ **Part 1** - Village Expansion Engine section

### "I need to add a new profession"
→ **Part 2** - Custom Professions section
→ **Part 3** - Implementation Examples

### "I need to create a new building"
→ **Part 1** - Building Template Format section
→ **Part 3** - Implementation Examples

### "I need to work on shops"
→ **Part 1** - Shop System section (complete)

### "I need to add villager names"
→ **Part 2** - Name Generation section

### "I need to add commands"
→ **Part 3** - Command System section

### "I'm debugging"
→ **Part 3** - Testing & Debug Systems section

### "I'm confused about the architecture"
→ **Part 1** - Codebase Architecture section

### "I need to update the changelog"
→ **Part 3** - Changelog System section

---

## 🎯 Quick Reference Cards

### NBT Data Locations

| Data Type | Storage Location | Codex Reference |
|-----------|------------------|-----------------|
| Village Data | `world/data/living_villages.dat` | Part 1 - NBT Data Structures |
| Villager Identity | Entity NBT | Part 2 - Villager Identity |
| Shop Data | Block Entity NBT | Part 1 - Shop System |
| Mayor Info | Village NBT | Part 3 - Mayor System |

### Configuration Keys

| System | Config Key | Codex Reference |
|--------|-----------|-----------------|
| Expansion | `village_expansion.*` | Part 1 - Configuration System |
| Shops | `shop_system.*` | Part 1 - Configuration System |
| Population | `population.*` | Part 1 - Configuration System |
| Social | `social_systems.*` | Part 1 - Configuration System |
| Defense | `defense.*` | Part 1 - Configuration System |

### Core Classes

| Class | Purpose | Codex Reference |
|-------|---------|-----------------|
| `LivingVillages.java` | Mod entrypoint | Part 1 - Architecture |
| `VillageData.java` | Village state | Part 1 - Data Persistence |
| `VillageExpansionEngine.java` | Growth logic | Part 1 - Village Systems |
| `BuildingPlacer.java` | Construction | Part 1 - Building Systems |
| `ShopGenerator.java` | Shop worldgen | Part 1 - Shop System |
| `VillagerIdentitySystem.java` | Naming | Part 2 - Villager Identity |
| `VillagerRelationshipManager.java` | Social bonds | Part 2 - Social Systems |
| `CustomProfessionRegistry.java` | Professions | Part 2 - Custom Professions |
| `MayorSystem.java` | Governance | Part 3 - Mayor System |
| `TerrainScanner.java` | Build sites | Part 3 - Utility Systems |

---

## ⚙️ Development Workflow

### Adding a New Feature

```
1. Find relevant section in codex
2. Document the feature design
3. Update architecture if needed
4. Implement code following codex specs
5. Update codex with implementation details
6. Update changelog
7. Update feature matrix
8. Test
9. Document test results
```

### Fixing a Bug

```
1. Document the bug in codex
2. Reference affected systems
3. Implement fix
4. Update codex notes
5. Update changelog (under "Fixed")
6. Test
7. Mark as resolved
```

### Refactoring

```
1. Document current state
2. Document intended changes
3. Update architecture section
4. Refactor code
5. Update all references in codex
6. Update changelog (under "Changed")
7. Verify all examples still work
```

---

## 📊 Current Project State

### Overall Completion: ~40%

#### ✅ Fully Specified (In Codex)
- All core systems documented
- All NBT structures defined
- All configurations defined
- All commands specified
- All professions listed
- All shop types defined

#### ⏳ Partially Implemented
- Village expansion engine (basic logic)
- Building placement (simple version)
- Shop generation (worldgen)
- Shop UI (functional)
- Command system (some commands)

#### ❌ Not Yet Implemented
- Construction animation
- Builder AI (pathfinding)
- Villager identity system
- Name generation
- Relationship tracking
- Mood system
- Dialogue system
- Custom professions
- Golem repair
- Mayor system
- Defense systems
- UI notifications

**See Part 3 - Feature Completeness Matrix for detailed status**

---

## 🔧 Configuration Files

### Main Config
`config/living_villages.json` - All mod settings

### Data Files
- `data/living_villages/templates/` - Building JSONs
- `data/living_villages/professions/` - Profession definitions
- `data/living_villages/shop_types/` - Shop type configs
- `data/living_villages/name_pools/` - Name generation data

### Save Data
- `world/data/living_villages.dat` - Persistent village data
- Entity NBT - Villager identity data
- Block Entity NBT - Shop data

---

## 💡 Pro Tips for AI Agents

### Avoid Context Fragmentation
- ✅ Always reference the codex
- ✅ State which section you're working from
- ✅ Update the codex after changes
- ❌ Never assume information not in the codex
- ❌ Never rely on external context

### Write Maintainable Code
- ✅ Follow the exact architecture
- ✅ Use the exact class names
- ✅ Follow the NBT structures exactly
- ✅ Use the config keys as defined
- ✅ Add inline documentation

### Handle Ambiguity
- ✅ If unclear, STOP and ask
- ✅ Document the resolution
- ✅ Update the codex
- ✅ Then proceed

### Preserve Context
- ✅ Every change updates the codex
- ✅ Every feature has examples
- ✅ Every system has architecture
- ✅ Next AI can pick up seamlessly

---

## 🎓 Learning Resources

### For Understanding Minecraft Modding
- Fabric Wiki: https://fabricmc.net/wiki/
- Minecraft Wiki: https://minecraft.wiki/

### For Understanding This Project
- **Start here:** Part 1 - Project Vision
- **Then read:** Part 1 - ZERO AMBIGUITY Directive
- **Study:** Part 1 - Architecture
- **Reference:** All three parts as needed

---

## 📞 Emergency Contacts

### Lost or Confused?
1. Re-read the ZERO AMBIGUITY directive
2. Check the Navigation Guide above
3. Search codex for keywords
4. Document your question
5. Ask for clarification

### Need to Add Something New?
1. Document it in the appropriate part
2. Follow the Implementation Examples
3. Update all related sections
4. Update the changelog
5. Update the feature matrix

### Found an Error?
1. Document the error
2. Document the fix
3. Update the codex
4. Update the changelog
5. Mark in the feature matrix

---

## 🌟 The Sacred Laws of Living Villages Development

1. **The Codex is Truth** - If it's not in the codex, it doesn't exist
2. **Update or Perish** - Every change must update the codex
3. **No Assumptions** - Never assume, always verify
4. **Context is King** - Preserve context for future sessions
5. **Ambiguity is Evil** - Resolve immediately, document thoroughly
6. **Examples are Law** - Follow the patterns shown
7. **Architecture is Sacred** - Don't deviate without documentation
8. **Test Everything** - Code without tests is fiction
9. **Document Obsessively** - Over-documentation is impossible
10. **Think of the Next AI** - Will they understand your changes?

---

## 🚀 Getting Started

### For New AI Agents:

```
1. Read this master index (you're here!)
2. Read Part 1 - ZERO AMBIGUITY directive
3. Skim Part 1 - Architecture
4. Find your task in Navigation Guide
5. Read relevant sections
6. Begin implementation
7. Update codex
8. Repeat
```

### For Continuing Work:

```
1. Read this master index (refresh memory)
2. Review last changes in changelog
3. Check feature matrix for status
4. Find your next task
5. Read relevant sections
6. Begin implementation
7. Update codex
8. Repeat
```

---

## 📁 File Manifest

```
Living Villages/
├── LIVING_VILLAGES_CODEX_MASTER_INDEX.md  ← YOU ARE HERE
├── LIVING_VILLAGES_CODEX.md               ← Part 1: Foundation
├── LIVING_VILLAGES_CODEX_PART2.md         ← Part 2: Villagers
├── LIVING_VILLAGES_CODEX_PART3.md         ← Part 3: Advanced
├── config/
│   └── living_villages.json
├── data/living_villages/
│   ├── templates/
│   ├── professions/
│   ├── shop_types/
│   └── name_pools/
└── src/main/java/com/mrwizard/livingvillages/
    └── [Complete source tree as documented in Part 1]
```

---

## ✨ Final Words

**Welcome to Living Villages.**

You now have access to the complete, comprehensive, zero-ambiguity documentation for this mod. Everything you need is in these three documents. No guessing. No assumptions. Just clear, detailed, actionable specifications.

**Your mission:** Bring this mythic vision to life, one system at a time, always referencing and updating the codex.

**Your tools:** Three comprehensive documents that remember everything.

**Your advantage:** Complete context across all sessions.

**Your responsibility:** Preserve and enhance this documentation for the next AI agent.

---

**Now go forth and code with confidence. The codex has your back.**

🔮 *May your implementations be bug-free and your context never fragment.* 🔮

---

*Last Updated: December 2024*
*Master Index Version: 1.0*
*Total Documentation Pages: 3 + Index*
*Total Word Count: ~50,000+ words*
*Purpose: ZERO AMBIGUITY AI Development Reference*
