package tokyo.nakanaka.buildvox.core.block;

import tokyo.nakanaka.buildvox.core.NamespacedId;

import java.util.Map;

/**
 * A functional interface for block state transformation.
 * Deprecated. BlockState transforming should be done by Block.transformState().
 */
@Deprecated
public interface BlockStateTransformer {
    /**
     * Gets the block state map which represents the state of the transformed block.
     * @param blockId the block id.
     * @param stateMap the block state map which represents the state of the untransformed block.
     * @param blockTrans the block transformation.
     * @return the block state map which represents the state of the transformed block.
     */
    Map<String, String> transform(NamespacedId blockId, Map<String, String> stateMap, BlockTransformation blockTrans);
}
