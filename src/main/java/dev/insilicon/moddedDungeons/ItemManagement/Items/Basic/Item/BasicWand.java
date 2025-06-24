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
    private static final double FOV_ANGLE = 30.0; // in degrees
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

        // Create a predicate to filter out the caster
        Predicate<Entity> filter = entity -> entity != player;

        // Play sound effect at cast location
        world.playSound(startLocation, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f);

        // Create particle effect animation
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

                // Move the particle location forward
                particleLocation = startLocation.clone().add(direction.clone().multiply(distance));

                // Check for blocks in the way
                Block block = particleLocation.getBlock();
                if (block.getType().isSolid()) {
                    world.spawnParticle(Particle.LAVA, particleLocation, 10, 0.2, 0.2, 0.2, 0.1);
                    world.spawnParticle(Particle.FLAME, particleLocation, 15, 0.3, 0.3, 0.3, 0.05);
                    world.playSound(particleLocation, Sound.BLOCK_FIRE_EXTINGUISH, 0.5f, 1.0f);
                    hitSomething = true;
                    return;
                }

                // Create fire particles
                world.spawnParticle(Particle.FLAME, particleLocation, PARTICLE_COUNT, 0.05, 0.05, 0.05, PARTICLE_SPEED);

                // Check for entities in path within FOV
                List<Entity> nearbyEntities = getNearbyEntitiesInFOV(particleLocation, direction, 1.5, FOV_ANGLE);
                for (Entity entity : nearbyEntities) {
                    if (entity != player && entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        livingEntity.damage(DAMAGE_AMOUNT, player);
                        livingEntity.setFireTicks(60); // Set entity on fire for 3 seconds

                        // Explosion particle effect on hit
                        world.spawnParticle(Particle.FLAME, livingEntity.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
                        world.spawnParticle(Particle.LAVA, livingEntity.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.1);
                        world.playSound(livingEntity.getLocation(), Sound.ENTITY_GENERIC_BURN, 0.8f, 1.2f);

                        hitSomething = true;
                        break;
                    }
                }

                distance += 0.5; // Increment distance for next step
            }
        }.runTaskTimer(ModdedDungeons.instance, 0L, 1L);

        // Show message to player
        player.sendMessage(Component.text("§6You cast a fire spell!"));
    }

    /**
     * Gets entities within a field of view from a location
     * @param location Center location
     * @param direction Direction vector
     * @param radius Radius to check
     * @param fovDegrees Field of view in degrees
     * @return List of entities within FOV
     */
    private List<Entity> getNearbyEntitiesInFOV(Location location, Vector direction, double radius, double fovDegrees) {
        List<Entity> result = new ArrayList<>();

        // Get all entities within radius
        for (Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                // Calculate if entity is within FOV
                Vector toEntity = entity.getLocation().toVector().subtract(location.toVector());

                if (toEntity.length() > 0) {
                    // Normalize vectors
                    toEntity = toEntity.normalize();
                    Vector dirNorm = direction.clone().normalize();

                    // Calculate angle between vectors
                    double dot = dirNorm.dot(toEntity);
                    double angleDegrees = Math.toDegrees(Math.acos(dot));

                    // Check if entity is in FOV
                    if (angleDegrees <= fovDegrees / 2) {
                        // Check if there's a clear line of sight (no blocks in between)
                        RayTraceResult rayTrace = location.getWorld().rayTraceBlocks(
                                location,
                                toEntity,
                                toEntity.length(),
                                org.bukkit.FluidCollisionMode.NEVER,
                                true
                        );

                        // Add entity if no blocks were hit
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
