package dev.insilicon.moddedDungeons.Dungeons.Levels.Level1;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;

public class Level1LootHandler implements Listener {

    private static HashMap<Location, Block> lootChests = new HashMap<>();
    private static HashMap<Location, Block> lootBarrel = new HashMap<>();
    private static HashMap<Location, Block> trapChest = new HashMap<>();

    public Level1LootHandler() {

        // detect all of the loot chests from the config


    }

}
