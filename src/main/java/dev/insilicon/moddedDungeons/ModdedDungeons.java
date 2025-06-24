package dev.insilicon.moddedDungeons;

import dev.insilicon.moddedDungeons.Dungeons.DungeonManager;
import dev.insilicon.moddedDungeons.Interface.Admin.ItemGUI;
import dev.insilicon.moddedDungeons.ItemManagement.ItemManager;
import dev.insilicon.moddedDungeons.Playerdata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public final class ModdedDungeons extends JavaPlugin {

    public static ModdedDungeons instance;
    public static ItemManager itemManager;
    public static DungeonManager dungeonManager;
    public static PlayerDataManager playerDataManager;
    private ItemGUI itemGUI;

    @Override
    public void onEnable() {
        instance = this;

        // init config file
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // test write / rad config
        getConfig().set("test", "true");
        if (getConfig().get("test") != "true") System.out.println("smth failed ");
        saveConfig();

        try {
            itemManager = new ItemManager();
            getServer().getPluginManager().registerEvents(itemManager, this);
            getLogger().info("ItemManager initialized successfully.");

            dungeonManager = new DungeonManager();
            getServer().getPluginManager().registerEvents(dungeonManager, this);

            playerDataManager = new PlayerDataManager();
            getServer().getPluginManager().registerEvents(playerDataManager, this);
            getLogger().info("PlayerDataManager initialized successfully.");

            itemGUI = new ItemGUI();
            getServer().getPluginManager().registerEvents(itemGUI, this);
            getLogger().info("ItemGUI initialized successfully.");

            registerCommand("itemgui", "Open the custom item GUI", "/itemgui", itemGUI);

        } catch (Exception e) {
            getLogger().severe("Failed to initialize plugin components: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void registerCommand(String name, String description, String usage, Object executor) {
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            BukkitCommand command = new BukkitCommand(name) {
                @Override
                public boolean execute(org.bukkit.command.CommandSender sender, String commandLabel, String[] args) {
                    if (executor instanceof org.bukkit.command.CommandExecutor) {
                        return ((org.bukkit.command.CommandExecutor) executor).onCommand(sender, this, commandLabel, args);
                    }
                    return false;
                }
            };

            command.setDescription(description);
            command.setUsage(usage);
            command.setPermission("moddeddungeons.admin." + name);

            commandMap.register(getName(), command);
            getLogger().info(name + " command registered programmatically");

        } catch (Exception e) {
            getLogger().severe("Failed to register command '" + name + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.shutdown();
            getLogger().info("PlayerDataManager shut down successfully.");
        }
    }
}
