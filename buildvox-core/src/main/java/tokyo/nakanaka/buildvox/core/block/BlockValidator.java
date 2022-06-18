package tokyo.nakanaka.buildvox.core.block;

import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

/**
 * A functional interface to validate the given block is settable into the world of the platform.
 */
public interface BlockValidator {
    /**
     * Checks the given block is settable into the world of the platform.
     * @param block a block
     * @return true if the given block is settable into the world of the platform, otherwise false.
     */
    boolean validate(VoxelBlock block);
}
