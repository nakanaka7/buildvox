package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.blockSpace.BlockSpace3;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

import java.util.Set;

@Deprecated
public class ClipboardBlockSpace3 implements BlockSpace3<VoxelBlock> {
    private Clipboard clipboard;

    public ClipboardBlockSpace3(Clipboard clipboard) {
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
