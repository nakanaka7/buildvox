package tokyo.nakanaka.buildvox.core.edit.voxelSpace;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

import java.util.function.Predicate;

/**
 * The voxel space which filters the original block when getting it.
 * @param <B> block type
 */
public class GettingFilteringVoxelSpace<B> implements VoxelSpace<B> {
    private final VoxelSpace<B> original;
    private final Predicate<B> get;

    /**
     * Constructs the space by the original voxel space and predicate of getBlock() method.
     * @param original the original world
     * @param get the predicate function for the get blocks in the original space.
     */
    public GettingFilteringVoxelSpace(VoxelSpace<B> original, Predicate<B> get) {
        this.original = original;
        this.get = get;
    }

    @Override
    public B getBlock(Vector3i pos) {
        B b = original.getBlock(pos);
        if(b == null)return null;
        if(get.test(b)) return b;
        return null;
    }

    @Override
    public void setBlock(Vector3i pos, B block) {
        original.setBlock(pos, block);
    }

}
