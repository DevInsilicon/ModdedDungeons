package dev.insilicon.moddedDungeons.Playerdata;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import dev.insilicon.moddedDungeons.ModdedDungeons;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager implements Listener {
    private final MongoDB mongoDB;
    private final Map<UUID, PlayerData> playerDataCache;
    private BukkitTask autoSaveTask;
    private static final long AUTO_SAVE_INTERVAL = 5 * 60 * 20; // 5 minutes in ticks

    public PlayerDataManager() {
        this.mongoDB = new MongoDB();
        this.playerDataCache = new ConcurrentHashMap<>();
        startAutoSaveTask();
    }

    private void startAutoSaveTask() {
        autoSaveTask = new BukkitRunnable() {
            @Override
            public void run() {
                saveAllPlayerData();
            }
        }.runTaskTimerAsynchronously(ModdedDungeons.instance, AUTO_SAVE_INTERVAL, AUTO_SAVE_INTERVAL);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loadPlayerData(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        savePlayerData(player.getUniqueId());
        removeFromCache(player.getUniqueId());
    }

    public PlayerData getPlayerData(UUID playerUUID) {
        return playerDataCache.get(playerUUID);
    }

    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    public void loadPlayerData(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerData playerData = loadPlayerDataFromDatabase(player.getUniqueId(), player.getName());
                playerDataCache.put(player.getUniqueId(), playerData);
            }
        }.runTaskAsynchronously(ModdedDungeons.instance);
    }

    private PlayerData loadPlayerDataFromDatabase(UUID playerUUID, String playerName) {
        if (!mongoDB.isConnected()) {
            ModdedDungeons.instance.getLogger().warning("MongoDB is not connected. Creating default player data.");
            return new PlayerData(playerUUID, playerName);
        }

        try {
            Document document = mongoDB.getPlayerCollection()
                    .find(Filters.eq("uuid", playerUUID.toString()))
                    .first();

            if (document != null) {
                return new PlayerData(document);
            } else {
                PlayerData newPlayerData = new PlayerData(playerUUID, playerName);
                savePlayerDataToDatabase(newPlayerData);
                return newPlayerData;
            }
        } catch (Exception e) {
            ModdedDungeons.instance.getLogger().severe("Failed to load player data: " + e.getMessage());
            return new PlayerData(playerUUID, playerName);
        }
    }

    public void savePlayerData(UUID playerUUID) {
        PlayerData playerData = playerDataCache.get(playerUUID);
        if (playerData != null && playerData.isDirty()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    savePlayerDataToDatabase(playerData);
                    playerData.setDirty(false);
                }
            }.runTaskAsynchronously(ModdedDungeons.instance);
        }
    }

    private void savePlayerDataToDatabase(PlayerData playerData) {
        if (!mongoDB.isConnected()) {
            ModdedDungeons.instance.getLogger().warning("MongoDB is not connected. Cannot save player data.");
            return;
        }

        try {
            Document document = playerData.toDocument();
            UpdateResult result = mongoDB.getPlayerCollection().updateOne(
                    Filters.eq("uuid", playerData.getPlayerUUID().toString()),
                    new Document("$set", document),
                    new UpdateOptions().upsert(true)
            );

            if (result.wasAcknowledged()) {
                ModdedDungeons.instance.getLogger().fine("Saved player data for " + playerData.getPlayerName());
            } else {
                ModdedDungeons.instance.getLogger().warning("Failed to save player data for " + playerData.getPlayerName());
            }
        } catch (Exception e) {
            ModdedDungeons.instance.getLogger().severe("Error saving player data: " + e.getMessage());
        }
    }

    public void saveAllPlayerData() {
        for (UUID playerUUID : playerDataCache.keySet()) {
            savePlayerData(playerUUID);
        }
        ModdedDungeons.instance.getLogger().info("Saved all player data to database");
    }

    public void removeFromCache(UUID playerUUID) {
        playerDataCache.remove(playerUUID);
    }

    public void shutdown() {
        saveAllPlayerData();
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
        }
        mongoDB.disconnect();
    }
}
