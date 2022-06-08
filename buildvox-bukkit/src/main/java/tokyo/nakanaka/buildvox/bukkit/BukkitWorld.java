package tokyo.nakanaka.buildvox.bukkit;

import org.bukkit.Server;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.EntityImpl;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;
import tokyo.nakanaka.buildvox.core.world.World;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
    public VoxelBlock getBlock(int x, int y, int z){
        org.bukkit.block.Block voxel = original.getBlockAt(x, y, z);
        return BukkitVoxelBlock.newInstance(voxel.getState());
    }

    private static record BlockEntityContent(Set<BukkitVoxelBlock.BlockEntityData> blockEntityDataSet, Inventory inventory) {
    }

    public VoxelBlock getBlockNew(int x, int y, int z) {
        org.bukkit.block.Block voxel = original.getBlockAt(x, y, z);
        var b = BukkitVoxelBlock.newInstance(voxel.getState());
        var block = b.getBlock();
        var state = b.getState();
        var blockEntityDataSet = b.getBlockEntityDataSet();
        var inventory = b.getInventory();
        var entityContent = new BlockEntityContent(blockEntityDataSet, inventory);
        var entity = new EntityImpl(entityContent);
        return new VoxelBlock(block, state, entity);
    }

    @Override
    public void setBlock(int x, int y, int z, VoxelBlock block, boolean physics){
        org.bukkit.block.Block targetBlock = original.getBlockAt(x, y, z);
        if(block instanceof BukkitVoxelBlock bukkitBlock) {
            bukkitBlock.setToWorld(server, targetBlock, physics);
        }else{
            String blockStr = block.toString();
            BlockData blockData = server.createBlockData(blockStr);
            targetBlock.setBlockData(blockData, physics);
            targetBlock.getState().update();
        }
    }

    public void setBlockNew(int x, int y, int z, VoxelBlock block, boolean physics){
        org.bukkit.block.Block targetBlock = original.getBlockAt(x, y, z);
        String blockStr = block.toString();
        BlockData blockData = server.createBlockData(blockStr);
        targetBlock.setBlockData(blockData, physics);
        org.bukkit.block.BlockState blockState = targetBlock.getState();
        EntityImpl entity = (EntityImpl) block.getEntity();
        BlockEntityContent entityContent = (BlockEntityContent) entity.getObj();
        for(var blockEntityData : entityContent.blockEntityDataSet()) {
            blockEntityData.merge(blockState);
        }
        blockState.update();
        var inventory = entityContent.inventory();
        if(blockState instanceof Container container && inventory != null) {
            ItemStack[] contents = inventory.getContents();
            container.getInventory().setContents(contents);
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
