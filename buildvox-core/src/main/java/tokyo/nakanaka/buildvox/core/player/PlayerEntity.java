package tokyo.nakanaka.buildvox.core.player;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.particleGui.Color;
import tokyo.nakanaka.buildvox.core.particleGui.ColoredParticleSpawner;
import tokyo.nakanaka.buildvox.core.World;

import java.util.UUID;

/**
 * Represents a player entity.
 */
public interface PlayerEntity extends ColoredParticleSpawner {
    /**
     * Gets the id of this player entity.
     * @return the id of this player entity.
     */
    UUID getId();

    void println(String msg);

    /**
     * Gets the block position of this player
     * @return the block position of this player
     */
    Vector3i getBlockPos();

    /**
     * Gets the id of the world which this player exists in
     * @return the id of the world which this player exists in
     */
    NamespacedId getWorldId();

    /**
     * Give her a pos marker.
     */
    void givePosMarker();

    /**
     * Give her a brush.
     */
    default void giveBrush() {
    }

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
