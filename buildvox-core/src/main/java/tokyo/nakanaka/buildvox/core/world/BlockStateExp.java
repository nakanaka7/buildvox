package tokyo.nakanaka.buildvox.core.world;

import tokyo.nakanaka.buildvox.core.blockStateTransformer.BlockTransformation;

/**
 * Experimental.
 * Represents a block state.
 * @param <S> the state object.
 */
public class BlockStateExp<S> {
    private final Block<S> block;
    private final S state;

    /**
     * Creates a block state.
     * @param block the block.
     * @param state the state.
     */
    public BlockStateExp(Block<S> block, S state) {
        this.block = block;
        this.state = state;
    }

    /**
     * Transforms this block state.
     * @param trans the block transformation.
     * @return the transformed block state. The block will remain.
     */
    public BlockStateExp<S> transform(BlockTransformation trans) {
        S newState = block.transform(state, trans);
        return new BlockStateExp<>(block, newState);
    }

    /**
     * Gets the block.
     * @return the block.
     */
    public Block<S> getBlock() {
        return block;
    }

    /**
     * Gets the state.
     * @return the state.
     */
    public S getState() {
        return state;
    }

}
