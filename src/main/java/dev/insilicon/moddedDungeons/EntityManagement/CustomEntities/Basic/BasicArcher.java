package dev.insilicon.moddedDungeons.EntityManagement.CustomEntities.Basic;

import dev.insilicon.moddedDungeons.EntityManagement.CustomEntities.BaseEntityArmor;
import dev.insilicon.moddedDungeons.EntityManagement.EntityManager;
import dev.insilicon.moddedDungeons.ItemManagement.Items.ArmorType;
import dev.insilicon.moddedDungeons.ItemManagement.ItemManager;
import dev.insilicon.moddedDungeons.ItemManagement.Items.BaseBow;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class BasicArcher extends BaseEntityArmor {
    public BasicArcher() {
        super("basic_archer",
                MiniMessage.miniMessage().deserialize("<green>Basic Archer"),
                List.of(
                        "<yellow>A basic archer entity",
                        "<gray>Health: <red>24.0",
                        "<gray>Damage: <red>6.0",
                        "<gray>Can use bows and wear armor"
                ),
                EntityType.SKELETON,
                EntityManager.entityKey,
                0,
                24.0,
                6.0,
                ArmorType.CHESTPLATE,
                10.0,
                3.0,
                1.0);
    }

    @Override
    public double entityAttacked(LivingEntity attacker, Entity victim, EntityDamageByEntityEvent event) {
        double baseDamage = event.getDamage();
        return baseDamage * 1.2;
    }

    @Override
    public void entityDeath(LivingEntity entity, EntityDeathEvent event) {
        event.setDroppedExp(event.getDroppedExp() + 8);
    }

    @Override
    public void entitySpawn(LivingEntity entity) {
        entity.customName(getDisplayName());
        entity.setCustomNameVisible(true);
        java.util.Random random = new java.util.Random();
        org.bukkit.inventory.EntityEquipment equipment = entity.getEquipment();
        if (equipment != null) {
            equipment.setHelmet(randomArmorPiece(Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.GOLDEN_HELMET, Material.IRON_HELMET, random));
            equipment.setChestplate(randomArmorPiece(Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.IRON_CHESTPLATE, random));
            equipment.setLeggings(randomArmorPiece(Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.IRON_LEGGINGS, random));
            equipment.setBoots(randomArmorPiece(Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.GOLDEN_BOOTS, Material.IRON_BOOTS, random));
            double bowRoll = random.nextDouble();
            ItemStack bow;
            if (bowRoll < 0.5) {
                bow = new ItemStack(Material.BOW);
            } else {
                bow = ItemManager.ItemTypes.stream().filter(i -> i.getName().equalsIgnoreCase("sonic_bow")).findFirst().map(i -> i.getDefaultStack(1)).orElse(new ItemStack(Material.BOW));
            }
            equipment.setItemInMainHand(bow);
        }
        if (random.nextDouble() < 0.05) {
            entity.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
        }
    }

    private ItemStack randomArmorPiece(Material leather, Material chain, Material gold, Material iron, java.util.Random random) {
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
            item.addEnchantment(org.bukkit.enchantments.Enchantment.PROTECTION, protLevel);
        }
        return item;
    }

    @Override
    public boolean entityArmorEquipped(LivingEntity entity, ItemStack armor) {
        return true;
    }

    @Override
    public void entityArmorAttacked(LivingEntity attacker, Entity victim, EntityDamageByEntityEvent event) {
    }
}
