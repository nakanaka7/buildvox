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

    public Block getBackgroundBlock() {
        return backgroundBlock;
    }

    public void setBackgroundBlock(Block backgroundBlock) {
        this.backgroundBlock = backgroundBlock;
    }

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

    public Vector3i[] getPosArrayClone() {
        return posArray.clone();
    }

    public Selection getSelection() {
        return selection;
    }

    public void setSelection(World world, Selection selection) {
        this.editTargetWorld = world;
        Arrays.fill(posArray, null);
        this.selection = selection;
        updateParticleGui();
    }

    public void setPosArrayWithSelectionNull(World world, Vector3i[] posArray) {
        this.editTargetWorld = world;
        this.posArray = posArray;
        this.selection = null;
        updateParticleGui();
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    public Clipboard getClipboard() {
        return clipboard;
    }

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

    public void setParticleGuiVisible(boolean particleGuiVisible) {
        this.particleGuiVisible = particleGuiVisible;
        if(particleGuiVisible) {
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
