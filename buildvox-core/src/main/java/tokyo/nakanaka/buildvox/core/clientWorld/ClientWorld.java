package tokyo.nakanaka.buildvox.core.clientWorld;

import tokyo.nakanaka.buildvox.core.VoxelSpace;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;
import tokyo.nakanaka.buildvox.core.world.World;

/**
 * A wrapped class of World. World is unique object (it has getId() method), whereas ClientWorld is not unique one.
 * The relation between World and ClientWorld is similar to File and stream object(FileWriter, BufferedWriter...).
 */
public class ClientWorld implements VoxelSpace<VoxelBlock> {
    protected final World original;
    protected boolean physics;

    /**
     * Constructs a new instance with an original World. The block setting physics will be set "false".
     * @param original the original world.
     */
    public ClientWorld(World original) {
        this.original = original;
    }

    /**
     * Constructs a new instance with an original World.
     * @param original the original world.
     */
    public ClientWorld(World original, boolean physics) {
        this.original = original;
        this.physics = physics;
    }

    /**
     * Get a block at the specified position
     * @param pos the position of the block
     * @return a block
     */
    public VoxelBlock getBlock(Vector3i pos) {
        return original.getBlock(pos.x(), pos.y(), pos.z());
    }

    /**
     * Set a block with fixed block setting physics.
     * @param pos the position to set a block.
     * @param block the block to set.
     */
    public void setBlock(Vector3i pos, VoxelBlock block) {
        original.setBlock(pos.x(), pos.y(), pos.z(), block, physics);
    }

}
