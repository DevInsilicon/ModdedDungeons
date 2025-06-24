package dev.insilicon.moddedDungeons.ItemManagement.Items;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BaseArmor extends BaseItem {

    public ArmorType armorType;

    public double DamageAbsorbtion = 0.0; // Is a percent
    public double maxHPAbsorbtion = 0.0; // Max HP that can be absorbed
    public double DamageReduction = 0.0; // Is not a percent and is a flat value

    public BaseArmor(String name, Component displayName, List<String> lore, Material baseMaterial, NamespacedKey key,
                     double levelRequirement, boolean canBeUsedOutsideDungeon, ArmorType armorType,
                     double damageAbsorbtion, double maxHPAbsorbtion, double damageReduction) {
        super(name, displayName, lore, baseMaterial, key, levelRequirement, canBeUsedOutsideDungeon);
        this.armorType = armorType;
        this.DamageAbsorbtion = damageAbsorbtion;
        this.maxHPAbsorbtion = maxHPAbsorbtion;
        this.DamageReduction = damageReduction;
    }

    public double UserDamaged(Player player, double damage) {
        // Default damage absorption logic
        double absorbedDamage = damage * (DamageAbsorbtion / 100.0);
        if (absorbedDamage > maxHPAbsorbtion) {
            absorbedDamage = maxHPAbsorbtion;
        }
        double finalDamage = damage - absorbedDamage - DamageReduction;
        if (finalDamage < 0) {
            finalDamage = 0; // Prevent negative damage
        }
        return finalDamage;
    }

    @Override
    public double playerDamagedWhileHeld(Player player, EntityDamageByEntityEvent event) {
        // Override the base method to include armor-specific logic
        return event.getFinalDamage();
    }

    public void userAttacked(Player player, Entity victim, EntityDamageByEntityEvent event) {
        // Default implementation
    }

    public boolean userEquipped(Player player, ItemStack item) {
        return true;
    }
}
