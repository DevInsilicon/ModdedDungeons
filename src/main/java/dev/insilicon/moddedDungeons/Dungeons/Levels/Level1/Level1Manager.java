package dev.insilicon.moddedDungeons.Dungeons.Levels.Level1;

import dev.insilicon.moddedDungeons.ModdedDungeons;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

public class Level1Manager implements Listener {

    private int minX = -37;
    private int minY = 56;
    private int minZ = -113;
    private int maxX = 52;
    private int maxY = 70;
    private int maxZ = -31;
    private String worldName = "world";

    public Level1Manager() {
        loadCoordinatesFromConfig();
        setLevelCoordinatesInConfig();

        ModdedDungeons.instance.getServer().getPluginManager().registerEvents(this, ModdedDungeons.instance);

        Level1_Spawning spawning = new Level1_Spawning();
        ModdedDungeons.instance.getServer().getPluginManager().registerEvents(spawning, ModdedDungeons.instance);
        Level1LootHandler lootHandler = new Level1LootHandler();
        ModdedDungeons.instance.getServer().getPluginManager().registerEvents(lootHandler, ModdedDungeons.instance);
    }

    private void loadCoordinatesFromConfig() {
        ConfigurationSection positionSection = ModdedDungeons.instance.getConfig().getConfigurationSection("levels.level1.position");

        if (positionSection != null) {
            minX = positionSection.getInt("x", minX);
            minY = positionSection.getInt("y", minY);
            minZ = positionSection.getInt("z", minZ);
            maxX = positionSection.getInt("mx", maxX);
            maxY = positionSection.getInt("my", maxY);
            maxZ = positionSection.getInt("mz", maxZ);
        }
        worldName = ModdedDungeons.instance.getConfig().getString("levels.level1.world", "world");
    }

    private void setLevelCoordinatesInConfig() {
        ModdedDungeons.instance.getConfig().set("levels.level1.position.x", minX);
        ModdedDungeons.instance.getConfig().set("levels.level1.position.y", minY);
        ModdedDungeons.instance.getConfig().set("levels.level1.position.z", minZ);
        ModdedDungeons.instance.getConfig().set("levels.level1.position.mx", maxX);
        ModdedDungeons.instance.getConfig().set("levels.level1.position.my", maxY);
        ModdedDungeons.instance.getConfig().set("levels.level1.position.mz", maxZ);
        ModdedDungeons.instance.getConfig().set("levels.level1.world", worldName);
        ModdedDungeons.instance.saveConfig();
    }
}
