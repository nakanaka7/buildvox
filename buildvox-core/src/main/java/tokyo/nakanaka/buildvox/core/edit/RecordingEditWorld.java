package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;
import tokyo.nakanaka.buildvox.core.world.World;

/**
   The edit world which records block settings. This object has undo-clipboard and redo-clipboard.
 */
@Deprecated
class RecordingEditWorld extends EditWorld {
    protected Clipboard undoClip = new Clipboard();
    protected Clipboard redoClip = new Clipboard();
    protected int blockCount;

    /**
     * @param original the original world. The block setting physics will be set "false".
     */
    public RecordingEditWorld(World original) {
        super(original);
    }

    /**
     * Before the block setting, the original block will be set into the same position of the undo-clipboard.
     * Then set the block into the original world and the same position of the redo-clipboard.
     * @param pos the position to set a block.
     * @param block the block to set.
     */
    @Override
    public void setBlock(Vector3i pos, VoxelBlock block) {
        int x = pos.x();
        int y = pos.y();
        int z = pos.z();
        if(undoClip.getBlock(x, y, z) == null) {
            VoxelBlock originalBlock = getBlock(pos);
            undoClip.setBlock(x, y, z, originalBlock);
        }
        super.setBlock(pos, block);
        redoClip.setBlock(x, y, z, block);
        ++blockCount;
    }

    /**
     * Get the clipboard which stores the original blocks before the block-settings.
     * @return the clipboard which stores the original blocks before the block-settings.
     */
    public Clipboard getUndoClipboard() {
        return undoClip;
    }

    /**
     * Get the clipboard which stores the same blocks of the block-settings.
     * @return the clipboard which stores the same blocks of the block-settings.
     */
    public Clipboard getRedoClipboard() {
        return redoClip;
    }

    /**
     * Get the block count of block-settings.
     * @return the block count of block-settings.
     */
    public int blockCount(){
        return this.blockCount;
    }

}
