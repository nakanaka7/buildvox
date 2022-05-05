package tokyo.nakanaka.buildvox.core;

import tokyo.nakanaka.buildvox.core.particleGui.Color;
import tokyo.nakanaka.buildvox.core.particleGui.ColoredParticleSpawner;
import tokyo.nakanaka.buildvox.core.world.World;

/**
 * Represents a player entity.
 */
public interface PlayerEntity extends ColoredParticleSpawner {
    /**
     * Give her a pos marker.
     */
    void givePosMarker();

    /**
     * Spawn particles to the player. Spawn colored redstone dust as possible. This particle
     * may be seen only to the player.
     * @param color a color of particle.
     * @param world a world to spawn particle.
     * @param x the x-coordinate of the particle.
     * @param y the y-coordinate of the particle.
     * @param z the z-coordinate of the particle.
     */
    @Override
    void spawnParticle(Color color, World world, double x, double y, double z);

}
