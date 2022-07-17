package tokyo.nakanaka.buildvox.core.player;

import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.World;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.brushSource.BrushSource;
import tokyo.nakanaka.buildvox.core.brushSource.BrushSourceClipboards;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.particleGui.Color;
import tokyo.nakanaka.buildvox.core.particleGui.ParticleGui;
import tokyo.nakanaka.buildvox.core.selection.FillSelection;
import tokyo.nakanaka.buildvox.core.selection.PasteSelection;
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
    private boolean particleGuiVisible;
    private ParticleGui particleGui;

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
     * Sets a pos array. The selection will be set null.
     * @param posArray the pos array.
     */
    public void setPosArray(Vector3i[] posArray) {
        this.posArray = posArray;
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

    /**
     * Sets whether the particle gui is visible
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
        World posOrSelectionWorld = getEditWorld();
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
