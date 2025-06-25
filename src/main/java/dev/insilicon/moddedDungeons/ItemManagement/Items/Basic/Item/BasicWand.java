package dev.insilicon.moddedDungeons.ItemManagement.Items.Basic.Item;

import dev.insilicon.moddedDungeons.ItemManagement.ItemManager;
import dev.insilicon.moddedDungeons.ItemManagement.Items.BaseItem;
import dev.insilicon.moddedDungeons.ModdedDungeons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BasicWand extends BaseItem {

    private static final double MAX_DISTANCE = 20.0;
    private static final double FOV_ANGLE = 30.0;
    private static final double DAMAGE_AMOUNT = 5.0;
    private static final int PARTICLE_COUNT = 3;
    private static final double PARTICLE_SPEED = 0.05;

    public BasicWand() {
        super("basic_wand", MiniMessage.miniMessage().deserialize("<gold>Fire Wand"),
                List.of(
                        "<yellow>A magical wand that shoots fire",
                        "<gray>Damage: <red>" + DAMAGE_AMOUNT,
                        "<gray>Range: <blue>" + MAX_DISTANCE + " blocks",
                        "<gray>Right-click to cast a fire spell!"
                ),
                Material.BLAZE_ROD,
                ItemManager.itemKey,
                0,
                false);
    }

    @Override
    public ItemStack getDefaultStack(double amount) {
        ItemStack fireWand = new ItemStack(this.baseMaterial, (int) amount);

        var meta = fireWand.getItemMeta();
        meta.displayName(this.displayName);
        List<Component> loreComponents = new ArrayList<>();
        for (String loreLine : this.lore) {
            loreComponents.add(MiniMessage.miniMessage().deserialize(loreLine));
        }
        meta.lore(loreComponents);
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        meta.getPersistentDataContainer().set(this.getNamespaceKey(), PersistentDataType.STRING, this.name);
        fireWand.setItemMeta(meta);

        return fireWand;
    }

    @Override
    public void interaction(Block block, Player player, ItemStack item) {
        World world = player.getWorld();
        Location startLocation = player.getEyeLocation();
        Vector direction = player.getLocation().getDirection();

        Predicate<Entity> filter = entity -> entity != player;

        world.playSound(startLocation, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f);

        new BukkitRunnable() {
            double distance = 0.5;
            Location particleLocation = startLocation.clone();
            boolean hitSomething = false;

            @Override
            public void run() {
                if (distance > MAX_DISTANCE || hitSomething) {
                    this.cancel();
                    return;
                }

                particleLocation = startLocation.clone().add(direction.clone().multiply(distance));

                Block block = particleLocation.getBlock();
                if (block.getType().isSolid()) {
                    world.spawnParticle(Particle.LAVA, particleLocation, 10, 0.2, 0.2, 0.2, 0.1);
                    world.spawnParticle(Particle.FLAME, particleLocation, 15, 0.3, 0.3, 0.3, 0.05);
                    world.playSound(particleLocation, Sound.BLOCK_FIRE_EXTINGUISH, 0.5f, 1.0f);
                    hitSomething = true;
                    return;
                }

                world.spawnParticle(Particle.FLAME, particleLocation, PARTICLE_COUNT, 0.05, 0.05, 0.05, PARTICLE_SPEED);

                List<Entity> nearbyEntities = getNearbyEntitiesInFOV(particleLocation, direction, 1.5, FOV_ANGLE);
                for (Entity entity : nearbyEntities) {
                    if (entity != player && entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        livingEntity.damage(DAMAGE_AMOUNT, player);
                        livingEntity.setFireTicks(60);

                        world.spawnParticle(Particle.FLAME, livingEntity.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
                        world.spawnParticle(Particle.LAVA, livingEntity.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.1);
                        world.playSound(livingEntity.getLocation(), Sound.ENTITY_GENERIC_BURN, 0.8f, 1.2f);

                        hitSomething = true;
                        break;
                    }
                }

                distance += 0.5;
            }
        }.runTaskTimer(ModdedDungeons.instance, 0L, 1L);

        player.sendMessage(Component.text("§6You cast a fire spell!"));
    }

    private List<Entity> getNearbyEntitiesInFOV(Location location, Vector direction, double radius, double fovDegrees) {
        List<Entity> result = new ArrayList<>();

        for (Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                Vector toEntity = entity.getLocation().toVector().subtract(location.toVector());

                if (toEntity.length() > 0) {
                    toEntity = toEntity.normalize();
                    Vector dirNorm = direction.clone().normalize();

                    double dot = dirNorm.dot(toEntity);
                    double angleDegrees = Math.toDegrees(Math.acos(dot));

                    if (angleDegrees <= fovDegrees / 2) {
                        RayTraceResult rayTrace = location.getWorld().rayTraceBlocks(
                                location,
                                toEntity,
                                toEntity.length(),
                                org.bukkit.FluidCollisionMode.NEVER,
                                true
                        );

                        if (rayTrace == null || rayTrace.getHitEntity() == entity) {
                            result.add(entity);
                        }
                    }
                }
            }
        }

        return result;
    }
}
