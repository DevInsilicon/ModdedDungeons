package dev.insilicon.moddedDungeons.ItemManagement.Items.Basic.Item;

import dev.insilicon.moddedDungeons.ItemManagement.ItemManager;
import dev.insilicon.moddedDungeons.ItemManagement.Items.BaseItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BasicWand extends BaseItem {
    public BasicWand() {
        super("basic_wand", MiniMessage.miniMessage().deserialize("<pink>Basic Wand"),
                List.of(
                        "<gray>Basic wand that shoots a fireball"
                ),
                Material.STICK,
                ItemManager.itemKey,
                0,
                false);
    }

    @Override
    public ItemStack getDefaultStack(double amount) {
        return super.getDefaultStack(amount);
    }

    @Override
    public void interaction(Block block, Player player, ItemStack item) {
        player.sendMessage("FIRE BALL!");
    }
}
