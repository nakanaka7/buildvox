package tokyo.nakanaka.buildvox.core.blockSpace;

import tokyo.nakanaka.buildvox.core.block.BlockTransformation;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

public class BlockStateTransformingBlockSpace3 implements BlockSpace3<VoxelBlock>{
    private BlockSpace3<VoxelBlock> original;
    private BlockTransformation blockTrans;

    public BlockStateTransformingBlockSpace3(BlockSpace3<VoxelBlock> original, BlockTransformation blockTrans) {
        this.original = original;
        this.blockTrans = blockTrans;
    }

    @Override
    public VoxelBlock getBlock(Vector3i pos) {
        return original.getBlock(pos);
    }

    @Override
    public void setBlock(Vector3i pos, VoxelBlock block) {
        original.setBlock(pos, block.transform(blockTrans));
    }

}
