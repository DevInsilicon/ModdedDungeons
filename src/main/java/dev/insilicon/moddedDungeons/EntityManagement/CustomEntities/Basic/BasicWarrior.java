package dev.insilicon.moddedDungeons.EntityManagement.CustomEntities.Basic;

import dev.insilicon.moddedDungeons.EntityManagement.CustomEntities.BaseEntityArmor;
import dev.insilicon.moddedDungeons.EntityManagement.EntityManager;
import dev.insilicon.moddedDungeons.ItemManagement.Items.ArmorType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

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
              EntityType.ZOMBIE,
              EntityManager.entityKey,
              0,
              30.0,
              8.0,
              ArmorType.CHESTPLATE,
              15.0,
              5.0,
              2.0);
    }

    @Override
    public double entityAttacked(LivingEntity attacker, Entity victim, EntityDamageByEntityEvent event) {
        double baseDamage = event.getDamage();
        return baseDamage * 1.5;
    }

    @Override
    public double entityDamaged(LivingEntity entity, EntityDamageByEntityEvent event) {
        double damage = super.entityDamaged(entity, event);
        if (event.getCause() == EntityDamageByEntityEvent.DamageCause.FIRE ||
            event.getCause() == EntityDamageByEntityEvent.DamageCause.FIRE_TICK) {
            damage *= 0.75;
        }
        return damage;
    }

    @Override
    public void entityDeath(LivingEntity entity, EntityDeathEvent event) {
        event.setDroppedExp(event.getDroppedExp() + 10);
    }

    @Override
    public void entitySpawn(LivingEntity entity) {
        entity.customName(getDisplayName());
        entity.setCustomNameVisible(true);

        java.util.Random random = new java.util.Random();
        org.bukkit.inventory.EntityEquipment equipment = entity.getEquipment();
        if (equipment != null) {
            equipment.setHelmet(randomArmorPiece(org.bukkit.Material.LEATHER_HELMET, org.bukkit.Material.CHAINMAIL_HELMET, org.bukkit.Material.GOLDEN_HELMET, org.bukkit.Material.IRON_HELMET, random));
            equipment.setChestplate(randomArmorPiece(org.bukkit.Material.LEATHER_CHESTPLATE, org.bukkit.Material.CHAINMAIL_CHESTPLATE, org.bukkit.Material.GOLDEN_CHESTPLATE, org.bukkit.Material.IRON_CHESTPLATE, random));
            equipment.setLeggings(randomArmorPiece(org.bukkit.Material.LEATHER_LEGGINGS, org.bukkit.Material.CHAINMAIL_LEGGINGS, org.bukkit.Material.GOLDEN_LEGGINGS, org.bukkit.Material.IRON_LEGGINGS, random));
            equipment.setBoots(randomArmorPiece(org.bukkit.Material.LEATHER_BOOTS, org.bukkit.Material.CHAINMAIL_BOOTS, org.bukkit.Material.GOLDEN_BOOTS, org.bukkit.Material.IRON_BOOTS, random));

            double weaponRoll = random.nextDouble();
            ItemStack weapon = null;
            if (weaponRoll < 0.10) {
                weapon = new ItemStack(org.bukkit.Material.STONE_AXE);
            } else if (weaponRoll < 0.20) {
                weapon = new ItemStack(org.bukkit.Material.IRON_SWORD);
            } else if (weaponRoll < 0.30) {
                weapon = new ItemStack(org.bukkit.Material.STONE_SWORD);
            } else if (weaponRoll < 0.80) {
                weapon = new ItemStack(org.bukkit.Material.WOODEN_SWORD);
            }
            if (weapon != null) {
                equipment.setItemInMainHand(weapon);
            }
        }

        if (random.nextDouble() < 0.05) {
            entity.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 0, false, false));
        }
    }

    private ItemStack randomArmorPiece(org.bukkit.Material leather, org.bukkit.Material chain, org.bukkit.Material gold, org.bukkit.Material iron, java.util.Random random) {
        double roll = random.nextDouble();
        ItemStack item = null;
        if (roll < 0.20) {
            item = new ItemStack(leather);
        } else if (roll < 0.40) {
            item = new ItemStack(chain);
        } else if (roll < 0.60) {
            item = new ItemStack(gold);
        } else if (roll < 0.85) {
            item = new ItemStack(iron);
        }
        if (item != null) {
            int protLevel = 1 + random.nextInt(2);
            item.addEnchantment(Enchantment.PROTECTION, protLevel);
        }
        return item;
    }

    @Override
    public boolean entityArmorEquipped(LivingEntity entity, ItemStack armor) {
        return true;
    }

    @Override
    public void entityArmorAttacked(LivingEntity attacker, Entity victim, EntityDamageByEntityEvent event) {
        if (Math.random() < 0.2) {
            if (victim instanceof LivingEntity) {
                LivingEntity livingVictim = (LivingEntity) victim;
                livingVictim.setVelocity(attacker.getLocation().getDirection().multiply(0.5));
            }
        }
    }
}