package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.edit.editWorld.RecordingEditWorld;
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
    private Selection sel;

    /***
     * Creates a new instance.
     * @param player the player.
     */
    public PlayerWorld(Player player) {
        super(player.getEditWorld());
        this.player = player;
        this.sel = player.getSelection();
    }

    /**
     * Set the selection.
     *
     * @param sel the selection.
     */
    public void setSelection(Selection sel) {
        this.sel = sel;
    }

    /**
     * Stores the selection change and block changes as one edit into player.
     *
     * @return the edit exit.
     */
    public EditExit end() {
        UndoableEdit selEdit = PlayerEdits.createSelectionEdit(player, sel);
        player.setSelection(sel);
        UndoableEdit blockEdit = PlayerEdits.createBlockEdit(this);
        CompoundEdit compoundEdit = new CompoundEdit();
        compoundEdit.addEdit(selEdit);
        compoundEdit.addEdit(blockEdit);
        compoundEdit.end();
        player.getUndoManager().addEdit(compoundEdit);
        return new EditExit(this.blockCount(), 0, 0);
    }

}
