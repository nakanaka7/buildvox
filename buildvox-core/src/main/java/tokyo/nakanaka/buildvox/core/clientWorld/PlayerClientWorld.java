package tokyo.nakanaka.buildvox.core.clientWorld;

import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

/**
 * A client world for a player. Calling end() stores an undoable edit
 * into the player.
 */
public class PlayerClientWorld extends ClientWorld {
    private final Player player;
    private final Vector3i[] initPosArray;
    private final Selection initSel;
    private final RecordingClientWorld recordingWorld;

    /**
     * Creates a new instance.
     * @param player the player.
     */
    public PlayerClientWorld(Player player) {
        super(player.getEditWorld());
        this.player = player;
        this.initPosArray = player.getPosArrayClone();
        this.initSel = player.getSelection();
        this.recordingWorld = new RecordingClientWorld(new ClientWorld(player.getEditWorld()));
    }

    /**
     * Gets the player.
     * @return the player.
     */
    public Player getPlayer() {
        return player;
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

    @Override
    public void setBlock(Vector3i pos, VoxelBlock block) {
        recordingWorld.setBlock(pos, block);
    }

    /**
     * Stores the selection change and block changes as one edit into player.
     * @return the edit exit.
     */
    public EditExit end() {
        Vector3i[] endPosArray = player.getPosArrayClone();
        Selection endSel = player.getSelection();
        UndoableEdit posArrayOrSelEdit = UndoableEdits.create(
            () -> {
                if(initSel == null) {
                    player.setPosArray(initPosArray);
                }else{
                    player.setSelection(initSel);
                }
            },
            () -> {
                if(endSel == null) {
                    player.setPosArray(endPosArray);
                }else {
                    player.setSelection(endSel);
                }
            }
        );
        UndoableEdit blockEdit = recordingWorld.createEdit();
        CompoundEdit compoundEdit = new CompoundEdit();
        compoundEdit.addEdit(posArrayOrSelEdit);
        compoundEdit.addEdit(blockEdit);
        compoundEdit.end();
        player.getUndoManager().addEdit(compoundEdit);
        return new EditExit(recordingWorld.blockCount(), 0, 0);
    }

}
