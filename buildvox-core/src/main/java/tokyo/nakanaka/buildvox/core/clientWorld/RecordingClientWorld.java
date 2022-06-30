package tokyo.nakanaka.buildvox.core.clientWorld;

import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;
import tokyo.nakanaka.buildvox.core.world.World;

import javax.swing.undo.UndoableEdit;
import java.util.Set;

/** A client world which records changing. */
public class RecordingClientWorld extends ClientWorld {
    private final Clipboard undoClip = new Clipboard();
    private final Clipboard redoClip = new Clipboard();

    public RecordingClientWorld(World original) {
        super(original);
    }

    public RecordingClientWorld(World original, boolean physics) {
        super(original, physics);
    }

    @Override
    public void setBlock(Vector3i pos, VoxelBlock block) {
        if (undoClip.getBlock(pos) == null) {
            VoxelBlock originalBlock = original.getBlock(pos.x(), pos.y(), pos.z());
            undoClip.setBlock(pos, originalBlock);
        }
        redoClip.setBlock(pos, block);
        original.setBlock(pos.x(), pos.y(), pos.z(), block, physics);
    }

    public int changingBlockCount() {
        return undoClip.blockCount();
    }

    /**
     * Returns an UndoableEdit which records the world's changing.
     * @return an UndoableEdit which records the world's changing.
     */
    public UndoableEdit createEdit() {
        return UndoableEdits.create(
            () -> {
                Set<Vector3i> blockPosSet = undoClip.blockPosSet();
                for(Vector3i pos : blockPosSet) {
                    VoxelBlock block = undoClip.getBlock(pos.x(), pos.y(), pos.z());
                    original.setBlock(pos.x(), pos.y(), pos.z(), block, physics);
                }
            },
            () -> {
                Set<Vector3i> blockPosSet = redoClip.blockPosSet();
                for(Vector3i pos : blockPosSet) {
                    VoxelBlock block = redoClip.getBlock(pos.x(), pos.y(), pos.z());
                    original.setBlock(pos.x(), pos.y(), pos.z(), block, physics);
                }
            }
        );
    }

}
