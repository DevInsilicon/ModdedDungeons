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
import org.bukkit.event.inventory.InventoryCloseEvent;
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
        int invSize = 54; // Always use double chest size (6 rows of 9 slots)
        Inventory inventory = Bukkit.createInventory(null, invSize, Component.text(INVENTORY_TITLE));

        int slot = 0;
        for (BaseItem item : ItemManager.ItemTypes) {
            if (slot >= inventory.getSize()) break;

            ItemStack displayItem = item.getDefaultStack(1);
            if (displayItem != null) {
                inventory.setItem(slot, displayItem);
                slot++;
            }
        }

        player.openInventory(inventory);
        openInventories.put(player, inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        if (event.getView().title().equals(Component.text(INVENTORY_TITLE))) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            BaseItem baseItem = ItemManager.getItemType(clickedItem);
            if (baseItem != null) {
                boolean success = ItemManager.giveItem(player, baseItem, 1);
                if (success) {
                    player.sendMessage(Component.text("You received: ", NamedTextColor.GREEN)
                            .append(clickedItem.displayName()));
                } else {
                    player.sendMessage(Component.text("Failed to give item. Inventory might be full.", NamedTextColor.RED));
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            openInventories.remove(player);
        }
    }
}
