package dev.insilicon.moddedDungeons.EntityManagement.CustomEntities;

import dev.insilicon.moddedDungeons.ItemManagement.Items.BaseItem;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Base class for all custom entities in the modded dungeons system.
 * Provides extensibility points and events similar to the item management system.
 * Custom entities can wear armor, use items, and have complex interactions.
 */
public class BaseEntity implements Listener {

    public String name;
    public Component displayName;
    public List<String> lore;
    public org.bukkit.entity.EntityType baseEntityType;
    private NamespacedKey namespaceKey;
    public double levelRequirement = 0;
    public boolean canSpawnOutsideDungeon = true;
    public double maxHealth = 20.0;
    public double damage = 1.0;

    /**
     * Constructor for BaseEntity with all parameters
     */
    public BaseEntity(String name, Component displayName, List<String> lore, 
                     org.bukkit.entity.EntityType baseEntityType, NamespacedKey key, 
                     double levelRequirement, boolean canSpawnOutsideDungeon,
                     double maxHealth, double damage) {
        this.name = name;
        this.displayName = displayName;
        this.lore = lore;
        this.baseEntityType = baseEntityType;
        this.namespaceKey = key;
        this.levelRequirement = levelRequirement;
        this.canSpawnOutsideDungeon = canSpawnOutsideDungeon;
        this.maxHealth = maxHealth;
        this.damage = damage;
    }

    /**
     * Called when this entity attacks another entity
     */
    public double entityAttacked(LivingEntity attacker, Entity victim, EntityDamageByEntityEvent event) {
        return event.getFinalDamage(); // Default behavior, can be overridden
    }

    /**
     * Called when this entity is damaged by another entity
     */
    public double entityDamaged(LivingEntity entity, EntityDamageByEntityEvent event) {
        return event.getFinalDamage(); // Default behavior, can be overridden
    }

    /**
     * Called when this entity dies
     */
    public void entityDeath(LivingEntity entity, EntityDeathEvent event) {
        // Default implementation - can be overridden
    }

    /**
     * Called when this entity spawns
     */
    public void entitySpawn(LivingEntity entity) {
        // Default implementation - can be overridden
    }

    /**
     * Trigger an item interaction from this entity
     * This allows entities to use items similar to players
     */
    public void triggerItemInteraction(LivingEntity entity, BaseItem item, Block block, ItemStack itemStack) {
        if (item != null) {
            item.entityInteraction(entity, block, itemStack);
        }
    }

    /**
     * Called when this entity attempts to use an item
     */
    public void useItem(LivingEntity entity, BaseItem item, ItemStack itemStack) {
        // Default implementation - can be overridden
    }

    /**
     * Called when this entity equips armor
     */
    public void armorEquipped(LivingEntity entity, ItemStack armor) {
        // Default implementation - can be overridden
    }

    /**
     * Called when this entity's armor is damaged
     */
    public void armorDamaged(LivingEntity entity, ItemStack armor, double damage) {
        // Default implementation - can be overridden
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public org.bukkit.entity.EntityType getBaseEntityType() {
        return baseEntityType;
    }

    public NamespacedKey getNamespaceKey() {
        return namespaceKey;
    }

    public double getLevelRequirement() {
        return levelRequirement;
    }

    public boolean isCanSpawnOutsideDungeon() {
        return canSpawnOutsideDungeon;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getDamage() {
        return damage;
    }
}
