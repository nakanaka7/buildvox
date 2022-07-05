package tokyo.nakanaka.buildvox.core;

import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.math.region3d.Infinite;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Represents a clipboard. */
public class Clipboard implements VoxelSpace<VoxelBlock> {
    private final Selection selection;
    private final Map<Vector3i, VoxelBlock> blockMap = new HashMap<>();
    private Integer maxX;
    private Integer maxY;
    private Integer maxZ;
    private Integer minX;
    private Integer minY;
    private Integer minZ;
    private boolean locked = false;

    /**
     * Creates a new instance.
     * @param selection the selection which is the bound of this clipboard.
     */
    public Clipboard(Selection selection) {
        this.selection = selection;
    }

    /**
     * Creates a new instance with infinite selection.
     */
    public Clipboard() {
        Parallelepiped bound = new Parallelepiped(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
                Double.MAX_VALUE, Double.MAX_VALUE);
        this.selection = new Selection(new Infinite(), bound);
    }

    /** Gets the selection */
    public Selection getSelection() {
        return selection;
    }

    /**
     * Locks this clipboard, which means that this clipboard will be read only.
     */
    public void lock(){
        this.locked = true;
    }

    public void setBlock(Vector3i pos, VoxelBlock block) {
        setBlock(pos.x(), pos.y(), pos.z(), block);
    }

    /**
     * Set the block into this clipboard. If the pos is out of the selection, if skips the block setting.
     * @param x the x-coordinate of the block in the clipboard position
     * @param y the y-coordinate of the block in the clipboard position
     * @param z the z-coordinate of the block in the clipboard position
     * @param block the block to set
     * @throws IllegalStateException if this clipboard is locked(the read only mode).
     */
    public void setBlock(int x, int y, int z, VoxelBlock block){
        if(this.locked){
            throw new IllegalStateException();
        }
        if(!selection.getRegion3d().contains(x + 0.5, y + 0.5, z + 0.5)) {
            return;
        }
        if(this.blockCount() == 0){
            this.maxX = x;
            this.maxY = y;
            this.maxZ = z;
            this.minX = x;
            this.minY = y;
            this.minZ = z;
        }else{
            this.maxX = Math.max(this.maxX, x);
            this.maxY = Math.max(this.maxY, y);
            this.maxZ = Math.max(this.maxZ, z);
            this.minX = Math.min(this.minX, x);
            this.minY = Math.min(this.minY, y);
            this.minZ = Math.min(this.minZ, z);
        }
        this.blockMap.put(new Vector3i(x, y, z), block);
    }

    public VoxelBlock getBlock(Vector3i pos) {
        return this.blockMap.get(pos);
    }

    public VoxelBlock getBlock(int x, int y, int z){
        return this.blockMap.get(new Vector3i(x, y, z));
    }

    public Set<Vector3i> blockPosSet() {
        return blockMap.keySet().stream()
                .map(s -> new Vector3i(s.x(), s.y(), s.z()))
                .collect(Collectors.toSet());
    }

    public int blockCount(){
        return this.blockMap.size();
    }

    public int maxX(){
        if(this.blockCount() == 0){
            throw new IllegalStateException();
        }
        return this.maxX;
    }

    public int maxY(){
        if(this.blockCount() == 0){
            throw new IllegalStateException();
        }
        return this.maxY;
    }

    public int maxZ(){
        if(this.blockCount() == 0){
            throw new IllegalStateException();
        }
        return this.maxZ;
    }


    public int minX(){
        if(this.blockCount() == 0){
            throw new IllegalStateException();
        }
        return this.minX;
    }

    public int minY(){
        if(this.blockCount() == 0){
            throw new IllegalStateException();
        }
        return this.minY;
    }

    public int minZ(){
        if(this.blockCount() == 0){
            throw new IllegalStateException();
        }
        return this.minZ;
    }

}
