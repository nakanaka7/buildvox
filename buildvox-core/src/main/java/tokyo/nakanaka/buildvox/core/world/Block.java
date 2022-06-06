package tokyo.nakanaka.buildvox.core.world;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.blockStateTransformer.BlockTransformation;
import tokyo.nakanaka.buildvox.core.system.Entity;

/**
 * Represents a block.
 * @param <S> the state object.
 */
public interface Block<S> extends Entity<NamespacedId> {
    /**
     * Transforms the state.
     * @param state the original state.
     * @param trans the block transformation.
     * @return the transformed state.
     */
    S transformState(S state, BlockTransformation trans);

}
