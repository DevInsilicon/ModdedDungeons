package dev.insilicon.moddedDungeons.ItemManagement.Items;

import net.kyori.adventure.key.Namespaced;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class BaseItem implements Listener {

    public String name;
    public Component displayName;
    public List<String> lore;
    public Material baseMaterial;
    private NamespacedKey namespaceKey;
    public double levelRequirement = 0;
    public boolean canBeUsedOutsideDungeon = true;

    public BaseItem(String name,Component displayName, List<String> lore, Material baseMaterial, NamespacedKey key, double levelRequirement, boolean canBeUsedOutsideDungeon) {
        this.name = name;
        this.displayName = displayName;
        this.lore = lore;
        this.baseMaterial = baseMaterial;
        this.namespaceKey = key;
        this.levelRequirement = levelRequirement;
        this.canBeUsedOutsideDungeon = canBeUsedOutsideDungeon;
    }

    public ItemStack getDefaultStack(double amount) {
        ItemStack itemStack = new ItemStack(Material.STONE, 1);
        ItemMeta errItemMeta = itemStack.getItemMeta();
        errItemMeta.displayName(displayName);
        errItemMeta.lore(List.of(MiniMessage.miniMessage().deserialize("<red>Corrupted Item!")));
        itemStack.setItemMeta(errItemMeta);
        itemStack.setAmount((int) amount);
        return itemStack;
    }

    public double playerAttackedWhileHeld(Player player, Entity victim, EntityDamageByEntityEvent event) {
        return event.getFinalDamage(); // Default behavior, can be overridden
    }

    public double playerDamagedWhileHeld(Player player, EntityDamageByEntityEvent event) {
        return event.getFinalDamage(); // Default behavior, can be overridden
    }

    public void interaction(Block block, Player player, ItemStack item) {

    }

    public void pickup(Player player, ItemStack item) {

    }

    public void drop(Player player, ItemStack item) {

    }



    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public Material getBaseMaterial() {
        return baseMaterial;
    }

    public NamespacedKey getNamespaceKey() {
        return namespaceKey;
    }

    public double getLevelRequirement() {
        return levelRequirement;
    }

    public boolean isCanBeUsedOutsideDungeon() {
        return canBeUsedOutsideDungeon;
    }
}
