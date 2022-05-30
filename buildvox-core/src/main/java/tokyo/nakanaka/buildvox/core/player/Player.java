package tokyo.nakanaka.buildvox.core.player;

import tokyo.nakanaka.buildvox.core.PlayerEntity;
import tokyo.nakanaka.buildvox.core.commandSender.CommandSender;
import tokyo.nakanaka.buildvox.core.edit.Clipboard;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.particleGui.ParticleGuiRepository;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.system.Entity;
import tokyo.nakanaka.buildvox.core.world.Block;
import tokyo.nakanaka.buildvox.core.world.World;

import javax.swing.undo.UndoManager;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player.
 */
public class Player implements CommandSender, Entity<UUID> {
    private UUID id;
    private Block backgroundBlock;
    private Clipboard clipboard;
    private UndoManager undoManager = new UndoManager();
    private World editTargetWorld;
    private Vector3i[] posArray;
    private Selection selection;
    private PlayerEntity playerEntity;
    private boolean particleGuiVisible;

    public Player(PlayerEntity playerEntity) {
        this.id = playerEntity.getId();
        this.posArray = new Vector3i[BuildVoxSystem.config.posArrayLength()];
        this.backgroundBlock = BuildVoxSystem.config.backgroundBlock();
        this.playerEntity = playerEntity;
    }

    @Override
    public UUID getId() {
        return id;
    }

    /**
     * Get the background block
     * @return the background block
     */
    public Block getBackgroundBlock() {
        return backgroundBlock;
    }

    /**
     * Set the background block
     * @param backgroundBlock a background block
     */
    public void setBackgroundBlock(Block backgroundBlock) {
        this.backgroundBlock = backgroundBlock;
    }

    /**
     * Get the edit target world.
     * @return the edit target world.
     */
    public World getEditTargetWorld() {
        return editTargetWorld;
    }

    /**
     * Set an edit target world. The all elements of pos array will be set null and the selection will be null.
     * @param editTargetWorld a world to edit
     */
    public void setEditTargetWorld(World editTargetWorld) {
        this.editTargetWorld = editTargetWorld;
        Arrays.fill(posArray, null);
        this.selection = null;
        updateParticleGui();
    }

    /**
     * Get the cloned pos array.
     * @return the cloned pos array.
     */
    public Vector3i[] getPosArrayClone() {
        return posArray.clone();
    }

    /**
     * Get the selection.
     * @return the selection.
     */
    public Selection getSelection() {
        return selection;
    }

    /**
     * Use setSelection without world.
     */
    @Deprecated
    public void setSelection(World world, Selection selection) {
        this.editTargetWorld = world;
        setSelection(selection);
    }

    /**
     * Set a selection. The all elements of pos array will be set null.
     * @param selection the selection
     */
    public void setSelection(Selection selection) {
        Arrays.fill(posArray, null);
        this.selection = selection;
        updateParticleGui();
    }

    /**
     * Use setPosArray without world
     */
    @Deprecated
    public void setPosArray(World world, Vector3i[] posArray) {
        this.editTargetWorld = world;
        setPosArray(posArray);
    }

    /**
     * Set a pos array. The selection will be set null.
     * @param posArray the pos array.
     */
    public void setPosArray(Vector3i[] posArray) {
        this.posArray = posArray;
        this.selection = null;
        updateParticleGui();
    }

    /**
     * Get the undo-manager.
     */
    public UndoManager getUndoManager() {
        return undoManager;
    }

    /**
     * Get the clipboard.
     * @return the clipboard.
     */
    public Clipboard getClipboard() {
        return clipboard;
    }

    /**
     * Set the clipboard.
     * @param clipboard the clipboard.
     */
    public void setClipboard(Clipboard clipboard) {
        this.clipboard = clipboard;
    }

    public PlayerEntity getPlayerEntity() {
        return playerEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id.equals(player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public void sendOutMessage(String msg) {
        playerEntity.println(BuildVoxSystem.getConfig().outColor() + msg);
    }

    @Override
    public void sendErrMessage(String msg) {
        playerEntity.println(BuildVoxSystem.getConfig().errColor() + msg);
    }

    @Override
    public World getWorld() {
        return playerEntity.getWorld();
    }

    @Override
    public Vector3i getBlockPos() {
        return playerEntity.getBlockPos();
    }

    /**
     * Set whether the particle gui is visible
     * @param b true if the gui is visible, otherwise false.
     */
    public void setParticleGuiVisible(boolean b) {
        this.particleGuiVisible = b;
        if(b) {
            ParticleGuiRepository.PARTICLE_GUI_REPOSITORY.create(this);
            updateParticleGui();
        }else{
            ParticleGuiRepository.PARTICLE_GUI_REPOSITORY.delete(this);
        }
    }

    private void updateParticleGui() {
        if(!particleGuiVisible) return;
        ParticleGuiRepository.PARTICLE_GUI_REPOSITORY.update(this);
    }

}
