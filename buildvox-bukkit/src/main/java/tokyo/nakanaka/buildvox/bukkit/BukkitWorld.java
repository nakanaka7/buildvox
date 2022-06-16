package tokyo.nakanaka.buildvox.bukkit;

import org.bukkit.Server;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;
import tokyo.nakanaka.buildvox.core.world.World;

import java.util.Objects;
import java.util.UUID;

/**
 * The class which implements {@link World} for Bukkit Platform
 */
public class BukkitWorld implements World {
    private org.bukkit.World original;
    private UUID uuid;
    private Server server;

    /**
     * Constructs an instance from a Server and the original world of org.bukkit.World
     * @param server a server
     * @param original the original world
     */
    public BukkitWorld(Server server, org.bukkit.World original) {
        this.original = original;
        this.uuid = original.getUID();
        this.server = server;
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
        return BukkitVoxelBlock.getVoxelBlock(block);
    }

    @Override
    public void setBlock(int x, int y, int z, VoxelBlock block, boolean physics){
        org.bukkit.block.Block voxel = original.getBlockAt(x, y, z);
        BukkitVoxelBlock.setVoxelBlock(server, voxel, block, physics);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BukkitWorld world = (BukkitWorld) o;
        return uuid.equals(world.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

}
