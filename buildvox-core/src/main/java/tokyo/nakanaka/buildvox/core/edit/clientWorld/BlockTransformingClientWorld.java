package tokyo.nakanaka.buildvox.core.edit.clientWorld;

import tokyo.nakanaka.buildvox.core.block.BlockTransformation;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

public class BlockTransformingClientWorld extends ClientWorld {
    private final BlockTransformation blockTrans;
    private final ClientWorld clientWorld;

    public BlockTransformingClientWorld(BlockTransformation blockTrans, ClientWorld clientWorld) {
        super(clientWorld.original, clientWorld.physics);
        this.blockTrans = blockTrans;
        this.clientWorld = clientWorld;
    }

    @Override
    public void setBlock(Vector3i pos, VoxelBlock block) {
        clientWorld.setBlock(pos, block.transform(blockTrans));
    }

}
