package tokyo.nakanaka.buildvox.core.player;

import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.World;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.brushSource.BrushSource;
import tokyo.nakanaka.buildvox.core.brushSource.BrushSourceClipboards;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.particleGui.ParticleGui;
import tokyo.nakanaka.buildvox.core.particleGui.PlayerParticleGui;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;

import javax.swing.undo.UndoManager;
import java.util.Arrays;

/**
 * Represents a player.
 */
public class Player {
    private final UndoManager undoManager = new UndoManager();
    private VoxelBlock backgroundBlock = VoxelBlock.valueOf(BuildVoxSystem.getBackgroundBlockId().toString());
    private Clipboard clipboard;
    private World editWorld;
    private Vector3i[] posArray = new Vector3i[2];
    private Selection selection;
    private BrushSource brushSource;
    private final PlayerEntity playerEntity;
    private final PlayerMessenger messenger;
    private final PlayerParticleGui playerParticleGui = new PlayerParticleGui(this);

    /**
     * Creates a new instance by the player entity.
     * @param playerEntity the player entity.
     */
    public Player(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
        this.messenger = new PlayerMessenger(playerEntity);
        Clipboard brushClip = BrushSourceClipboards.createSphere(VoxelBlock.valueOf("stone"), 3);
        this.brushSource = new BrushSource(brushClip);
    }

    /**
     * Gets the background block
     * @return the background block
     */
    public VoxelBlock getBackgroundBlock() {
        return backgroundBlock;
    }

    /**
     * Sets the background block
     * @param backgroundBlock a background block
     */
    public void setBackgroundBlock(VoxelBlock backgroundBlock) {
        this.backgroundBlock = backgroundBlock;
    }

    /**
     * Gets the edit world.
     * @return the edit world.
     */
    public World getEditWorld() {
        return editWorld;
    }

    /**
     * Sets an edit world. If the new world is different from the old one, the all elements of pos array will
     * be set null and the selection will be null.
     * @param editWorld the edit world.
     */
    public void setEditWorld(World editWorld) {
        if(this.editWorld == null || !this.editWorld.getId().equals(editWorld.getId())) {
            this.editWorld = editWorld;
            Arrays.fill(posArray, null);
            this.selection = null;
            updateParticleGui();
        }
    }

    /**
     * Gets the cloned pos array.
     * @return the cloned pos array.
     */
    public Vector3i[] getPosArrayClone() {
        return posArray.clone();
    }

    /**
     * Gets the selection.
     * @return the selection.
     */
    public Selection getSelection() {
        return selection;
    }

    /**
     * Sets a selection. The all elements of pos array will be set null.
     * @param selection the selection
     */
    public void setSelection(Selection selection) {
        Arrays.fill(posArray, null);
        this.selection = selection;
        updateParticleGui();
    }

    /**
     * Sets a pos array. The array will be cloned internally. The selection will be set null.
     * @param posArray the pos array.
     */
    public void setPosArray(Vector3i[] posArray) {
        this.posArray = posArray.clone();
        this.selection = null;
        updateParticleGui();
    }

    /**
     * Gets the undo-manager.
     */
    public UndoManager getUndoManager() {
        return undoManager;
    }

    /**
     * Gets the clipboard.
     * @return the clipboard.
     */
    public Clipboard getClipboard() {
        return clipboard;
    }

    /**
     * Sets the clipboard.
     * @param clipboard the clipboard.
     */
    public void setClipboard(Clipboard clipboard) {
        this.clipboard = clipboard;
    }

    public BrushSource getBrushSource() {
        return brushSource;
    }

    public void setBrushSource(BrushSource brushSource) {
        this.brushSource = brushSource;
    }

    /**
     * Gets the player entity.
     * @return the player entity.
     */
    public PlayerEntity getPlayerEntity() {
        return playerEntity;
    }

    /**
     * Gets the messenger.
     */
    public PlayerMessenger getMessenger() {
        return messenger;
    }

    public ParticleGui getParticleGui() {
        return playerParticleGui.getParticleGui();
    }

    public boolean isParticleGuiVisible() {
        return playerParticleGui.isParticleGuiVisible();
    }

    /**
     * Sets whether the particle gui is visible
     * @param b true if the gui is visible, otherwise false.
     */
    public void setParticleGuiVisible(boolean b) {
        playerParticleGui.setParticleGuiVisible(b);
        if (b) {
            if(playerEntity == null) return;
            playerParticleGui.setParticleGui(new ParticleGui(playerEntity));
            updateParticleGui();
        } else {
            playerParticleGui.getParticleGui().close();
        }
    }

    private void updateParticleGui() {
        playerParticleGui.update();
    }

}
