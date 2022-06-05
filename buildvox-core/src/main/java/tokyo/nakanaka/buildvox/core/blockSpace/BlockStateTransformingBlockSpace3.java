package tokyo.nakanaka.buildvox.core.blockSpace;

import tokyo.nakanaka.buildvox.core.blockStateTransformer.BlockStateTransformer;
import tokyo.nakanaka.buildvox.core.blockStateTransformer.BlockTransformation;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.BlockState;

import java.util.Map;

public class BlockStateTransformingBlockSpace3 implements BlockSpace3<BlockState>{
    private BlockSpace3<BlockState> original;
    private BlockStateTransformer blockStateTransformer;
    private BlockTransformation blockTrans;

    public BlockStateTransformingBlockSpace3(BlockSpace3<BlockState> original, BlockStateTransformer blockStateTransformer, BlockTransformation blockTrans) {
        this.original = original;
        this.blockStateTransformer = blockStateTransformer;
        this.blockTrans = blockTrans;
    }

    @Override
    public BlockState getBlock(Vector3i pos) {
        return original.getBlock(pos);
    }

    @Override
    public void setBlock(Vector3i pos, BlockState block) {
        Map<String, String> transStateMap = blockStateTransformer.transform(block.getId(), block.getStateMap(), blockTrans);
        BlockState transBlock = block.withStateMap(transStateMap);
        original.setBlock(pos, transBlock);
    }

}
