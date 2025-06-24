package dev.insilicon.moddedDungeons.EntityManagement.CustomEntities;

import dev.insilicon.moddedDungeons.ItemManagement.Items.ArmorType;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Base class for custom entities that can wear and manage armor.
 * Extends BaseEntity with armor-specific functionality.
 */
public class BaseEntityArmor extends BaseEntity {

    public ArmorType preferredArmorType;
    public double damageAbsorption = 0.0; // Percentage
    public double maxHPAbsorption = 0.0; // Max HP that can be absorbed
    public double damageReduction = 0.0; // Flat damage reduction

    /**
     * Constructor for BaseEntityArmor with armor-specific parameters
     */
    public BaseEntityArmor(String name, Component displayName, List<String> lore,
                          org.bukkit.entity.EntityType baseEntityType, NamespacedKey key,
                          double levelRequirement, boolean canSpawnOutsideDungeon,
                          double maxHealth, double damage, ArmorType preferredArmorType,
                          double damageAbsorption, double maxHPAbsorption, double damageReduction) {
        super(name, displayName, lore, baseEntityType, key, levelRequirement, 
              canSpawnOutsideDungeon, maxHealth, damage);
        this.preferredArmorType = preferredArmorType;
        this.damageAbsorption = damageAbsorption;
        this.maxHPAbsorption = maxHPAbsorption;
        this.damageReduction = damageReduction;
    }

    /**
     * Calculate damage reduction when this entity takes damage while wearing armor
     */
    public double entityArmorDamaged(LivingEntity entity, double damage) {
        // Default armor damage absorption logic
        double absorbedDamage = damage * (damageAbsorption / 100.0);
        if (absorbedDamage > maxHPAbsorption) {
            absorbedDamage = maxHPAbsorption;
        }
        double finalDamage = damage - absorbedDamage - damageReduction;
        if (finalDamage < 0) {
            finalDamage = 0; // Prevent negative damage
        }
        return finalDamage;
    }

    @Override
    public double entityDamaged(LivingEntity entity, EntityDamageByEntityEvent event) {
        // Override the base method to include armor-specific logic
        return entityArmorDamaged(entity, event.getDamage());
    }

    /**
     * Called when this entity attacks another entity while wearing armor
     */
    public void entityArmorAttacked(LivingEntity attacker, Entity victim, EntityDamageByEntityEvent event) {
        // Default implementation - can be overridden
    }

    /**
     * Called when this entity equips armor
     */
    public boolean entityArmorEquipped(LivingEntity entity, ItemStack armor) {
        return true; // Default implementation
    }

    // Getter methods for armor properties
    public ArmorType getPreferredArmorType() {
        return preferredArmorType;
    }

    public double getDamageAbsorption() {
        return damageAbsorption;
    }

    public double getMaxHPAbsorption() {
        return maxHPAbsorption;
    }

    public double getDamageReduction() {
        return damageReduction;
    }
}