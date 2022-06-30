package tokyo.nakanaka.buildvox.core.clientWorld;

import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

import javax.swing.undo.UndoableEdit;
import java.util.Set;

/** A client world which records changing. */
public class RecordingClientWorld extends ClientWorld {
    private final ClientWorld clientWorld;
    private final Clipboard undoClip = new Clipboard();
    private final Clipboard redoClip = new Clipboard();

    public RecordingClientWorld(ClientWorld clientWorld) {
        super(clientWorld.world, clientWorld.physics);
        this.clientWorld = clientWorld;
    }

    @Override
    public void setBlock(Vector3i pos, VoxelBlock block) {
        if (undoClip.getBlock(pos) == null) {
            VoxelBlock originalBlock = getBlock(pos);
            undoClip.setBlock(pos, originalBlock);
        }
        redoClip.setBlock(pos, block);
        clientWorld.setBlock(pos, block);
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
                    clientWorld.setBlock(pos, block);
                }
            },
            () -> {
                Set<Vector3i> blockPosSet = redoClip.blockPosSet();
                for(Vector3i pos : blockPosSet) {
                    VoxelBlock block = redoClip.getBlock(pos.x(), pos.y(), pos.z());
                    clientWorld.setBlock(pos, block);
                }
            }
        );
    }

}
