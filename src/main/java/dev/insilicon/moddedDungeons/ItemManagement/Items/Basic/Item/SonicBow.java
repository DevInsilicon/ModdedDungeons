package dev.insilicon.moddedDungeons.ItemManagement.Items.Basic.Item;

import dev.insilicon.moddedDungeons.ItemManagement.Items.BaseBow;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SonicBow extends BaseBow {
    public SonicBow() {
        super("sonic_bow",
                MiniMessage.miniMessage().deserialize("<aqua>Sonic Bow"),
                List.of("<gray>Arrows are faster and leave a trail of smoke"),
                Material.BOW,
                new NamespacedKey("modded_dungeons", "sonic_bow"),
                0,
                true);
    }

    @Override
    public double playerShotWhileHeld(Player player, Entity victim, EntityDamageByEntityEvent event) {
        return event.getFinalDamage() + 3.0;
    }

    @Override
    public double entityShotWhileHeld(LivingEntity shooter, Entity victim, EntityDamageByEntityEvent event) {
        return event.getFinalDamage() + 3.0;
    }

    @Override
    public void onArrowTick(Arrow arrow) {
        if (arrow.getPersistentDataContainer().has(new NamespacedKey("modded_dungeons", "sonic_bow_arrow"), PersistentDataType.BYTE)) {
            arrow.getWorld().spawnParticle(Particle.LARGE_SMOKE, arrow.getLocation(), 2, 0.05, 0.05, 0.05, 0.01);
        }
    }

    @Override
    public ItemStack getDefaultStack(double amount) {
        ItemStack returnItem = new ItemStack(baseMaterial, (int) amount);
        ItemMeta meta = returnItem.getItemMeta();
        if (meta == null) return super.getDefaultStack(amount);
        meta.displayName(MiniMessage.miniMessage().deserialize("<aqua>Sonic Bow"));
        meta.lore(this.lore.stream().map(l -> MiniMessage.miniMessage().deserialize(l)).toList());
        meta.getPersistentDataContainer().set(dev.insilicon.moddedDungeons.ItemManagement.ItemManager.itemKey, org.bukkit.persistence.PersistentDataType.STRING, this.name);

        // Add enchantment glint without adding any actual enchantment functionality
        meta.addEnchant(Enchantment.INFINITY, 1, true);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS); // Hide the enchantment from the lore

        returnItem.setItemMeta(meta);
        return returnItem;
    }
}
