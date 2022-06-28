package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.edit.editWorld.EditWorld;
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
            posArrayOrSelEdit = createPosArrayEdit(player, endPosArray);
            player.setPosArray(endPosArray);
        }else {
            posArrayOrSelEdit = createSelectionEdit(player, endSel);
            player.setSelection(endSel);
        }
        UndoableEdit blockEdit = createBlockEdit(this);
        CompoundEdit compoundEdit = new CompoundEdit();
        compoundEdit.addEdit(posArrayOrSelEdit);
        compoundEdit.addEdit(blockEdit);
        compoundEdit.end();
        player.getUndoManager().addEdit(compoundEdit);
        return new EditExit(this.blockCount(), 0, 0);
    }

    /* Creates an undoable edit for edit world that is target of recordingEditWorld */
    private static UndoableEdit createBlockEdit(RecordingEditWorld recordingEditWorld) {
        Clipboard undoClipboard = recordingEditWorld.getUndoClipboard();
        Clipboard redoClipboard = recordingEditWorld.getRedoClipboard();
        EditWorld editWorld = new EditWorld(recordingEditWorld.getOriginal());
        return createEdit(
                () -> {
                    Set<Vector3i> blockPosSet = undoClipboard.blockPosSet();
                    for(Vector3i pos : blockPosSet) {
                        VoxelBlock block = undoClipboard.getBlock(pos.x(), pos.y(), pos.z());
                        editWorld.setBlock(pos, block);
                    }
                },
                () -> {
                    Set<Vector3i> blockPosSet = redoClipboard.blockPosSet();
                    for(Vector3i pos : blockPosSet) {
                        VoxelBlock block = redoClipboard.getBlock(pos.x(), pos.y(), pos.z());
                        editWorld.setBlock(pos, block);
                    }
                }
        );
    }

    /* Creates an edit to set a new pos array into the player */
    private static UndoableEdit createPosArrayEdit(Player player, Vector3i[] posArray) {
        Vector3i[] initPosArray = player.getPosArrayClone();
        Selection initSelection = player.getSelection();
        return createEdit(
                () -> {
                    if(initSelection == null) {
                        player.setPosArray(initPosArray.clone());
                    }else{
                        player.setSelection(initSelection);
                    }
                },
                () -> player.setPosArray(posArray)
        );
    }

    /* Creates an edit to set a new selection into the player */
    private static UndoableEdit createSelectionEdit(Player player, Selection selection) {
        Vector3i[] initPosArray = player.getPosArrayClone();
        Selection initSelection = player.getSelection();
        return createEdit(
                () -> {
                    if(initSelection == null) {
                        player.setPosArray(initPosArray.clone());
                    }else{
                        player.setSelection(initSelection);
                    }
                },
                () -> player.setSelection(selection)
        );
    }

    /**
     * Creates an UndoableEdit from undoRunnable and redoRunnable.
     * @param undoRunnable a runnable for undo.
     * @param redoRunnable a runnable for redo.
     * @return an instance
     */
    private static UndoableEdit createEdit(Runnable undoRunnable, Runnable redoRunnable) {
        return new UndoableEdit() {
            @Override
            public void undo() {
                undoRunnable.run();
            }

            @Override
            public boolean canUndo() {
                return true;
            }

            @Override
            public void redo() {
                redoRunnable.run();
            }

            @Override
            public boolean canRedo() {
                return true;
            }

            @Override
            public void die() {

            }

            @Override
            public boolean addEdit(UndoableEdit anEdit) {
                return false;
            }

            @Override
            public boolean replaceEdit(UndoableEdit anEdit) {
                return false;
            }

            @Override
            public boolean isSignificant() {
                return true;
            }

            @Override
            public String getPresentationName() {
                return "";
            }

            @Override
            public String getUndoPresentationName() {
                return "";
            }

            @Override
            public String getRedoPresentationName() {
                return "";
            }
        };
    }

}
