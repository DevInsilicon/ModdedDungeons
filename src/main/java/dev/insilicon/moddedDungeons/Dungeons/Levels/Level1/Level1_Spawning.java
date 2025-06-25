package dev.insilicon.moddedDungeons.Dungeons.Levels.Level1;

import dev.insilicon.moddedDungeons.EntityManagement.EntityManager;
import dev.insilicon.moddedDungeons.EntityManagement.CustomEntities.BaseEntity;
import dev.insilicon.moddedDungeons.EntityManagement.CustomEntities.Basic.BasicWarrior;
import dev.insilicon.moddedDungeons.EntityManagement.CustomEntities.Basic.BasicArcher;
import dev.insilicon.moddedDungeons.ModdedDungeons;
import dev.insilicon.moddedDungeons.Interface.Admin.DebugLogCMD;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Level1_Spawning implements Listener {
    private final List<Location> coralLocations = new ArrayList<>();
    private int maxEntities = 50;
    private int minX, minY, minZ, maxX, maxY, maxZ;
    private World world;
    private String worldName = "dungeons"; // Default to dungeons world
    private final Random random = new Random();
    private static final String SYSTEM_REMOVE_META = "system_removed";

    public Level1_Spawning() {
        loadConfigAndRegion();
        scanForCoralBlocks();
        startSpawningTask();
    }

    private void loadConfigAndRegion() {
        // Load region bounds from config (same as Level1Manager)
        ConfigurationSection pos = ModdedDungeons.instance.getConfig().getConfigurationSection("levels.level1.position");
        if (pos != null) {
            minX = pos.getInt("x", -37);
            minY = pos.getInt("y", 56);
            minZ = pos.getInt("z", -113);
            maxX = pos.getInt("mx", 52);
            maxY = pos.getInt("my", 70);
            maxZ = pos.getInt("mz", -31);
        } else {
            minX = -37; minY = 56; minZ = -113; maxX = 52; maxY = 70; maxZ = -31;
        }
        
        // Fix: Use correct world name from config (should be "dungeons")
        worldName = ModdedDungeons.instance.getConfig().getString("levels.level1.world", "dungeons");
        world = Bukkit.getWorld(worldName);
        
        if (world == null) {
            DebugLogCMD.log("[Level1_Spawning] ERROR: World '" + worldName + "' not found!");
            // Try fallback to default world
            world = Bukkit.getWorlds().get(0);
            DebugLogCMD.log("[Level1_Spawning] Falling back to world: " + world.getName());
        } else {
            DebugLogCMD.log("[Level1_Spawning] Using world: " + world.getName());
        }
        
        maxEntities = ModdedDungeons.instance.getConfig().getInt("levels.level1.max_entities", 50);
        DebugLogCMD.log("[Level1_Spawning] Configuration loaded - Region: (" + minX + "," + minY + "," + minZ + ") to (" + maxX + "," + maxY + "," + maxZ + "), Max entities: " + maxEntities);
    }

    private void scanForCoralBlocks() {
        if (world == null) {
            DebugLogCMD.log("[CoralScan] World is null!");
            return;
        }
        DebugLogCMD.log("[CoralScan] Scanning in world: " + world.getName());
        coralLocations.clear();
        // Force-load chunk at (34,59,-89) for debugging
        int debugX = 34, debugY = 59, debugZ = -89;
        world.getChunkAt(debugX >> 4, debugZ >> 4).load();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location loc = new Location(world, x, y, z);
                    Material mat = loc.getBlock().getType();
                    // Debug: Log block at debug location and a few nearby
                    if ((x == debugX && y == debugY && z == debugZ) ||
                        (x == debugX && y == debugY && (z == debugZ+1 || z == debugZ-1)) ||
                        (x == debugX && (y == debugY+1 || y == debugY-1) && z == debugZ)) {
                        DebugLogCMD.log("[CoralScan] Block at ("+x+","+y+","+z+") is: " + mat);
                    }
                    if (mat == Material.DEAD_BRAIN_CORAL_BLOCK) {
                        coralLocations.add(loc.clone());
                    }
                }
            }
        }
        DebugLogCMD.log("[CoralScan] Found " + coralLocations.size() + " coral blocks in region.");
    }

    private void startSpawningTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (world == null) {
                    DebugLogCMD.log("[Spawning] World is null, cannot spawn entities");
                    return;
                }
                int count = countCustomEntitiesInRegion();
                DebugLogCMD.log("[Spawning] Current entity count: " + count + "/" + maxEntities);
                // Remove excess entities if over max
                if (count > maxEntities) {
                    int toRemove = count - maxEntities;
                    DebugLogCMD.log("[Spawning] Over max entities, removing " + toRemove + " custom entities");
                    int removed = 0;
                    for (Entity entity : world.getEntities()) {
                        if (!(entity instanceof LivingEntity)) continue;
                        LivingEntity le = (LivingEntity) entity;
                        if (isInRegion(le.getLocation()) && EntityManager.getEntityType(le) != null) {
                            // Mark for system removal
                            le.setMetadata(SYSTEM_REMOVE_META, new FixedMetadataValue(ModdedDungeons.instance, true));
                            le.setHealth(0.0);
                            removed++;
                            if (removed >= toRemove) break;
                        }
                    }
                }
                if (count >= maxEntities) {
                    DebugLogCMD.log("[Spawning] Max entities reached, skipping spawn");
                    return;
                }
                int toSpawn = Math.min(5, maxEntities - count); // Spawn max 5 at a time
                DebugLogCMD.log("[Spawning] Attempting to spawn " + toSpawn + " entities (coral locations: " + coralLocations.size() + ")");
                for (int i = 0; i < toSpawn; i++) {
                    Location spawnLoc = getRandomCoralSpawnLocation();
                    if (spawnLoc != null) {
                        BaseEntity entityType;
                        if (random.nextDouble() < 0.5) {
                            entityType = new BasicWarrior();
                        } else {
                            entityType = new BasicArcher();
                        }
                        try {
                            LivingEntity spawned = EntityManager.spawnEntity(entityType, spawnLoc);
                            if (spawned != null) {
                                DebugLogCMD.log("[Spawning] SUCCESS: Spawned " + entityType.getName() + " at " + spawnLoc);
                            } else {
                                DebugLogCMD.log("[Spawning] FAILED: spawnEntity returned null for " + entityType.getName());
                            }
                        } catch (Exception ex) {
                            DebugLogCMD.log("[Spawning] EXCEPTION: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    } else {
                        DebugLogCMD.log("[Spawning] No valid spawn location found (coral blocks: " + coralLocations.size() + ")");
                    }
                }
            }
        }.runTaskTimer(ModdedDungeons.instance, 20L, 100L); // More frequent for testing (5 seconds)
    }

    private int countCustomEntitiesInRegion() {
        int count = 0;
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            Location loc = entity.getLocation();
            if (isInRegion(loc)) {
                if (EntityManager.getEntityType((LivingEntity) entity) != null) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isInRegion(Location loc) {
        return loc.getWorld().equals(world)
                && loc.getBlockX() >= minX && loc.getBlockX() <= maxX
                && loc.getBlockY() >= minY && loc.getBlockY() <= maxY
                && loc.getBlockZ() >= minZ && loc.getBlockZ() <= maxZ;
    }

    private Location getRandomCoralSpawnLocation() {
        if (coralLocations.isEmpty()) return null;
        Location base = coralLocations.get(random.nextInt(coralLocations.size()));
        return base.clone().add(0, 2, 0);
    }

    // Prevent item drops if killed by system (not a player)
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) return;
        LivingEntity le = (LivingEntity) entity;
        if (EntityManager.getEntityType(le) != null) {
            boolean systemRemoved = le.hasMetadata(SYSTEM_REMOVE_META);
            boolean killedByPlayer = event.getEntity().getKiller() != null;
            if (systemRemoved || !killedByPlayer) {
                event.getDrops().clear();
                event.setDroppedExp(0);
                DebugLogCMD.log("[EntityDeath] Cleared drops for system-removed or non-player kill: " + entity.getType());
            }
        }
    }
}
