package dev.insilicon.moddedDungeons.ItemManagement.Items.Basic.Item;

import dev.insilicon.moddedDungeons.ItemManagement.ItemManager;
import dev.insilicon.moddedDungeons.ItemManagement.Items.BaseItem;
import dev.insilicon.moddedDungeons.ModdedDungeons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;

public class BasicSpear extends BaseItem
{
    private static HashMap<Player, Long> lastClickTime = new HashMap<>();

    public BasicSpear() {
        super("basic_spear", MiniMessage.miniMessage().deserialize(""),
                List.of(
                        "<gray>Just a basic spear. Does 6 Damage.",
                        "<gray>Get an extra 2 blocks of reach when clicked.",
                        "<gray>Each piece of armor decreases damage by 10%."
                ),
                Material.STICK,
                ItemManager.itemKey,
                -1,
                false);
    }

    @Override
    public ItemStack getDefaultStack(double amount) {

        ItemStack returnItem = new ItemStack(baseMaterial, (int) amount);

        ItemMeta meta = returnItem.getItemMeta();
        if (meta == null) return super.getDefaultStack(amount);

        meta.displayName(MiniMessage.miniMessage().deserialize("<red>Basic Spear"));
        for (String lore : this.lore) {
            meta.lore(List.of(MiniMessage.miniMessage().deserialize(lore)));
        }

        meta.getPersistentDataContainer().set(ItemManager.itemKey, PersistentDataType.STRING, this.name);
        returnItem.setItemMeta(meta);

        return returnItem;
    }

    @Override
    public double playerAttackedWhileHeld(Player player, Entity victim, EntityDamageByEntityEvent event) {
        if (!(victim instanceof Player)) return event.getFinalDamage();
        Player vicPlayer = (Player) victim;

        if (ModdedDungeons.dungeonManager.isPlayerInDungeon(player)) {
            double baseDamage = 6.0;
            double armorReduction = 0.1 * vicPlayer.getInventory().getArmorContents().length;
            double finalDamage = baseDamage * (1 - armorReduction);
            if (finalDamage < 0) finalDamage = 0;
            return finalDamage;
        } else {
            return event.getFinalDamage();
        }
    }

    @Override
    public void interaction(Block block, Player player, ItemStack item) {
        // Send raycast 6 blocks forward from the player
        Location origin = player.getEyeLocation();
        Vector direction = origin.getDirection().normalize();
        RayTraceResult result = player.getWorld().rayTraceEntities(origin,direction,6);

        if (result != null && result.getHitEntity() != null) {


            if (!(result.getHitEntity() instanceof LivingEntity)) return;
            LivingEntity hitEntity = (LivingEntity) result.getHitEntity();
            if (hitEntity instanceof HumanEntity) {
                HumanEntity hitHuman = (HumanEntity) hitEntity;

                double damage = 6.0;
                for (ItemStack armor : hitHuman.getInventory().getArmorContents()) {
                    damage = damage * 0.9;
                }

                if (damage < 0) damage = 0;
                hitEntity.damage(damage, player);

            } else {
                hitEntity.damage(6, player);
            }

        } else {

        }
    }

    @Override
    public boolean isCanBeUsedOutsideDungeon() {
        return false;
    }
}
