package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import java.util.Set;

/**
 * An edit world for a player. Calling end() stores an undoable edit
 * into the player.
 */
public class PlayerWorld extends ClientWorld {
    private final Player player;
    private final Vector3i[] initPosArray;
    private final Selection initSel;
    private final Clipboard undoClip = new Clipboard();
    private final Clipboard redoClip = new Clipboard();
    private int blockCount;

    /***
     * Creates a new instance.
     * @param player the player.
     */
    public PlayerWorld(Player player) {
        super(player.getEditWorld());
        this.player = player;
        this.initPosArray = player.getPosArrayClone();
        this.initSel = player.getSelection();
    }

    /**
     * Sets the pos array.
     * @param posArray the pos array.
     */
    public void setPosArray(Vector3i[] posArray) {
        player.setPosArray(posArray);
    }

    /**
     * Sets the selection.
     * @param sel the selection.
     */
    public void setSelection(Selection sel) {
        player.setSelection(sel);
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
     * Get the block count of block-settings.
     * @return the block count of block-settings.
     */
    public int blockCount(){
        return this.blockCount;
    }

    /**
     * Stores the selection change and block changes as one edit into player.
     * @return the edit exit.
     */
    public EditExit end() {
        Vector3i[] endPosArray = player.getPosArrayClone();
        Selection endSel = player.getSelection();
        UndoableEdit posArrayOrSelEdit;
        if(endSel == null) {
            posArrayOrSelEdit = UndoableEdits.create(
                () -> {
                    if(initSel == null) {
                        player.setPosArray(initPosArray.clone());
                    }else{
                        player.setSelection(initSel);
                    }
                },
                () -> player.setPosArray(endPosArray)
            );
        }else {
            posArrayOrSelEdit = UndoableEdits.create(
                () -> {
                    if(initSel == null) {
                        player.setPosArray(initPosArray.clone());
                    }else{
                        player.setSelection(initSel);
                    }
                },
                () -> player.setSelection(endSel)
            );
        }
        UndoableEdit blockEdit = createBlockEdit();
        CompoundEdit compoundEdit = new CompoundEdit();
        compoundEdit.addEdit(posArrayOrSelEdit);
        compoundEdit.addEdit(blockEdit);
        compoundEdit.end();
        player.getUndoManager().addEdit(compoundEdit);
        return new EditExit(this.blockCount(), 0, 0);
    }

    /* Creates an undoable edit for edit world that is target of recordingEditWorld */
    private UndoableEdit createBlockEdit() {
        ClientWorld clientWorld = new ClientWorld(getOriginal());
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
