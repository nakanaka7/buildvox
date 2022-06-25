package tokyo.nakanaka.buildvox.core.player;

import tokyo.nakanaka.buildvox.core.blockSpace.Clipboard;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.particleGui.Color;
import tokyo.nakanaka.buildvox.core.particleGui.ParticleGui;
import tokyo.nakanaka.buildvox.core.selection.FillSelection;
import tokyo.nakanaka.buildvox.core.selection.PasteSelection;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;
import tokyo.nakanaka.buildvox.core.world.World;

import javax.swing.undo.UndoManager;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player.
 */
public class Player {
    private UUID id;
    private VoxelBlock backgroundBlock;
    private Clipboard clipboard;
    private UndoManager undoManager = new UndoManager();
    private World editTargetWorld;
    private Vector3i[] posArray = new Vector3i[2];
    private Selection selection;
    private PlayerEntity playerEntity;
    private boolean particleGuiVisible;
    private ParticleGui particleGui;

    public Player(PlayerEntity playerEntity) {
        this.id = playerEntity.getId();
        this.backgroundBlock = VoxelBlock.valueOf(BuildVoxSystem.getBackgroundBlockId().toString());
        this.playerEntity = playerEntity;
    }

    /**
     * Get the background block
     * @return the background block
     */
    public VoxelBlock getBackgroundBlock() {
        return backgroundBlock;
    }

    /**
     * Set the background block
     * @param backgroundBlock a background block
     */
    public void setBackgroundBlock(VoxelBlock backgroundBlock) {
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

    /**
     * Use this objects' methods
     */
    @Deprecated
    public PlayerEntity getPlayerEntity() {
        return playerEntity;
    }

    /**
     * Give her a pos marker.
     */
    public void givePosMarker() {
        playerEntity.givePosMarker();
    }

    /**
     * Spawn redstone dust particles to the player. This particle may be seen only to the player.
     * @param color a color of particle.
     * @param world a world to spawn particle.
     * @param pos the position of the particle.
     */
    public void spawnDustParticle(Color color, World world, Vector3d pos) {
        playerEntity.spawnParticle(color, world, pos.x(), pos.y(), pos.z());
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

    public void sendOutMessage(String msg) {
        playerEntity.println(BuildVoxSystem.getOutColor() + msg);
    }

    public void sendErrMessage(String msg) {
        playerEntity.println(BuildVoxSystem.getErrColor() + msg);
    }

    public World getWorld() {
        return playerEntity.getWorld();
    }

    public Vector3i getBlockPos() {
        return playerEntity.getBlockPos();
    }

    /**
     * Set whether the particle gui is visible
     * @param b true if the gui is visible, otherwise false.
     */
    public void setParticleGuiVisible(boolean b) {
        this.particleGuiVisible = b;
        if (b) {
            if(playerEntity == null) return;
            particleGui = new ParticleGui(playerEntity);
            updateParticleGui();
        } else {
            particleGui.close();
        }
    }

    private void updateParticleGui() {
        if(!particleGuiVisible) return;
        if(particleGui == null)return;
        particleGui.clearAllLines();
        World posOrSelectionWorld = getEditTargetWorld();
        if(posOrSelectionWorld == null)return;
        Selection selection = getSelection();
        if(selection != null){
            Color color;
            if(selection instanceof PasteSelection){
                color = Color.YELLOW;
            }else if(selection instanceof FillSelection){
                color = Color.LIME;
            }else{
                color = Color.MAGENTA;
            }
            particleGui.addParallelepipedLines(color, posOrSelectionWorld, selection.getBound());
        }
        Vector3i[] posData = getPosArrayClone();
        for(int i = 0; i < posData.length; ++ i){
            Vector3i pos = posData[i];
            if(pos != null){
                Color color;
                if(i == 0){
                    color = Color.RED;
                }else if(i == 1){
                    color = Color.BLUE;
                }else if(i == 2){
                    color = Color.YELLOW;
                }else if(i == 3){
                    color = Color.LIME;
                }else{
                    throw new InternalError();
                }
                particleGui.addBlockLines(color, posOrSelectionWorld, (int)Math.floor(pos.x()), (int)Math.floor(pos.y()), (int)Math.floor(pos.z()));
                for(int j = i + 1; j < posData.length; ++ j){
                    Vector3i posJ = posData[j];
                    if(posJ != null){
                        particleGui.addLine(Color.CYAN, posOrSelectionWorld,
                                pos.x() + 0.5, pos.y() + 0.5, pos.z() + 0.5,
                                posJ.x() + 0.5, posJ.y() + 0.5, posJ.z() + 0.5);
                    }
                }
            }
        }
        if(posData.length == 2){
            Vector3i pos0 = posData[0];
            Vector3i pos1 = posData[1];
            if(pos0 != null && pos1 != null){
                double px = Math.max(pos0.x(), pos1.x()) + 1;
                double py = Math.max(pos0.y(), pos1.y()) + 1;
                double pz = Math.max(pos0.z(), pos1.z()) + 1;
                double nx = Math.min(pos0.x(), pos1.x());
                double ny = Math.min(pos0.y(), pos1.y());
                double nz = Math.min(pos0.z(), pos1.z());
                Parallelepiped parallelepiped = new Parallelepiped(px, py, pz, nx, ny, nz);
                particleGui.addParallelepipedLines(Color.CYAN, posOrSelectionWorld, parallelepiped);
            }
        }
    }

}
