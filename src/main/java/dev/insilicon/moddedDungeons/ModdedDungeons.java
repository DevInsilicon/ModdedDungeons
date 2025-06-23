package dev.insilicon.moddedDungeons;

import dev.insilicon.moddedDungeons.ItemManagement.ItemManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ModdedDungeons extends JavaPlugin {

    public static ModdedDungeons instance;
    public static ItemManager itemManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        try {
            itemManager = new ItemManager();
            getServer().getPluginManager().registerEvents(itemManager, this);
            getLogger().info("ItemManager initialized successfully.");
        } catch (Exception e) {
            getLogger().severe("Failed to initialize ItemManager: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
