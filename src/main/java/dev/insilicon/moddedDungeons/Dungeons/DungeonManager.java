package dev.insilicon.moddedDungeons.Dungeons;

import dev.insilicon.moddedDungeons.Dungeons.Levels.Level1.Level1Manager;
import dev.insilicon.moddedDungeons.ModdedDungeons;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class DungeonManager implements Listener
{
    public static List<RoomPosition> roomPositions = new ArrayList<>();


    public DungeonManager() {
        detectRoomPositions();

        Level1Manager level1Manager = new Level1Manager();
    }

    public void detectRoomPositions() {
        // Clear existing room positions before detecting new ones
        roomPositions.clear();

        // Get the configuration section containing the levels
        org.bukkit.configuration.ConfigurationSection levelsSection = ModdedDungeons.instance.getConfig().getConfigurationSection("levels");

        // If levels section doesn't exist, log and return
        if (levelsSection == null) {
            ModdedDungeons.instance.getLogger().warning("No 'levels' section found in the config file.");
            return;
        }

        // Loop through each level defined in the config
        for (String levelId : levelsSection.getKeys(false)) {
            org.bukkit.configuration.ConfigurationSection levelSection = levelsSection.getConfigurationSection(levelId);

            // Skip if this level section doesn't exist
            if (levelSection == null) continue;

            // Check if this level has position data
            if (levelSection.isSet("position")) {
                org.bukkit.configuration.ConfigurationSection positionSection = levelSection.getConfigurationSection("position");

                if (positionSection != null) {
                    // Extract position coordinates
                    int x = positionSection.getInt("x", 0);
                    int y = positionSection.getInt("y", 0);
                    int z = positionSection.getInt("z", 0);
                    int mx = positionSection.getInt("mx", 0);
                    int my = positionSection.getInt("my", 0);
                    int mz = positionSection.getInt("mz", 0);

                    // Create a new RoomPosition and add it to the list
                    RoomPosition roomPosition = new RoomPosition(levelId, x, y, z, mx, my, mz);
                    roomPositions.add(roomPosition);

                    ModdedDungeons.instance.getLogger().info("Detected room position for level: " + levelId);
                }
            }
        }

        ModdedDungeons.instance.getLogger().info("Detected " + roomPositions.size() + " room positions in total.");
    }

    public boolean isPlayerInDungeon(Player player) {
        return true;
    }

}

class RoomPosition {
    private String levelId;
    private int x,y,z;
    private int mx, my, mz;

    public RoomPosition(String levelId, int x, int y, int z, int mx, int my, int mz) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.mx = mx;
        this.my = my;
        this.mz = mz;
    }

    public String getLevelId() {
        return levelId;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getZ() {
        return z;
    }
    public int getMx() {
        return mx;
    }
    public int getMy() {
        return my;
    }
    public int getMz() {
        return mz;
    }

    public boolean isInRoom(LivingEntity entity) {
        if (entity == null) return false;
        int entityX = entity.getLocation().getBlockX();
        int entityY = entity.getLocation().getBlockY();
        int entityZ = entity.getLocation().getBlockZ();

        return (entityX >= x && entityX <= mx) &&
               (entityY >= y && entityY <= my) &&
               (entityZ >= z && entityZ <= mz);
    }
}