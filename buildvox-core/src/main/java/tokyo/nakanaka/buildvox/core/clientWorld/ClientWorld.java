package tokyo.nakanaka.buildvox.core.clientWorld;

import tokyo.nakanaka.buildvox.core.VoxelSpace;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;
import tokyo.nakanaka.buildvox.core.world.World;

/**
 * A wrapped class of World. World is unique object (it has getId() method), whereas ClientWorld is not unique one.
 * The relation between World and ClientWorld is similar to File and Writer.
 */
public class ClientWorld implements VoxelSpace<VoxelBlock> {
    protected final World world;
    protected boolean physics;

    /**
     * Constructs a new instance with an original World. The block setting physics will be set "false".
     * @param world the original world.
     */
    public ClientWorld(World world) {
        this.world = world;
    }

    /**
     * Constructs a new instance with an original World.
     * @param world the original world.
     */
    public ClientWorld(World world, boolean physics) {
        this.world = world;
        this.physics = physics;
    }

    /** Gets the world. */
    public World getWorld() {
        return world;
    }

    /** Gets the physics */
    public boolean getPhysics() {
        return physics;
    }

    /**
     * Get a block at the specified position
     * @param pos the position of the block
     * @return a block
     */
    public VoxelBlock getBlock(Vector3i pos) {
        return world.getBlock(pos.x(), pos.y(), pos.z());
    }

    /**
     * Set a block with fixed block setting physics.
     * @param pos the position to set a block.
     * @param block the block to set.
     */
    public void setBlock(Vector3i pos, VoxelBlock block) {
        world.setBlock(pos.x(), pos.y(), pos.z(), block, physics);
    }

}
