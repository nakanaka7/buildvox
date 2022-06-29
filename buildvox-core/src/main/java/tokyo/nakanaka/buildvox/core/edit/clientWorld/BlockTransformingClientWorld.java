package tokyo.nakanaka.buildvox.core.edit.clientWorld;

import tokyo.nakanaka.buildvox.core.block.BlockTransformation;
import tokyo.nakanaka.buildvox.core.edit.voxelSpace.VoxelSpace;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

public class BlockTransformingClientWorld implements VoxelSpace<VoxelBlock> {
    private VoxelSpace<VoxelBlock> original;
    private BlockTransformation blockTrans;

    public BlockTransformingClientWorld(VoxelSpace<VoxelBlock> original, BlockTransformation blockTrans) {
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
