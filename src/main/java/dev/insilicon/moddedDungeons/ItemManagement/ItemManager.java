package dev.insilicon.moddedDungeons.ItemManagement;

import dev.insilicon.moddedDungeons.ItemManagement.Items.BaseItem;
import dev.insilicon.moddedDungeons.ItemManagement.Items.Basic.Item.BasicWand;
import dev.insilicon.moddedDungeons.ModdedDungeons;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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



    }

    public static BaseItem getItemType(ItemStack item) {
        if (!item.getPersistentDataContainer().has(itemKey, PersistentDataType.STRING)) return null;

        String itemName = item.getPersistentDataContainer().get(itemKey, PersistentDataType.STRING);
        if (itemName == null) return null;

        for (BaseItem item2 : ItemTypes) {
            if (item2.name.equals(itemName)) {
                return item2;
            }
        }
        ModdedDungeons.instance.getLogger().log(Level.WARNING,"Item not found?? " + itemName);
        return null;
    }
}
