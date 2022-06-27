package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.voxelSpace.VoxelSpace;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

import java.util.Set;

@Deprecated
public class ClipboardVoxelSpace implements VoxelSpace<VoxelBlock> {
    private Clipboard clipboard;

    public ClipboardVoxelSpace(Clipboard clipboard) {
        this.clipboard = clipboard;
    }

    @Override
    public VoxelBlock getBlock(Vector3i pos) {
        return clipboard.getBlock(pos.x(), pos.y(), pos.z());
    }

    @Override
    public void setBlock(Vector3i pos, VoxelBlock block) {
        clipboard.setBlock(pos.x(), pos.y(), pos.z(), block);
    }

    public Set<Vector3i> blockPosSet() {
        return clipboard.blockPosSet();
    }

}
