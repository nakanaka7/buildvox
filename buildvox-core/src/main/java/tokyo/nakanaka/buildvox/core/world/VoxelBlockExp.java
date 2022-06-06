package tokyo.nakanaka.buildvox.core.world;

import tokyo.nakanaka.buildvox.core.block.Block;
import tokyo.nakanaka.buildvox.core.block.BlockTransformation;

/**
 * Experimental.
 * Represents a voxel block.
 * @param <S> the state object.
 * @param <E> the entity object.
 */
public class VoxelBlockExp<S extends Block.State, E extends Block.Entity> {
    private final Block<S, E> block;
    private final S state;
    private final E entity;

    /**
     * Creates a voxel block.
     * @param block the block.
     * @param state the state.
     */
    public VoxelBlockExp(Block<S, E> block, S state) {
        this(block, state, null);
    }

    /**
     * Creates a voxel block.
     * @param block the block.
     * @param state the state.
     * @param entity the entity.
     */
    public VoxelBlockExp(Block<S, E> block, S state, E entity) {
        this.block = block;
        this.state = state;
        this.entity = entity;
    }

    /**
     * Transforms this voxel block.
     * @param trans the block transformation.
     * @return the transformed voxel block. The block will remain.
     */
    public VoxelBlockExp<S, E> transform(BlockTransformation trans) {
        S newState = block.transformState(state, trans);
        return new VoxelBlockExp<>(block, newState);
    }

    /**
     * Gets the block.
     * @return the block.
     */
    public Block<S, E> getBlock() {
        return block;
    }

    /**
     * Gets the state.
     * @return the state.
     */
    public S getState() {
        return state;
    }

    /**
     * Gets the entity.
     * @return the entity.
     */
    public E getEntity() {
        return entity;
    }

}
