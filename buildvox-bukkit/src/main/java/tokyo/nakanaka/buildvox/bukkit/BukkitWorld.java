package tokyo.nakanaka.buildvox.bukkit;

import tokyo.nakanaka.buildvox.bukkit.block.BlockUtils;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.world.World;

/**
 * The class which implements {@link World} for Bukkit Platform
 */
public class BukkitWorld implements World {
    private org.bukkit.World original;

    /**
     * Constructs an instance from a Server and the original world of org.bukkit.World
     * @param server a server
     * @param original the original world
     */
    public BukkitWorld(org.bukkit.World original) {
        this.original = original;
    }

    /**
     * Get the original world.
     * @return the original world.
     */
    public org.bukkit.World getOriginal() {
        return original;
    }

    @Override
    public NamespacedId getId() {
        return new NamespacedId(original.getName());
    }

    @Override
    public VoxelBlock getBlock(int x, int y, int z) {
        org.bukkit.block.Block block = original.getBlockAt(x, y, z);
        return BlockUtils.getVoxelBlock(block);
    }

    @Override
    public void setBlock(int x, int y, int z, VoxelBlock block, boolean physics){
        org.bukkit.block.Block voxel = original.getBlockAt(x, y, z);
        BlockUtils.setVoxelBlock(voxel, block, physics);
    }

}
