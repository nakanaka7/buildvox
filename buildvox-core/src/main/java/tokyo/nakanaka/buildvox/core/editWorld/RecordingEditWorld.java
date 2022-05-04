package tokyo.nakanaka.buildvox.core.editWorld;

import tokyo.nakanaka.buildvox.core.edit.Clipboard;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.Block;
import tokyo.nakanaka.buildvox.core.world.World;

/*
    Experimental
 */
public class RecordingEditWorld extends EditWorld {
    private Clipboard undoClip = new Clipboard();
    private Clipboard redoClip = new Clipboard();
    private int blockCount;

    /**
     * @param original the original world
     */
    public RecordingEditWorld(World original) {
        super(original);
    }

    @Override
    public void setBlock(Vector3i pos, Block block) {
        int x = pos.x();
        int y = pos.y();
        int z = pos.z();
        if(undoClip.getBlock(x, y, z) == null) {
            Block originalBlock = getBlock(pos);
            undoClip.setBlock(x, y, z, originalBlock);
        }
        super.setBlock(pos, block);
        redoClip.setBlock(x, y, z, block);
        ++blockCount;
    }

    public Clipboard getUndoClipboard() {
        return undoClip;
    }

    public Clipboard getRedoClipboard() {
        return redoClip;
    }

    public int blockCount(){
        return this.blockCount;
    }

}
