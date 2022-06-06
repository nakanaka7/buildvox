package tokyo.nakanaka.buildvox.core.block;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.system.Entity;

/**
 * Represents a block.
 * @param <S> the state object.
 */
public interface Block<S extends Block.State, E extends Block.Entity> extends Entity<NamespacedId> {
    /**
     * Represents the block state.
     */
    interface State {
    }

    /**
     * Represents the block entity.
     */
    interface Entity {
    }

    /**
     * Transforms the state.
     * @param state the original state.
     * @param trans the block transformation.
     * @return the transformed state.
     */
    S transformState(S state, BlockTransformation trans);

}
