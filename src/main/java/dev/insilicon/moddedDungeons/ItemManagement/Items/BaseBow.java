package dev.insilicon.moddedDungeons.ItemManagement.Items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BaseBow extends BaseItem {
    public BaseBow(String name, net.kyori.adventure.text.Component displayName, List<String> lore, Material baseMaterial, NamespacedKey key, double levelRequirement, boolean canBeUsedOutsideDungeon) {
        super(name, displayName, lore, baseMaterial, key, levelRequirement, canBeUsedOutsideDungeon);
    }

    public double playerShotWhileHeld(Player player, Entity victim, EntityDamageByEntityEvent event) {
        return event.getFinalDamage();
    }

    public double entityShotWhileHeld(LivingEntity shooter, Entity victim, EntityDamageByEntityEvent event) {
        return event.getFinalDamage();
    }

    public double entityAttackedWhileHeld(LivingEntity attacker, Entity victim, EntityDamageByEntityEvent event) {
        return entityShotWhileHeld(attacker, victim, event);
    }

    public void onArrowTick(org.bukkit.entity.Arrow arrow) {
    }
}
