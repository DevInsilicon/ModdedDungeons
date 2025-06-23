package dev.insilicon.moddedDungeons.ItemManagement;

import dev.insilicon.moddedDungeons.ItemManagement.Items.BaseItem;
import dev.insilicon.moddedDungeons.ItemManagement.Items.Basic.Item.BasicWand;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemManager implements Listener {
    public static NamespacedKey itemKey = new NamespacedKey("modded_dungeons", "item_key");
    public static List<BaseItem> ItemTypes = new ArrayList<>();

    public ItemManager() {
        registery();
    }

    public void registery() {

        //Basic Tier
        ItemTypes.add(new BasicWand());

        // Tier 1
    }


    // Events
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (!event.getItem().getPersistentDataContainer().has(itemKey)) return;

        String itemName = event.getItem().getPersistentDataContainer().get(itemKey, PersistentDataType.STRING);
    }
}
