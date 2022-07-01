package tokyo.nakanaka.buildvox.core.clientWorld;

import tokyo.nakanaka.buildvox.core.block.BlockTransformation;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

/** The client world which applies block transformation when setting blocks */
public class BlockTransformingClientWorld extends ClientWorld {
    private final BlockTransformation blockTrans;
    private final ClientWorld clientWorld;

    /**
     * Creates a new instance.
     * @param blockTrans the block transformation.
     * @param clientWorld the original client world.
     */
    public BlockTransformingClientWorld(BlockTransformation blockTrans, ClientWorld clientWorld) {
        super(clientWorld.getWorld(), clientWorld.getPhysics());
        this.blockTrans = blockTrans;
        this.clientWorld = clientWorld;
    }

    @Override
    public void setBlock(Vector3i pos, VoxelBlock block) {
        clientWorld.setBlock(pos, block.transform(blockTrans));
    }

}
