package dev.insilicon.moddedDungeons.EntityManagement;

import dev.insilicon.moddedDungeons.EntityManagement.CustomEntities.BaseEntity;
import dev.insilicon.moddedDungeons.EntityManagement.CustomEntities.BaseEntityArmor;
import dev.insilicon.moddedDungeons.EntityManagement.CustomEntities.Basic.BasicArcher;
import dev.insilicon.moddedDungeons.EntityManagement.CustomEntities.Basic.BasicWarrior;
import dev.insilicon.moddedDungeons.ItemManagement.ItemManager;
import dev.insilicon.moddedDungeons.ItemManagement.Items.BaseArmor;
import dev.insilicon.moddedDungeons.ItemManagement.Items.BaseItem;
import dev.insilicon.moddedDungeons.ModdedDungeons;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Manages custom entities in the modded dungeons system.
 * Handles entity events, armor interactions, and item usage for custom entities.
 * Modeled after the ItemManager architecture for consistency.
 * 
 * <p>The EntityManager serves as the central hub for all custom entity functionality,
 * providing event handling, registration, and interaction management. It follows
 * the same architectural patterns as ItemManager to ensure consistency and
 * maintainability across the plugin.</p>
 * 
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Registry and management of custom entity types</li>
 *   <li>Event handling for entity spawn, death, damage, and attack events</li>
 *   <li>Integration with the item management system for entity weapons and armor</li>
 *   <li>Persistent data management for entity identification</li>
 *   <li>Entity spawning and configuration</li>
 * </ul>
 * 
 * <p>Event Processing Order:</p>
 * <ol>
 *   <li>Entity damage/attack events are captured</li>
 *   <li>Entity-specific behaviors are applied</li>
 *   <li>Armor interactions are processed</li>
 *   <li>Weapon interactions are handled</li>
 *   <li>Final damage/effects are applied</li>
 * </ol>
 * 
 * <p>Usage:</p>
 * <pre>{@code
 * // Register EntityManager in your plugin
 * entityManager = new EntityManager();
 * getServer().getPluginManager().registerEvents(entityManager, this);
 * 
 * // Spawn a custom entity
 * BaseEntity warriorType = EntityManager.getEntityType("basic_warrior");
 * LivingEntity warrior = EntityManager.spawnEntity(warriorType, location);
 * }</pre>
 * 
 * @see BaseEntity
 * @see BaseEntityArmor
 * @see ItemManager
 * @since 1.0
 */
public class EntityManager implements Listener {
    
    public static NamespacedKey entityKey = new NamespacedKey("modded_dungeons", "entity_key");
    public static NamespacedKey fromDungeonKey = new NamespacedKey("modded_dungeons", "from_dungeon");
    public static List<BaseEntity> EntityTypes = new ArrayList<>();
    public static List<BaseEntityArmor> PrecompiledArmorEntityTypes = new ArrayList<>();

    public EntityManager() {
        registry();
    }

    /**
     * Registry method to register all custom entity types
     */
    public void registry() {
        // Clear lists to avoid duplicates if registry() is called multiple times
        EntityTypes.clear();
        PrecompiledArmorEntityTypes.clear();

        // Basic Tier entities
        EntityTypes.add(new BasicWarrior());
        EntityTypes.add(new BasicArcher());

        // Precompile armor entity types
        for (BaseEntity entity : EntityTypes) {
            if (entity instanceof BaseEntityArmor) {
                PrecompiledArmorEntityTypes.add((BaseEntityArmor) entity);
            }
        }
    }

    // Events
    @EventHandler(priority = org.bukkit.event.EventPriority.LOW)
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        BaseEntity entityType = getEntityType(entity);
        
        // If the entity does not have the key, but matches a custom type by class, assign the key
        if (entityType == null) {
            // Try to match by entity type (e.g., for natural spawns)
            for (BaseEntity type : EntityTypes) {
                if (type.getBaseEntityType() == entity.getType()) {
                    entity.getPersistentDataContainer().set(entityKey, org.bukkit.persistence.PersistentDataType.STRING, type.getName());
                    entityType = type;
                    break;
                }
            }
        }
        
        // If we found a custom entity type, configure it
        if (entityType != null) {
            // Always ensure the key is set for custom entities
            entity.getPersistentDataContainer().set(entityKey, org.bukkit.persistence.PersistentDataType.STRING, entityType.getName());
            entityType.entitySpawn(entity);
            ModdedDungeons.instance.getLogger().info("Custom entity configured: " + entityType.getName() + " at " + entity.getLocation());
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        BaseEntity entityType = getEntityType(entity);
        if (entityType == null) return;

        entityType.entityDeath(entity, event);
    }

    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent event) {
        entityDamagedByEntity(event);
        entityArmorDamaged(event);
        entityArmorAttacked(event);
        entityArmorEquipped(event);
        entityAttackedByEntity(event);
        entityWeaponAttack(event);
    }

    /**
     * Handle entity attacking another entity
     */
    public void entityAttackedByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity)) return;
        
        LivingEntity attacker = (LivingEntity) event.getDamager();
        BaseEntity entityType = getEntityType(attacker);
        if (entityType == null) return;

        double originalDamage = event.getDamage();
        double modifiedDamage = entityType.entityAttacked(attacker, event.getEntity(), event);
        event.setDamage(modifiedDamage);
    }

    /**
     * Handle entity being damaged by another entity
     */
    public void entityDamagedByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        
        LivingEntity victim = (LivingEntity) event.getEntity();
        BaseEntity entityType = getEntityType(victim);
        if (entityType == null) return;

        double originalDamage = event.getDamage();
        double modifiedDamage = entityType.entityDamaged(victim, event);
        event.setDamage(modifiedDamage);
    }

    /**
     * Handle entity weapon attacks (entities using weapons)
     */
    public void entityWeaponAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity)) return;
        
        LivingEntity attacker = (LivingEntity) event.getDamager();
        EntityEquipment equipment = attacker.getEquipment();
        if (equipment == null) return;

        ItemStack weapon = equipment.getItemInMainHand();
        if (weapon == null) return;

        BaseItem itemType = ItemManager.getItemType(weapon);
        if (itemType == null) return;

        // Use the new entity weapon interaction method
        double originalDamage = event.getDamage();
        double modifiedDamage = itemType.entityAttackedWhileHeld(attacker, event.getEntity(), event);
        event.setDamage(modifiedDamage);
    }

    /**
     * Handle entity armor damage absorption
     */
    public void entityArmorDamaged(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        
        LivingEntity entity = (LivingEntity) event.getEntity();
        BaseEntity entityType = getEntityType(entity);
        if (!(entityType instanceof BaseEntityArmor)) return;

        BaseEntityArmor armoredEntity = (BaseEntityArmor) entityType;
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        // Check all armor pieces
        ItemStack[] armorContents = equipment.getArmorContents();
        for (ItemStack armorPiece : armorContents) {
            if (armorPiece == null) continue;

            BaseItem itemType = ItemManager.getItemType(armorPiece);
            if (itemType instanceof BaseArmor) {
                BaseArmor baseArmor = (BaseArmor) itemType;
                double originalDamage = event.getDamage();
                double reducedDamage = baseArmor.UserDamaged((Player) entity, originalDamage);
                event.setDamage(reducedDamage);
            }
        }

        // Apply entity-specific armor damage reduction
        double originalDamage = event.getDamage();
        double reducedDamage = armoredEntity.entityArmorDamaged(entity, originalDamage);
        event.setDamage(reducedDamage);
    }

    /**
     * Handle entity armor attack events
     */
    public void entityArmorAttacked(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity)) return;
        
        LivingEntity attacker = (LivingEntity) event.getDamager();
        BaseEntity entityType = getEntityType(attacker);
        if (!(entityType instanceof BaseEntityArmor)) return;

        BaseEntityArmor armoredEntity = (BaseEntityArmor) entityType;
        armoredEntity.entityArmorAttacked(attacker, event.getEntity(), event);
    }

    /**
     * Handle entity armor equip events
     */
    public void entityArmorEquipped(EntityDamageByEntityEvent event) {
        // Check both attacker and victim for armor equip events
        LivingEntity entity = null;
        if (event.getEntity() instanceof LivingEntity) {
            entity = (LivingEntity) event.getEntity();
        } else if (event.getDamager() instanceof LivingEntity) {
            entity = (LivingEntity) event.getDamager();
        }

        if (entity == null) return;

        BaseEntity entityType = getEntityType(entity);
        if (!(entityType instanceof BaseEntityArmor)) return;

        BaseEntityArmor armoredEntity = (BaseEntityArmor) entityType;
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        ItemStack[] armorContents = equipment.getArmorContents();
        for (ItemStack armorPiece : armorContents) {
            if (armorPiece == null) continue;
            armoredEntity.entityArmorEquipped(entity, armorPiece);
        }
    }

    /**
     * Get the BaseEntity type from a living entity
     */
    public static BaseEntity getEntityType(LivingEntity entity) {
        if (!entity.getPersistentDataContainer().has(entityKey, PersistentDataType.STRING)) {
            return null;
        }

        String entityName = entity.getPersistentDataContainer().get(entityKey, PersistentDataType.STRING);
        if (entityName == null) return null;

        for (BaseEntity entityType : EntityTypes) {
            if (entityType.name.equals(entityName)) {
                return entityType;
            }
        }
        
        ModdedDungeons.instance.getLogger().log(Level.WARNING, "Entity not found: " + entityName);
        return null;
    }

    /**
     * Spawn a custom entity at a location
     */
    public static LivingEntity spawnEntity(BaseEntity entityType, org.bukkit.Location location) {
        if (entityType == null || location == null || location.getWorld() == null) {
            return null;
        }

        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, entityType.getBaseEntityType());
        if (entity == null) return null;

        // IMMEDIATELY set the persistence key to avoid cancellation by DungeonManager
        entity.getPersistentDataContainer().set(entityKey, PersistentDataType.STRING, entityType.getName());

        // Set entity properties
        entity.customName(entityType.getDisplayName());
        entity.setCustomNameVisible(true);
        entity.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(entityType.getMaxHealth());
        entity.setHealth(entityType.getMaxHealth());

        // Trigger spawn event
        entityType.entitySpawn(entity);

        ModdedDungeons.instance.getLogger().info("Successfully spawned custom entity: " + entityType.getName() + " at " + location);
        return entity;
    }
}
