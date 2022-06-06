package tokyo.nakanaka.buildvox.core.block;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.system.Entity;

/**
 * Represents a block.
 * @param <S> the state object.
 * @param <E> the entity object.
 */
public interface Block<S extends Block.State, E extends Block.Entity> extends Entity<NamespacedId> {
    /** * Represents a block state. */
    interface State {
    }

    /** Represents a block entity. */
    interface Entity {
    }

    /** Empty block entity */
    class EmptyEntity implements Block.Entity {
    }

    /**
     * Transforms the state.
     * @param state the original state.
     * @param trans the block transformation.
     * @return the transformed state.
     */
    S transformState(S state, BlockTransformation trans);

}
