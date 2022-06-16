package tokyo.nakanaka.buildvox.bukkit;

import org.bukkit.Server;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.EntityImpl;
import tokyo.nakanaka.buildvox.core.block.StateImpl;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class which extends {@link VoxelBlock} for Bukkit Platform
 */
public class BukkitVoxelBlock extends VoxelBlock {
    private Set<BlockEntityData> blockEntityDataSet = new HashSet<>();
    private Inventory inventory;

    private BukkitVoxelBlock(NamespacedId id, Map<String, String> stateMap) {
        super(id, new StateImpl(stateMap));
    }

    public interface BlockEntityData {
        void merge(org.bukkit.block.BlockState blockState);
    }

    public Set<BlockEntityData> getBlockEntityDataSet() {
        return blockEntityDataSet;
    }

    public Inventory getInventory() {
        return inventory;
    }

    private static record CommandBlockData(String command, String name) implements BlockEntityData {
        public void merge(org.bukkit.block.BlockState blockState){
            if(blockState instanceof CommandBlock commandBlock) {
                commandBlock.setCommand(command);
                commandBlock.setName(name);
            }
        }
    }

    private static record SignData(String[] lines, boolean glowing) implements BlockEntityData {
        @Override
        public void merge(org.bukkit.block.BlockState blockState) {
            if(blockState instanceof Sign sign) {
                for (int index = 0; index < lines.length; ++index) {
                    sign.setLine(index, lines[index]);
                }
                sign.setGlowingText(glowing);
            }
        }
    }

    public static record BlockEntityContent(Set<BlockEntityData> blockEntityDataSet, Inventory inventory) {
    }

    public static VoxelBlock getVoxelBlock(org.bukkit.block.Block voxel) {
        var b = BukkitVoxelBlock.newInstance(voxel.getState());
        var state = (StateImpl)b.getState();
        var blockEntityDataSet = b.getBlockEntityDataSet();
        var inventory = b.getInventory();
        var entityContent = new BlockEntityContent(blockEntityDataSet, inventory);
        var entity = new EntityImpl(entityContent);
        return new VoxelBlock(b.getBlockId(), state, entity);
    }

    /**
     * Get a new instance from a BlockState.
     * @param blockState a block state.
     * @return a new instance
     */
    public static BukkitVoxelBlock newInstance(org.bukkit.block.BlockState blockState) {
        BlockData blockData = blockState.getBlockData();
        String blockStr = blockData.getAsString();
        VoxelBlock block = BuildVoxSystem.parseBlock(blockStr);
        BukkitVoxelBlock bukkitBlock = new BukkitVoxelBlock(block.getBlockId(), ((StateImpl)block.getState()).getStateMap());
        if(blockState instanceof CommandBlock commandBlock) {
            bukkitBlock.blockEntityDataSet.add(new CommandBlockData(commandBlock.getCommand(), commandBlock.getName()));
        }
        if(blockState instanceof Sign sign) {
            bukkitBlock.blockEntityDataSet.add(new SignData(sign.getLines(), sign.isGlowingText()));
        }
        if(blockState instanceof Container container) {
            bukkitBlock.inventory = container.getSnapshotInventory();
        }
        return bukkitBlock;
    }

    public static void setVoxelBlock(Server server, org.bukkit.block.Block voxel, VoxelBlock block, boolean physics) {
        String blockStr = block.toString();
        BlockData blockData = server.createBlockData(blockStr);
        voxel.setBlockData(blockData, physics);
        org.bukkit.block.BlockState blockState = voxel.getState();
        EntityImpl entity = (EntityImpl) block.getEntity();
        if(entity != null) {
            BukkitVoxelBlock.BlockEntityContent entityContent = (BukkitVoxelBlock.BlockEntityContent) entity.getObj();
            for (var blockEntityData : entityContent.blockEntityDataSet()) {
                blockEntityData.merge(blockState);
            }
            blockState.update();
            var inventory = entityContent.inventory();
            if (blockState instanceof Container container && inventory != null) {
                ItemStack[] contents = inventory.getContents();
                container.getInventory().setContents(contents);
            }
        }else {
            blockState.update();
        }
    }

    void setToWorld(Server server, org.bukkit.block.Block targetBlock, boolean physics) {
        String blockStr = toString();
        BlockData blockData = server.createBlockData(blockStr);
        targetBlock.setBlockData(blockData, physics);
        org.bukkit.block.BlockState blockState = targetBlock.getState();
        for(var blockEntityData : blockEntityDataSet) {
            blockEntityData.merge(blockState);
        }
        blockState.update();
        if(blockState instanceof Container container && inventory != null) {
            ItemStack[] contents = inventory.getContents();
            container.getInventory().setContents(contents);
        }
    }

}
