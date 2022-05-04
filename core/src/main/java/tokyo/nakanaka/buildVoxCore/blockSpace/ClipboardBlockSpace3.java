package tokyo.nakanaka.buildVoxCore.blockSpace;

import tokyo.nakanaka.buildVoxCore.edit.Clipboard;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3i;
import tokyo.nakanaka.buildVoxCore.world.Block;

import java.util.Set;

public class ClipboardBlockSpace3 implements BlockSpace3<Block> {
    private Clipboard clipboard;

    public ClipboardBlockSpace3(Clipboard clipboard) {
        this.clipboard = clipboard;
    }

    @Override
    public Block getBlock(Vector3i pos) {
        return clipboard.getBlock(pos.x(), pos.y(), pos.z());
    }

    @Override
    public void setBlock(Vector3i pos, Block block) {
        clipboard.setBlock(pos.x(), pos.y(), pos.z(), block);
    }

    public Set<Vector3i> blockPosSet() {
        return clipboard.blockPosSet();
    }

}
