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
    private static Server server;

    private BukkitVoxelBlock(NamespacedId id, Map<String, String> stateMap) {
        super(id, new StateImpl(stateMap));
    }

    public interface BlockEntityData {
        void merge(org.bukkit.block.BlockState blockState);
    }

    public static void setServer(Server server) {
        BukkitVoxelBlock.server = server;
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
        org.bukkit.block.BlockState blockState = voxel.getState();
        VoxelBlock block = VoxelBlock.valueOf(blockState.getBlockData().getAsString());
        Set<BlockEntityData> blockEntityDataSet = new HashSet<>();
        Inventory inventory = null;
        if(blockState instanceof CommandBlock commandBlock) {
            blockEntityDataSet.add(new CommandBlockData(commandBlock.getCommand(), commandBlock.getName()));
        }
        if(blockState instanceof Sign sign) {
            blockEntityDataSet.add(new SignData(sign.getLines(), sign.isGlowingText()));
        }
        if(blockState instanceof Container container) {
            inventory = container.getSnapshotInventory();
        }
        var entityContent = new BlockEntityContent(blockEntityDataSet, inventory);
        var entity = new EntityImpl(entityContent);
        return new VoxelBlock(block.getBlockId(), block.getState(), entity);
    }

    public static void setVoxelBlock(org.bukkit.block.Block voxel, VoxelBlock block, boolean physics) {
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

}
