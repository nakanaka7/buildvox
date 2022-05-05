package tokyo.nakanaka.buildvox.bukkit;

import org.bukkit.Server;
import org.bukkit.block.data.BlockData;
import tokyo.nakanaka.buildvox.core.world.Block;
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
    public Block getBlock(int x, int y, int z){
        org.bukkit.block.Block voxel = original.getBlockAt(x, y, z);
        return BukkitBlock.newInstance(voxel.getState());
    }

    @Override
    public void setBlock(int x, int y, int z, Block block, boolean physics){
        org.bukkit.block.Block targetBlock = original.getBlockAt(x, y, z);
        if(block instanceof BukkitBlock bukkitBlock) {
            bukkitBlock.setToWorld(server, targetBlock, physics);
        }else{
            String blockStr = block.toString();
            BlockData blockData = server.createBlockData(blockStr);
            targetBlock.setBlockData(blockData, physics);
            targetBlock.getState().update();
        }
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
