package tokyo.nakanaka.buildvox.core.edit.clientWorld;

import tokyo.nakanaka.buildvox.core.block.BlockTransformation;
import tokyo.nakanaka.buildvox.core.edit.voxelSpace.VoxelSpace;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

public class BlockTransformingClientWorld implements VoxelSpace<VoxelBlock> {
    private ClientWorld clientWorld;
    private BlockTransformation blockTrans;

    public BlockTransformingClientWorld(BlockTransformation blockTrans, ClientWorld clientWorld) {
        this.clientWorld = clientWorld;
        this.blockTrans = blockTrans;
    }

    @Override
    public VoxelBlock getBlock(Vector3i pos) {
        return clientWorld.getBlock(pos);
    }

    @Override
    public void setBlock(Vector3i pos, VoxelBlock block) {
        clientWorld.setBlock(pos, block.transform(blockTrans));
    }

}
