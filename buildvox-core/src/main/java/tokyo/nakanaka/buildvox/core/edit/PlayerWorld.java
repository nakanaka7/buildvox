package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.edit.editWorld.RecordingEditWorld;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selection.Selection;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

/**
 * An edit world for a player. Calling end() stores an undoable edit
 * into the player.
 */
public class PlayerWorld extends RecordingEditWorld {
    private final Player player;
    private final Vector3i[] initPosArray;
    private final Selection initSel;

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
     * Stores the selection change and block changes as one edit into player.
     * @return the edit exit.
     */
    public EditExit end() {
        Vector3i[] endPosArray = player.getPosArrayClone();
        Selection endSel = player.getSelection();
        if(initSel != null) {
            player.setSelection(initSel);
        }else{
            player.setPosArray(initPosArray);
        }
        UndoableEdit posArrayOrSelEdit;
        if(endSel == null) {
            posArrayOrSelEdit = PlayerEdits.createPosArrayEdit(player, endPosArray);
            player.setPosArray(endPosArray);
        }else {
            posArrayOrSelEdit = PlayerEdits.createSelectionEdit(player, endSel);
            player.setSelection(endSel);
        }
        UndoableEdit blockEdit = PlayerEdits.createBlockEdit(this);
        CompoundEdit compoundEdit = new CompoundEdit();
        compoundEdit.addEdit(posArrayOrSelEdit);
        compoundEdit.addEdit(blockEdit);
        compoundEdit.end();
        player.getUndoManager().addEdit(compoundEdit);
        return new EditExit(this.blockCount(), 0, 0);
    }

}
