package tokyo.nakanaka.buildvox.core.edit.voxelSpace;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

import java.util.function.Predicate;

/**
 * The voxel space which filters the original block when setting it.
 * @param <B> block type
 */
public class SettingFilteringVoxelSpace <B> implements VoxelSpace<B> {
    private final VoxelSpace<B> original;
    private final Predicate<B> set;

    /**
     * Constructs the space by the original voxel space and predicate of setBlock() method.
     * @param original the original world
     * @param set the predicate function for the set blocks in the original space.
     */
    public SettingFilteringVoxelSpace(VoxelSpace<B> original, Predicate<B> set) {
        this.original = original;
        this.set = set;
    }

    @Override
    public B getBlock(Vector3i pos) {
        return original.getBlock(pos);
    }

    @Override
    public void setBlock(Vector3i pos, B block) {
        if(set.test(block)) {
            original.setBlock(pos, block);
        }
    }

}
