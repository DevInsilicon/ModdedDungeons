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
 * 
 * <p>This class serves as the foundation for creating custom entities with advanced
 * behaviors. It provides event handlers for common entity actions such as attacking,
 * taking damage, spawning, and dying. The architecture mirrors the BaseItem system
 * to maintain consistency and familiarity for developers.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Event-driven architecture with extensible methods</li>
 *   <li>Integration with the item management system</li>
 *   <li>Support for custom behaviors through method overriding</li>
 *   <li>Persistent data storage for entity identification</li>
 *   <li>Level requirements and dungeon restrictions</li>
 * </ul>
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * public class CustomBeast extends BaseEntity {
 *     public CustomBeast() {
 *         super("custom_beast", Component.text("Custom Beast"), 
 *               List.of("A fierce custom beast"), EntityType.WOLF,
 *               key, 5, false, 25.0, 7.0);
 *     }
 *     
 *     @Override
 *     public double entityAttacked(LivingEntity attacker, Entity victim, EntityDamageByEntityEvent event) {
 *         // Custom attack behavior
 *         return event.getDamage() * 1.2; // 20% damage boost
 *     }
 * }
 * }</pre>
 * 
 * @see BaseEntityArmor
 * @see EntityManager
 * @since 1.0
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
     * Constructor for BaseEntity with all parameters.
     * 
     * @param name The internal name identifier for this entity type
     * @param displayName The display name shown to players
     * @param lore Description lines for this entity type
     * @param baseEntityType The base Bukkit entity type to use
     * @param key The NamespacedKey for persistent data storage
     * @param levelRequirement Minimum level required to spawn this entity
     * @param canSpawnOutsideDungeon Whether this entity can spawn outside dungeons
     * @param maxHealth Maximum health points for this entity
     * @param damage Base damage dealt by this entity
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
     * Called when this entity attacks another entity.
     * Override this method to implement custom attack behaviors.
     * 
     * @param attacker The entity performing the attack
     * @param victim The entity being attacked
     * @param event The damage event containing damage information
     * @return The final damage to be dealt (can be modified from original)
     */
    public double entityAttacked(LivingEntity attacker, Entity victim, EntityDamageByEntityEvent event) {
        return event.getFinalDamage(); // Default behavior, can be overridden
    }

    /**
     * Called when this entity takes damage from another entity.
     * Override this method to implement custom damage reduction or effects.
     * 
     * @param entity The entity taking damage
     * @param event The damage event containing damage information
     * @return The final damage to be taken (can be modified from original)
     */
    public double entityDamaged(LivingEntity entity, EntityDamageByEntityEvent event) {
        return event.getFinalDamage(); // Default behavior, can be overridden
    }

    /**
     * Called when this entity dies.
     * Override this method to implement custom death behaviors such as
     * special loot drops, effects, or spawning other entities.
     * 
     * @param entity The entity that died
     * @param event The death event containing drop and experience information
     */
    public void entityDeath(LivingEntity entity, EntityDeathEvent event) {
        // Default implementation - can be overridden
    }

    /**
     * Called when this entity spawns.
     * Override this method to set up initial entity state, equipment,
     * or trigger spawn effects.
     * 
     * @param entity The entity that spawned
     */
    public void entitySpawn(LivingEntity entity) {
        // Default implementation - can be overridden
    }

    /**
     * Trigger an item interaction from this entity.
     * This allows entities to use items similar to players, providing
     * a bridge between the entity and item management systems.
     * 
     * @param entity The entity using the item
     * @param item The BaseItem being used
     * @param block The block being interacted with (can be null)
     * @param itemStack The actual ItemStack being used
     */
    public void triggerItemInteraction(LivingEntity entity, BaseItem item, Block block, ItemStack itemStack) {
        if (item != null) {
            item.entityInteraction(entity, block, itemStack);
        }
    }

    /**
     * Called when this entity attempts to use an item.
     * Override this method to implement custom item usage behaviors.
     * 
     * @param entity The entity using the item
     * @param item The BaseItem being used
     * @param itemStack The actual ItemStack being used
     */
    public void useItem(LivingEntity entity, BaseItem item, ItemStack itemStack) {
        // Default implementation - can be overridden
    }

    /**
     * Called when this entity equips armor.
     * Override this method to implement custom armor equip behaviors.
     * 
     * @param entity The entity equipping armor
     * @param armor The armor ItemStack being equipped
     */
    public void armorEquipped(LivingEntity entity, ItemStack armor) {
        // Default implementation - can be overridden
    }

    /**
     * Called when this entity's armor takes damage.
     * Override this method to implement custom armor damage behaviors.
     * 
     * @param entity The entity whose armor is damaged
     * @param armor The armor ItemStack that was damaged
     * @param damage The amount of damage the armor absorbed
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
