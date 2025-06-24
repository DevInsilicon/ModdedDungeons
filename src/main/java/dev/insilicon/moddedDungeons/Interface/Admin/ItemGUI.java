package dev.insilicon.moddedDungeons.Interface.Admin;

import dev.insilicon.moddedDungeons.ItemManagement.ItemManager;
import dev.insilicon.moddedDungeons.ItemManagement.Items.BaseItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ItemGUI implements Listener, CommandExecutor {

    private static final String INVENTORY_TITLE = "Item Selector";
    private static final Map<Player, Inventory> openInventories = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command!", NamedTextColor.RED));
            return true;
        }

        if (!player.hasPermission("moddeddungeons.admin.itemgui")) {
            player.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }

        openItemGUI(player);
        return true;
    }

    private void openItemGUI(Player player) {
        int itemCount = ItemManager.ItemTypes.size();
        int invSize = ((itemCount / 9) + (itemCount % 9 > 0 ? 1 : 0)) * 9;
        invSize = Math.max(9, Math.min(54, invSize)); // Min size 9, max size 54

        Inventory inventory = Bukkit.createInventory(null, invSize, Component.text(INVENTORY_TITLE));

        int slot = 0;
        for (BaseItem item : ItemManager.ItemTypes) {
            if (slot >= inventory.getSize()) break;

            ItemStack displayItem = item.getDefaultStack(1);
            inventory.setItem(slot, displayItem);
            slot++;
        }

        player.openInventory(inventory);
        openInventories.put(player, inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (!openInventories.containsKey(player)) return;

        if (event.getView().title().equals(Component.text(INVENTORY_TITLE))) {
            event.setCancelled(true); // Prevent taking items

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            BaseItem baseItem = ItemManager.getItemType(clickedItem);
            if (baseItem != null) {
                // Give the player the item
                ItemManager.giveItem(player, baseItem, 1);
                player.sendMessage(Component.text("You received: ", NamedTextColor.GREEN)
                        .append(clickedItem.displayName()));
            }
        }
    }
}
