package dev.insilicon.moddedDungeons.EntityManagement.CustomEntities.Basic;

import dev.insilicon.moddedDungeons.EntityManagement.CustomEntities.BaseEntityArmor;
import dev.insilicon.moddedDungeons.EntityManagement.EntityManager;
import dev.insilicon.moddedDungeons.ItemManagement.Items.ArmorType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Basic Warrior entity - a simple custom entity that can wear armor and use weapons.
 * Demonstrates the entity management system functionality.
 */
public class BasicWarrior extends BaseEntityArmor {

    public BasicWarrior() {
        super("basic_warrior", 
              MiniMessage.miniMessage().deserialize("<gold>Basic Warrior"),
              List.of(
                  "<yellow>A basic warrior entity",
                  "<gray>Health: <red>30.0",
                  "<gray>Damage: <red>8.0",
                  "<gray>Can wear armor and use weapons"
              ),
              EntityType.ZOMBIE, // Base entity type
              EntityManager.entityKey,
              0, // Level requirement
              false, // Can spawn outside dungeon
              30.0, // Max health
              8.0, // Base damage
              ArmorType.CHESTPLATE, // Preferred armor type
              15.0, // Damage absorption percentage
              5.0, // Max HP absorption
              2.0); // Flat damage reduction
    }

    @Override
    public double entityAttacked(LivingEntity attacker, Entity victim, EntityDamageByEntityEvent event) {
        // Increase damage by 50% when attacking
        double baseDamage = event.getDamage();
        return baseDamage * 1.5;
    }

    @Override
    public double entityDamaged(LivingEntity entity, EntityDamageByEntityEvent event) {
        // Apply armor reduction first
        double damage = super.entityDamaged(entity, event);
        
        // Additional warrior-specific defense: reduce fire damage by 25%
        if (event.getCause() == EntityDamageByEntityEvent.DamageCause.FIRE ||
            event.getCause() == EntityDamageByEntityEvent.DamageCause.FIRE_TICK) {
            damage *= 0.75;
        }
        
        return damage;
    }

    @Override
    public void entityDeath(LivingEntity entity, EntityDeathEvent event) {
        // Drop extra experience when this warrior dies
        event.setDroppedExp(event.getDroppedExp() + 10);
    }

    @Override
    public void entitySpawn(LivingEntity entity) {
        // Set custom properties when warrior spawns
        entity.setCustomName(getDisplayName());
        entity.setCustomNameVisible(true);
    }

    @Override
    public boolean entityArmorEquipped(LivingEntity entity, ItemStack armor) {
        // Warriors prefer heavier armor - accept all armor types
        return true;
    }

    @Override
    public void entityArmorAttacked(LivingEntity attacker, Entity victim, EntityDamageByEntityEvent event) {
        // When warrior attacks while wearing armor, chance to knockback victim
        if (Math.random() < 0.2) { // 20% chance
            if (victim instanceof LivingEntity) {
                LivingEntity livingVictim = (LivingEntity) victim;
                // Simple knockback effect
                livingVictim.setVelocity(attacker.getLocation().getDirection().multiply(0.5));
            }
        }
    }
}