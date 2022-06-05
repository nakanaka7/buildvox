package tokyo.nakanaka.buildvox.core.world;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.system.Entity;

/**
 * Represents a world. A world is a single space. It does not contain 3 parallel "dimension"
 * spaces(over-world, nether, and end). Each dimension is considered as a single world.
 */
public interface World extends Entity<NamespacedId> {
    @Override
    NamespacedId getId();

    /**
     * Gets a block of the specified position.
     * @param x the x-coordinate of the block.
     * @param y the y-coordinate of the block.
     * @param z the z-coordinate of the block.
     * @return a block of the specified position.
     */
    BlockState getBlock(int x, int y, int z);

    /**
     * Set a block into the specified position.
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     * @param z the z-coordinate.
     * @param block a block.
     * @param physics whether applying physics or not.
     * @throws IllegalArgumentException if the world cannot set the block.
     */
    void setBlock(int x, int y, int z, BlockState block, boolean physics);
}
