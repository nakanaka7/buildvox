package tokyo.nakanaka.buildvox.core.particleGui;

import tokyo.nakanaka.buildvox.core.World;

/**
 * An object which spawn particles.
 */
public interface ColoredParticleSpawner {
    /**
     * Spawn colored particle at the given position
     * @param color the color of the particle
     * @param world world of the position
     * @param x x-coordinate of the position
     * @param y y-coordinate of the position
     * @param z z-coordinate of the position
     */
    void spawnParticle(Color color, World world, double x, double y, double z);
}
