package dev.insilicon.moddedDungeons.ItemManagement.Items;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class BaseArmor {

    public ArmorType armorType;

    public double DamageAbsorbtion = 0.0; // Is a percent
    public double maxHPAbsorbtion = 0.0; // Max HP that can be absorbed
    public double DamageReduction = 0.0; // Is not a percent and is a flat valueß

    public BaseArmor(ArmorType armorType, double damageAbsorbtion, double maxHPAbsorbtion, double damageReduction) {
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

    public void userAttacked(Player player, Entity victim, EntityDamageByEntityEvent event) {
        return;
    }

    public boolean userEquipped(Player player, ItemStack item) {
        return true;
    }
}

