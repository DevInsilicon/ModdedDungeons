package dev.insilicon.moddedDungeons.Playerdata;

import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData {
    private final UUID playerUUID;
    private final String playerName;
    private Document data;
    private boolean isDirty;

    public PlayerData(UUID playerUUID, String playerName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.data = new Document();
        this.isDirty = false;
    }

    public PlayerData(Document document) {
        this.playerUUID = UUID.fromString(document.getString("uuid"));
        this.playerName = document.getString("name");
        this.data = document;
        this.isDirty = false;
    }

    public Document toDocument() {
        data.put("uuid", playerUUID.toString());
        data.put("name", playerName);
        return data;
    }

    public static PlayerData fromPlayer(Player player) {
        return new PlayerData(player.getUniqueId(), player.getName());
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Object getValue(String key) {
        return data.get(key);
    }

    public void setValue(String key, Object value) {
        data.put(key, value);
        this.isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }
}
