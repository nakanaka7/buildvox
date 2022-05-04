package tokyo.nakanaka.buildVoxCore.player;

import tokyo.nakanaka.buildVoxCore.PlayerEntity;
import tokyo.nakanaka.buildVoxCore.edit.Clipboard;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3i;
import tokyo.nakanaka.buildVoxCore.selection.Selection;
import tokyo.nakanaka.buildVoxCore.system.BuildVoxSystem;
import tokyo.nakanaka.buildVoxCore.world.Block;
import tokyo.nakanaka.buildVoxCore.world.World;

import javax.swing.undo.UndoManager;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class Player {
    private UUID id;
    private Block backgroundBlock;
    private Clipboard clipboard;
    private UndoManager undoManager = new UndoManager();
    private World world;
    private Vector3i[] posArray;
    private Selection selection;
    private PlayerEntity playerEntity;

    public Player(UUID id, PlayerEntity playerEntity) {
        this.id = id;
        this.posArray = new Vector3i[BuildVoxSystem.config.posArrayLength()];
        this.backgroundBlock = BuildVoxSystem.config.backgroundBlock();
        this.playerEntity = playerEntity;
    }

    public UUID getId() {
        return id;
    }

    public Block getBackgroundBlock() {
        return backgroundBlock;
    }

    public void setBackgroundBlock(Block backgroundBlock) {
        this.backgroundBlock = backgroundBlock;
    }

    public World getWorld() {
        return world;
    }

    public void setWorldWithPosArrayClearedAndSelectionNull(World world) {
        this.world = world;
        Arrays.fill(posArray, null);
        this.selection = null;
    }

    public Vector3i[] getPosArrayClone() {
        return posArray.clone();
    }

    public Selection getSelection() {
        return selection;
    }

    public void setSelectionWithPosArrayCleared(World world, Selection selection) {
        this.world = world;
        Arrays.fill(posArray, null);
        this.selection = selection;
    }

    public void setPosArrayWithSelectionNull(World world, Vector3i[] posArray) {
        this.world = world;
        this.posArray = posArray;
        this.selection = null;
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

}
