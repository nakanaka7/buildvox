package tokyo.nakanaka.buildvox.core.clientWorld;

import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selection.Selection;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

/* experimental */
public class PlayerClientWorldExp extends ClientWorld {
    private final Player player;
    private final Vector3i[] initPosArray;
    private final Selection initSel;
    private final RecordingClientWorld recordingWorld;
    private final ClientWorld delegateWorld;

    /**
     * Creates a new instance.
     * @param player the player.
     */
    public PlayerClientWorldExp(Player player) {
        super(player.getEditWorld());
        this.player = player;
        this.initPosArray = player.getPosArrayClone();
        this.initSel = player.getSelection();
        this.recordingWorld = new RecordingClientWorld(new ClientWorld(player.getEditWorld()));
        this.delegateWorld = this.recordingWorld;
    }

    private PlayerClientWorldExp(Builder builder) {
        super(builder.player.getEditWorld());
        this.player = builder.player;
        this.initPosArray = player.getPosArrayClone();
        this.initSel = player.getSelection();
        this.recordingWorld = new RecordingClientWorld(new ClientWorld(player.getEditWorld()));
        ClientWorld dw = this.recordingWorld;
        VoxelBlock background = player.getBackgroundBlock();
        if(builder.masked) {
            dw = new MaskedClientWorld(background, dw);
        }
        if(builder.integrity != null) {
            dw = new IntegrityClientWorld(builder.integrity, background, dw);
        }
        this.delegateWorld = dw;
    }

    /** Builder */
    public static class Builder {
        private final Player player;
        private Double integrity;
        private boolean masked;

        /** Creates a new instance. */
        public Builder(Player player) {
            this.player = player;
        }

        /** Set the integrity. */
        public Builder integrity(double integrity) {
            this.integrity = integrity;
            return this;
        }

        /** Set masked. */
        public Builder masked(boolean b) {
            this.masked = b;
            return this;
        }

        /**
         * Returns a PlayerClientWorld instance.
         * @throws IllegalArgumentException if failed to build a new instance.
         */
        public PlayerClientWorldExp build() {
            try {
                return new PlayerClientWorldExp(this);
            }catch (Exception ex) {
                throw new IllegalArgumentException();
            }
        }

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
    public VoxelBlock getBlock(Vector3i pos) {
        return delegateWorld.getBlock(pos);
    }

    @Override
    public void setBlock(Vector3i pos, VoxelBlock block) {
        delegateWorld.setBlock(pos, block);
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
