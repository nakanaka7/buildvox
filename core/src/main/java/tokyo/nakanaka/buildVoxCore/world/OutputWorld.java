package tokyo.nakanaka.buildVoxCore.world;

public interface OutputWorld {
    /**
     * Set a block into the specified position.
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     * @param z the z-coordinate.
     * @param block a block.
     * @param physics whether applying physics or not.
     * @throws IllegalArgumentException if the world cannot set the block.
     */
    void setBlock(int x, int y, int z, Block block, boolean physics);

}
