package dev.insilicon.moddedDungeons.Dungeons.Levels.Level1;

import dev.insilicon.moddedDungeons.ModdedDungeons;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class Level1LootHandler implements Listener {

    private static Map<Location, Map<Block, Long>> lootChests = new HashMap<>();
    private static Map<Location, Map<Block, Long>> lootBarrels = new HashMap<>();
    private static Map<Location, Map<Block, Long>> trapChests = new HashMap<>();

    private static final long COOLDOWN_TIME = 300000;

    private int minX, minY, minZ, maxX, maxY, maxZ;

    public Level1LootHandler() {
        loadLevelBoundaries();
        findAllContainers();
    }

    private void loadLevelBoundaries() {
        ConfigurationSection positionSection = ModdedDungeons.instance.getConfig().getConfigurationSection("levels.level1.position");

        if (positionSection != null) {
            minX = positionSection.getInt("x");
            minY = positionSection.getInt("y");
            minZ = positionSection.getInt("z");
            maxX = positionSection.getInt("mx");
            maxY = positionSection.getInt("my");
            maxZ = positionSection.getInt("mz");
        } else {
            minX = -37;
            minY = 56;
            minZ = -113;
            maxX = 52;
            maxY = 70;
            maxZ = -31;
        }
    }

    private void findAllContainers() {
        lootChests.clear();
        lootBarrels.clear();
        trapChests.clear();

        World world = ModdedDungeons.instance.getServer().getWorlds().get(0);

        ModdedDungeons.instance.getLogger().info("Finding containers in Level 1...");
        ModdedDungeons.instance.getLogger().info("Scanning area from: " + minX + "," + minY + "," + minZ + " to " + maxX + "," + maxY + "," + maxZ);

        int chestCount = 0;
        int barrelCount = 0;
        int trapCount = 0;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location loc = new Location(world, x, y, z);
                    Block block = loc.getBlock();

                    if (block.getType() == Material.CHEST) {
                        Map<Block, Long> innerMap = new HashMap<>();
                        innerMap.put(block, 0L);
                        lootChests.put(loc, innerMap);
                        chestCount++;
                    }
                    else if (block.getType() == Material.TRAPPED_CHEST) {
                        Map<Block, Long> innerMap = new HashMap<>();
                        innerMap.put(block, 0L);
                        trapChests.put(loc, innerMap);
                        trapCount++;
                    }
                    else if (block.getType() == Material.BARREL) {
                        Map<Block, Long> innerMap = new HashMap<>();
                        innerMap.put(block, 0L);
                        lootBarrels.put(loc, innerMap);
                        barrelCount++;
                    }
                }
            }
        }

        ModdedDungeons.instance.getLogger().info("Found " + chestCount + " chests, " + barrelCount + " barrels, and " + trapCount + " trap chests in Level 1");
    }

    private boolean isOnCooldown(Location location, Map<Location, Map<Block, Long>> containerMap) {
        if (!containerMap.containsKey(location)) {
            return false;
        }

        Map<Block, Long> blockTimeMap = containerMap.get(location);
        if (blockTimeMap.isEmpty()) {
            return false;
        }

        Block block = blockTimeMap.keySet().iterator().next();
        long lastLootTime = blockTimeMap.get(block);

        return (System.currentTimeMillis() - lastLootTime) < COOLDOWN_TIME;
    }

    private void updateLootTime(Location location, Map<Location, Map<Block, Long>> containerMap) {
        if (!containerMap.containsKey(location)) {
            return;
        }

        Map<Block, Long> blockTimeMap = containerMap.get(location);
        if (blockTimeMap.isEmpty()) {
            return;
        }

        Block block = blockTimeMap.keySet().iterator().next();
        blockTimeMap.put(block, System.currentTimeMillis());
    }

    public void replenishLootChests() {
    }

    public void replenishTrapChests() {
    }

    public void replenishBarrels() {
    }

    private int getCooldownTimeLeft(Location location, Map<Location, Map<Block, Long>> containerMap) {
        if (!containerMap.containsKey(location)) {
            return 0;
        }

        Map<Block, Long> blockTimeMap = containerMap.get(location);
        if (blockTimeMap.isEmpty()) {
            return 0;
        }

        Block block = blockTimeMap.keySet().iterator().next();
        long lastLootTime = blockTimeMap.get(block);

        long timeElapsed = System.currentTimeMillis() - lastLootTime;
        if (timeElapsed >= COOLDOWN_TIME) {
            return 0;
        }

        return (int)((COOLDOWN_TIME - timeElapsed) / 1000);
    }

    @EventHandler
    public void onContainerInteract(PlayerInteractEvent event) {
    }
}
