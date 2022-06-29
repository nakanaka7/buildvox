package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.edit.voxelSpace.VoxelSpace;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;
import tokyo.nakanaka.buildvox.core.world.World;

import javax.swing.undo.UndoableEdit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A wrapped class of World used by {@link WorldEdits}. This class's instance fixes
 * physics of block settings.
 */
public class ClientWorld implements VoxelSpace<VoxelBlock> {
    protected final World original;
    protected Clipboard undoClip = new Clipboard();
    protected Clipboard redoClip = new Clipboard();
    protected Map<Vector3i, Boolean> redoPhysics = new HashMap<>();
    protected boolean recording;
    protected boolean physics;

    /**
     * Constructs a new instance with an original World. The block setting physics will be set "false".
     * @param original the original world.
     */
    public ClientWorld(World original) {
        this.original = original;
    }

    /**
     * Constructs a new instance with an original World.
     * @param original the original world.
     */
    public ClientWorld(World original, boolean physics) {
        this.original = original;
        this.physics = physics;
    }

    public World getOriginal() {
        return original;
    }

    /**  Sets whether recording the world's changing. */
    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    /**
     * Get a block at the specified position
     * @param pos the position of the block
     * @return a block
     */
    public VoxelBlock getBlock(Vector3i pos) {
        return original.getBlock(pos.x(), pos.y(), pos.z());
    }

    /**
     * Set a block with fixed block setting physics.
     * @param pos the position to set a block.
     * @param block the block to set.
     */
    public void setBlock(Vector3i pos, VoxelBlock block) {
        if(recording) {
            if (undoClip.getBlock(pos) == null) {
                VoxelBlock originalBlock = original.getBlock(pos.x(), pos.y(), pos.z());
                undoClip.setBlock(pos, originalBlock);
            }
            redoClip.setBlock(pos, block);
            redoPhysics.put(pos, physics);
        }
        original.setBlock(pos.x(), pos.y(), pos.z(), block, physics);
    }

    /**
     * Returns an UndoableEdit which records the world's changing. The recording will be all cleared.
     * @return an UndoableEdit which records the world's changing.
     */
    public UndoableEdit pullEdit() {
        Clipboard undoClip0 = this.undoClip;
        Clipboard redoClip0 = this.redoClip;
        Map<Vector3i, Boolean> redoPhysics0 = this.redoPhysics;
        this.undoClip = new Clipboard();
        this.redoClip = new Clipboard();
        this.redoPhysics = new HashMap<>();
        return UndoableEdits.create(
            () -> {
                Set<Vector3i> blockPosSet = undoClip0.blockPosSet();
                for(Vector3i pos : blockPosSet) {
                    VoxelBlock block = undoClip0.getBlock(pos.x(), pos.y(), pos.z());
                    original.setBlock(pos.x(), pos.y(), pos.z(), block, false);
                }
            },
            () -> {
                Set<Vector3i> blockPosSet = redoClip0.blockPosSet();
                for(Vector3i pos : blockPosSet) {
                    VoxelBlock block = redoClip0.getBlock(pos.x(), pos.y(), pos.z());
                    boolean p = redoPhysics0.get(pos);
                    original.setBlock(pos.x(), pos.y(), pos.z(), block, p);
                }
            }
        );
    }

}
