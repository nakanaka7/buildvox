package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selection.Selection;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

/**
 * An edit world for a player. Calling end() stores an undoable edit
 * into the player.
 */
public class PlayerWorld extends ClientWorld {
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
        this.setRecording(true);
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


    /**
     * Get the block count of block-settings.
     * @return the block count of block-settings.
     */
    public int blockCount(){
        return undoClip.blockCount();
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
        int blockCount = this.blockCount();
        UndoableEdit blockEdit = pullEdit();
        CompoundEdit compoundEdit = new CompoundEdit();
        compoundEdit.addEdit(posArrayOrSelEdit);
        compoundEdit.addEdit(blockEdit);
        compoundEdit.end();
        player.getUndoManager().addEdit(compoundEdit);
        return new EditExit(blockCount, 0, 0);
    }
}
