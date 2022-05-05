package tokyo.nakanaka.buildvox.core.blockSpace;

import tokyo.nakanaka.buildvox.core.blockStateTransformer.BlockStateTransformer;
import tokyo.nakanaka.buildvox.core.blockStateTransformer.BlockTransformation;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.Block;

import java.util.Map;

public class BlockStateTransformingBlockSpace3 implements BlockSpace3<Block>{
    private BlockSpace3<Block> original;
    private BlockStateTransformer blockStateTransformer;
    private BlockTransformation blockTrans;

    public BlockStateTransformingBlockSpace3(BlockSpace3<Block> original, BlockStateTransformer blockStateTransformer, BlockTransformation blockTrans) {
        this.original = original;
        this.blockStateTransformer = blockStateTransformer;
        this.blockTrans = blockTrans;
    }

    @Override
    public Block getBlock(Vector3i pos) {
        return original.getBlock(pos);
    }

    @Override
    public void setBlock(Vector3i pos, Block block) {
        Map<String, String> transStateMap = blockStateTransformer.transform(block.getId(), block.getStateMap(), blockTrans);
        Block transBlock = block.withStateMap(transStateMap);
        original.setBlock(pos, transBlock);
    }

}
