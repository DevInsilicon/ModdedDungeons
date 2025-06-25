package dev.insilicon.moddedDungeons.Dungeons.Levels.PreDungeon;

import dev.insilicon.moddedDungeons.ModdedDungeons;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class PredungeonManager implements Listener {
    private final Location portalStart;
    private final Location portalEnd;
    private final Location portalDestination;
    private final World world;

    public PredungeonManager() {
        FileConfiguration config = ModdedDungeons.instance.getConfig();
        int x1 = config.getInt("dungeon_settings.predungeon.portal1.x");
        int y1 = config.getInt("dungeon_settings.predungeon.portal1.y");
        int z1 = config.getInt("dungeon_settings.predungeon.portal1.z");
        int x2 = config.getInt("dungeon_settings.predungeon.portal1.mx");
        int y2 = config.getInt("dungeon_settings.predungeon.portal1.my");
        int z2 = config.getInt("dungeon_settings.predungeon.portal1.mz");
        int destX = config.getInt("dungeon_settings.predungeon.portal1_destination.x");
        int destY = config.getInt("dungeon_settings.predungeon.portal1_destination.y");
        int destZ = config.getInt("dungeon_settings.predungeon.portal1_destination.z");
        this.world = Bukkit.getWorlds().get(0);
        this.portalStart = new Location(world, x1, y1, z1);
        this.portalEnd = new Location(world, x2, y2, z2);
        this.portalDestination = new Location(world, destX, destY, destZ);
        portalParticlesSchedule();
        Bukkit.getPluginManager().registerEvents(this, ModdedDungeons.instance);
    }

    public void portalParticlesSchedule() {
        Bukkit.getScheduler().runTaskTimer(ModdedDungeons.instance, () -> {
            spawnPortalParticlesBetween(portalStart, portalEnd);
        }, 0L, 10L);
    }

    private void spawnPortalParticlesBetween(Location start, Location end) {
        int steps = 20;
        double dx = (end.getX() - start.getX()) / steps;
        double dy = (end.getY() - start.getY()) / steps;
        double dz = (end.getZ() - start.getZ()) / steps;
        for (int i = 0; i <= steps; i++) {
            double x = start.getX() + dx * i;
            double y = start.getY() + dy * i;
            double z = start.getZ() + dz * i;
            world.spawnParticle(Particle.PORTAL, x, y, z, 5, 0.2, 0.2, 0.2, 0.01);
        }
    }

    public void teleportPlayerToPortalDestination(Player player) {
        player.teleport(portalDestination);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().equals(world) && isNearPortalLine(player.getLocation())) {
            teleportPlayerToPortalDestination(player);
        }
    }

    private boolean isNearPortalLine(Location loc) {
        int steps = 20;
        double dx = (portalEnd.getX() - portalStart.getX()) / steps;
        double dy = (portalEnd.getY() - portalStart.getY()) / steps;
        double dz = (portalEnd.getZ() - portalStart.getZ()) / steps;
        for (int i = 0; i <= steps; i++) {
            double x = portalStart.getX() + dx * i;
            double y = portalStart.getY() + dy * i;
            double z = portalStart.getZ() + dz * i;
            if (loc.getWorld().equals(world) && loc.distance(new Location(world, x, y, z)) <= 1.0) {
                return true;
            }
        }
        return false;
    }
}
