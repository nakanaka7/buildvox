package tokyo.nakanaka.buildvox.bukkit;

import org.bukkit.block.CommandBlock;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tokyo.nakanaka.buildvox.bukkit.block.BlockUtils;
import tokyo.nakanaka.buildvox.bukkit.block.BukkitBlockEntity;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.World;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

import java.util.HashSet;
import java.util.Set;

/**
 * The class which implements {@link World} for Bukkit Platform
 */
public class BukkitWorld implements World {
    private org.bukkit.World original;

    /**
     * Constructs an instance from a Server and the original world of org.bukkit.World
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
        org.bukkit.block.BlockState blockState = block.getState();
        VoxelBlock block1 = VoxelBlock.valueOf(blockState.getBlockData().getAsString());
        Set<BlockUtils.BlockEntityData> blockEntityDataSet = new HashSet<>();
        Inventory inventory = null;
        if(blockState instanceof CommandBlock commandBlock) {
            blockEntityDataSet.add(new BlockUtils.CommandBlockData(commandBlock.getCommand(), commandBlock.getName()));
        }
        if(blockState instanceof Sign sign) {
            blockEntityDataSet.add(new BlockUtils.SignData(sign.getLines(), sign.isGlowingText()));
        }
        if(blockState instanceof Container container) {
            inventory = container.getSnapshotInventory();
        }
        var entity = new BukkitBlockEntity(blockEntityDataSet, inventory);
        return new VoxelBlock(block1.getBlockId(), block1.getState(), entity);
    }

    @Override
    public void setBlock(int x, int y, int z, VoxelBlock block, boolean physics){
        org.bukkit.block.Block voxel = original.getBlockAt(x, y, z);
        String blockStr = block.withoutEntity().toString();
        BlockData blockData = BuildVoxPlugin.getInstance().getServer().createBlockData(blockStr);
        voxel.setBlockData(blockData, physics);
        org.bukkit.block.BlockState blockState = voxel.getState();
        BukkitBlockEntity entity = (BukkitBlockEntity) block.getEntity();
        if(entity != null) {
            for (var blockEntityData : entity.getBlockEntityDatum()) {
                blockEntityData.merge(blockState);
            }
            blockState.update();
            var inventory = entity.getInventory();
            if (blockState instanceof Container container && inventory != null) {
                ItemStack[] contents = inventory.getContents();
                container.getInventory().setContents(contents);
            }
        }else {
            blockState.update();
        }
    }

}
