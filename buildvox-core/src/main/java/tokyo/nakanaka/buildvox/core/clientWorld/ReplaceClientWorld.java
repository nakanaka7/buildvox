package tokyo.nakanaka.buildvox.core.clientWorld;

import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

/* experimental */
public class ReplaceClientWorld extends ClientWorld {
    private final ClientWorld delegateWorld;
    private final VoxelBlock[] filters;

    public ReplaceClientWorld(ClientWorld clientWorld, VoxelBlock... filters) {
        super(clientWorld.getWorld(), clientWorld.getPhysics());
        this.delegateWorld = clientWorld;
        this.filters = filters;
    }

    @Override
    public void setBlock(Vector3i pos, VoxelBlock block) {
        VoxelBlock a = getBlock(pos);
        for(var f : filters) {
            if(a.equals(f)) {
                delegateWorld.setBlock(pos, block);
            }
        }
    }

}
