package dev.insilicon.moddedDungeons.Dungeons;

import dev.insilicon.moddedDungeons.Dungeons.Levels.Level1.Level1Manager;
import dev.insilicon.moddedDungeons.ModdedDungeons;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.ArrayList;
import java.util.List;

public class DungeonManager implements Listener
{
    public static List<RoomPosition> roomPositions = new ArrayList<>();

    public DungeonManager() {
        Level1Manager level1Manager = new Level1Manager();
        detectRoomPositions();
    }

    public void detectRoomPositions() {
        roomPositions.clear();
        org.bukkit.configuration.ConfigurationSection levelsSection = ModdedDungeons.instance.getConfig().getConfigurationSection("levels");
        if (levelsSection == null) {
            ModdedDungeons.instance.getLogger().warning("No 'levels' section found in the config file.");
            return;
        }
        for (String levelId : levelsSection.getKeys(false)) {
            org.bukkit.configuration.ConfigurationSection levelSection = levelsSection.getConfigurationSection(levelId);
            if (levelSection == null) continue;
            if (levelSection.isSet("position")) {
                org.bukkit.configuration.ConfigurationSection positionSection = levelSection.getConfigurationSection("position");
                if (positionSection != null) {
                    int x = positionSection.getInt("x", 0);
                    int y = positionSection.getInt("y", 0);
                    int z = positionSection.getInt("z", 0);
                    int mx = positionSection.getInt("mx", 0);
                    int my = positionSection.getInt("my", 0);
                    int mz = positionSection.getInt("mz", 0);
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

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (entity.getPersistentDataContainer().has(dev.insilicon.moddedDungeons.EntityManagement.EntityManager.entityKey, org.bukkit.persistence.PersistentDataType.STRING)) {
            return;
        }
        String worldName = entity.getWorld().getName();
        if (!"dungeons".equals(worldName)) {
            return;
        }
        boolean inDungeonRoom = false;
        for (RoomPosition room : roomPositions) {
            if (room.isInRoom(entity)) {
                inDungeonRoom = true;
                break;
            }
        }
        if (inDungeonRoom) {
            ModdedDungeons.instance.getServer().getScheduler().runTaskLater(
                ModdedDungeons.instance, 
                () -> {
                    if (entity.isValid() && !entity.getPersistentDataContainer().has(dev.insilicon.moddedDungeons.EntityManagement.EntityManager.entityKey, org.bukkit.persistence.PersistentDataType.STRING)) {
                        entity.remove();
                        ModdedDungeons.instance.getLogger().info("Removed vanilla entity: " + entity.getType() + " in dungeon room");
                    }
                }, 
                1L
            );
        }
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equals("dungeons") && event.getItem().getType() == Material.CHORUS_FRUIT) {
            event.setCancelled(true);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You cannot eat chorus fruit in the dungeon!"));
        }
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