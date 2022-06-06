package tokyo.nakanaka.buildvox.core.world;

/*
 * Represents a world. A world is a single space. It does not contain 3 parallel "dimension"
 * spaces(over-world, nether, and end). Each dimension is considered as a single world. A world
 * may have 2 or 3 dimensional biome. Both type are acceptable.
 */
public interface WorldExperimental {
    /**
     * Gets a block of the specified position. Block interface implementation depends on the platform.
     * @param x the x-coordinate of the block.
     * @param y the y-coordinate of the block.
     * @param z the z-coordinate of the block.
     * @return a block of the specified position.
     */
    VoxelBlock getBlock(int x, int y, int z);

    /**
     * Set a block into the specified position. Block interface implementation depends on the platform.
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     * @param z the z-coordinate.
     * @param block a block.
     * @param physics whether applying physics or not.
     * @throws IllegalArgumentException if the block interface is not for the platform.
     */
    void setBlock(int x, int y, int z, VoxelBlock block, boolean physics);

    /**
     * Gets biome dimension. 2 or 3 must be return.
     * @return biome dimension. 2 or 3 must be return.
     */
    int biomeDimension();

    /**
     * Gets a biome 3d of the specified position. Biome 3d interface implementation depends on the platform.
     * @param x the x-coordinate of the 3d biome.
     * @param y the y-coordinate of the 3d biome.
     * @param z the z-coordinate of the 3d biome.
     * @return a biome 3d of the specified position.
     * @throws UnsupportedOperationException if the world does not have 3d biome.
     */
    Biome getBiome3d(int x, int y, int z);

    /**
     * Sets a biome into the specified position. Biome 3d interface implementation depends on the platform.
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     * @param z the z-coordinate.
     * @param biome a 3d biome.
     * @throws UnsupportedOperationException if the world does not have 3d biome.
     * @throws IllegalArgumentException if the biome interface is not for the platform.
     */
    void setBiome3d(int x, int y, int z, Biome biome);

    /**
     * Gets a biome of the specified position. Biome interface implementation depends on the platform.
     * @param x the x-coordinate of the 3d biome.
     * @param z the z-coordinate of the 3d biome.
     * @return a biome of the specified position.
     * @throws UnsupportedOperationException if the world does not have 2d biome.
     */
    Biome getBiome2d(int x, int z);

    /**
     * Sets a biome into the specified position. Biome interface implementation depends on the platform.
     * @param x the x-coordinate.
     * @param z the z-coordinate.
     * @param biome a biome.
     * @throws UnsupportedOperationException if the world does not have 2d biome.
     * @throws IllegalArgumentException if the biome interface is not for the platform.
     */
    void setBiome2d(int x, int z, Biome biome);

    /**
     * Gets entities.
     */
    Entity[] getEntities();

    /**
     * Set the entity into the specified point.
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @param z z-coordinate.
     * @param entity an entity.
     */
    void setEntity(double x, double y, double z, Entity entity);

}
