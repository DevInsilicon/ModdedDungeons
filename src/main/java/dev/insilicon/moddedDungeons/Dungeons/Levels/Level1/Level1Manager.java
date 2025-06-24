package dev.insilicon.moddedDungeons.Dungeons.Levels.Level1;

import dev.insilicon.moddedDungeons.ModdedDungeons;
import org.bukkit.event.Listener;

public class Level1Manager implements Listener {

    public Level1Manager() {
        ModdedDungeons.instance.getServer().getPluginManager().registerEvents(this, ModdedDungeons.instance);

        Level1_Spawning spawning = new Level1_Spawning();
        ModdedDungeons.instance.getServer().getPluginManager().registerEvents(spawning, ModdedDungeons.instance);
        Level1LootHandler lootHandler = new Level1LootHandler();
        ModdedDungeons.instance.getServer().getPluginManager().registerEvents(lootHandler, ModdedDungeons.instance);

    }


}
