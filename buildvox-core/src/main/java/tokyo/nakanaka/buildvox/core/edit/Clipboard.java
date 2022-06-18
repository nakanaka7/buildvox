package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Clipboard {
    private Map<Vector3i, VoxelBlock> blockMap = new HashMap<>();
    private Integer maxX;
    private Integer maxY;
    private Integer maxZ;
    private Integer minX;
    private Integer minY;
    private Integer minZ;
    private boolean locked = false;

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
     * Set the block into this clipboard
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
