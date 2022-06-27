package tokyo.nakanaka.buildvox.core.voxelSpace;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

import java.util.function.Predicate;

/**
 * The block space which filters the original block when getting it.
 * @param <B> block type
 */
public class GettingFilteringVoxelSpace<B> implements VoxelSpace<B> {
    private VoxelSpace<B> original;
    private Predicate<B> predicate;

    /**
     * Constructs the space by the original block space and predicate of getBlock() method.
     * @param original the original world
     * @param predicate the predicate function for the get blocks in the original space.
     */
    public GettingFilteringVoxelSpace(VoxelSpace<B> original, Predicate<B> predicate) {
        this.original = original;
        this.predicate = predicate;
    }

    @Override
    public B getBlock(Vector3i pos) {
        B b = original.getBlock(pos);
        if(b == null)return null;
        if(predicate.test(b)) return b;
        return null;
    }

    @Override
    public void setBlock(Vector3i pos, B block) {
        original.setBlock(pos, block);
    }

}
