package dev.insilicon.moddedDungeons.ItemManagement;

import dev.insilicon.moddedDungeons.ItemManagement.Items.BaseArmor;
import dev.insilicon.moddedDungeons.ItemManagement.Items.BaseItem;
import dev.insilicon.moddedDungeons.ItemManagement.Items.Basic.Item.BasicWand;
import dev.insilicon.moddedDungeons.ItemManagement.Items.Basic.Item.SonicBow;
import dev.insilicon.moddedDungeons.ModdedDungeons;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ItemManager implements Listener {
    public static NamespacedKey itemKey = new NamespacedKey("modded_dungeons", "item_key");
    public static NamespacedKey fromDungeonKey = new NamespacedKey("modded_dungeons", "from_dungeon");
    public static List<BaseItem> ItemTypes = new ArrayList<>();
    public static List<BaseArmor> PrecompiledArmorTypes = new ArrayList<>();

    public ItemManager() {
        registry();
        startArrowTickTask();
    }

    public void registry() {
        // Clear lists to avoid duplicates if registry() is called multiple times
        ItemTypes.clear();
        PrecompiledArmorTypes.clear();

        // Basic Tier
        ItemTypes.add(new BasicWand());
        ItemTypes.add(new SonicBow());

        // Tier 1

        // precompile armor types
        for (BaseItem item : ItemTypes) {
            if (item instanceof BaseArmor) {
                PrecompiledArmorTypes.add((BaseArmor) item);
            }
        }
    }

    public void startArrowTickTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntitiesByClass(Arrow.class)) {
                        Arrow arrow = (Arrow) entity;
                        if (arrow.getPersistentDataContainer().has(new NamespacedKey("modded_dungeons", "sonic_bow_arrow"), PersistentDataType.BYTE)) {
                            BaseItem bowType = ItemTypes.stream().filter(i -> i.getName().equalsIgnoreCase("sonic_bow")).findFirst().orElse(null);
                            if (bowType instanceof dev.insilicon.moddedDungeons.ItemManagement.Items.BaseBow baseBow) {
                                baseBow.onArrowTick(arrow);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(ModdedDungeons.instance, 1, 1);
    }


    // Events
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        BaseItem itemType = getItemType(event.getItem());
        if (itemType == null) return;

        itemType.interaction(event.getClickedBlock(), event.getPlayer(), event.getItem());
    }

    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent event) {
        playerDamagedWhileHeld(event);
        BaseArmorUserDamaged(event);
        BaseArmorUserAttacked(event);
        BaseArmorUserEquipped(event);
        playerAttackedWhileHeld(event);
    }


    // TODO: playerAttackedWhileHeld : BaseItem
    public void playerAttackedWhileHeld(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem == null) return;
        
        BaseItem itemType = getItemType(heldItem);
        if (itemType == null) return;
        
        double originalDamage = event.getDamage();
        double modifiedDamage = itemType.playerAttackedWhileHeld(player, event.getEntity(), event);
        event.setDamage(modifiedDamage);
    }

    // TODO: playerDamagedWhileHeld : BaseItem
    public void playerDamagedWhileHeld(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem == null) return;
        
        BaseItem itemType = getItemType(heldItem);
        if (itemType == null) return;
        
        double originalDamage = event.getDamage();
        double modifiedDamage = itemType.playerDamagedWhileHeld(player, event);
        event.setDamage(modifiedDamage);
    }

    // TODO: pickup : BaseItem
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        ItemStack item = event.getItem().getItemStack();
        BaseItem itemType = getItemType(item);
        if (itemType == null) return;

        itemType.pickup((Player) event.getEntity(), item);
    }

    // TODO: drop : BaseItem
    @EventHandler
    public void onItemDrop(EntityDropItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        ItemStack item = event.getItemDrop().getItemStack();
        BaseItem itemType = getItemType(item);
        if (itemType == null) return;

        boolean isBecauseDeath = event.getEntity().isDead();
        itemType.drop((Player) event.getEntity(), item, isBecauseDeath);
    }

    // TODO: UserDamaged : BaseArmor
    public void BaseArmorUserDamaged(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        for (ItemStack armorPiece : player.getInventory().getArmorContents()) {
            if (armorPiece == null) continue;

            BaseItem itemType = getItemType(armorPiece);
            if (itemType == null) continue;

            if (itemType instanceof BaseArmor baseArmor) {
                double originalDamage = event.getDamage();
                double reducedDamage = baseArmor.UserDamaged(player, originalDamage);
                event.setDamage(reducedDamage);
            }
        }
    }

    // TODO: userAttacked : BaseArmor
    public void BaseArmorUserAttacked(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        for (ItemStack armorPiece : player.getInventory().getArmorContents()) {
            if (armorPiece == null) continue;

            BaseItem itemType = getItemType(armorPiece);
            if (itemType == null) continue;

            if (itemType instanceof BaseArmor baseArmor) {
                baseArmor.userAttacked(player, event.getEntity(), event);
            }
        }
    }

    // TODO: userEquipped : BaseArmor
    public void BaseArmorUserEquipped(EntityDamageByEntityEvent event) {
        Player player = null;
        if (event.getEntity() instanceof Player) {
            player = (Player) event.getEntity();
        } else if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
        }

        if (player == null) return;

        for (ItemStack armorPiece : player.getInventory().getArmorContents()) {
            if (armorPiece == null) continue;

            BaseItem itemType = getItemType(armorPiece);
            if (itemType == null) continue;

            if (itemType instanceof BaseArmor baseArmor) {
                baseArmor.userEquipped(player, armorPiece);
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;

        // Check if the shooter is a Player or any LivingEntity
        if (arrow.getShooter() instanceof org.bukkit.entity.LivingEntity shooter) {
            ItemStack bow = null;

            // Get the bow from the shooter's hand
            if (shooter instanceof Player player) {
                bow = player.getInventory().getItemInMainHand();
            } else if (shooter.getEquipment() != null) {
                bow = shooter.getEquipment().getItemInMainHand();
            }

            // Check if the bow is a sonic bow
            if (bow != null) {
                BaseItem itemType = getItemType(bow);
                if (itemType != null && itemType.getName().equalsIgnoreCase("sonic_bow")) {
                    arrow.getPersistentDataContainer().set(new NamespacedKey("modded_dungeons", "sonic_bow_arrow"), PersistentDataType.BYTE, (byte)1);
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!arrow.getPersistentDataContainer().has(new NamespacedKey("modded_dungeons", "sonic_bow_arrow"), PersistentDataType.BYTE)) return;
        arrow.getWorld().spawnParticle(Particle.LARGE_SMOKE, arrow.getLocation(), 8, 0.1, 0.1, 0.1, 0.01);
    }

    public static BaseItem getItemType(ItemStack item) {
        if (!item.getPersistentDataContainer().has(itemKey, PersistentDataType.STRING)) return null;

        String itemName = item.getPersistentDataContainer().get(itemKey, PersistentDataType.STRING);
        if (itemName == null) return null;

        return ItemTypes.stream().filter(i -> i.getName().equalsIgnoreCase(itemName)).findFirst().orElse(null);
    }

    public static boolean giveItem(Player player, BaseItem itemType, int amount) {
        if (player == null || itemType == null || amount <= 0) return false;
        ItemStack itemStack = itemType.getDefaultStack(amount);
        if (itemStack == null) return false;
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(itemStack);
            return true;
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
            return true;
        }
    }
}

