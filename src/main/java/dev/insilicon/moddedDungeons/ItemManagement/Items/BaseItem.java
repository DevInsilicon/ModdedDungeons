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

    public void drop(Player player, ItemStack item, boolean isBecauseDeath) {

    }

    /**
     * Called when an entity (not player) uses this item.
     * This provides more control for custom entities using weapons or items,
     * allowing different behaviors than player interactions.
     * 
     * <p>This method is called through the entity management system when
     * a custom entity attempts to use this item. It allows for entity-specific
     * item behaviors that may differ from player usage.</p>
     * 
     * @param entity The entity using this item
     * @param block The block being interacted with (can be null)
     * @param item The ItemStack being used
     */
    public void entityInteraction(org.bukkit.entity.LivingEntity entity, Block block, ItemStack item) {
        // Default implementation - can be overridden
    }

    /**
     * Called when an entity attacks another entity while holding this item.
     * This allows custom entity weapon behaviors that may differ from
     * player weapon behaviors.
     * 
     * <p>Since entities cannot handle advanced AI for weapons, this method
     * provides fine-grained control over how entities use weapons, allowing
     * for different damage calculations, effects, or behaviors.</p>
     * 
     * @param attacker The entity attacking with this item
     * @param victim The entity being attacked
     * @param event The damage event containing damage and cause information
     * @return The final damage to be dealt (can be modified from original)
     */
    public double entityAttackedWhileHeld(org.bukkit.entity.LivingEntity attacker, Entity victim, EntityDamageByEntityEvent event) {
        return event.getFinalDamage(); // Default behavior, can be overridden
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
