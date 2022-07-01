package tokyo.nakanaka.buildvox.bukkit.block;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tokyo.nakanaka.buildvox.bukkit.BuildVoxPlugin;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.BlockImpl;
import tokyo.nakanaka.buildvox.core.block.EntityImpl;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

import java.util.HashSet;
import java.util.Set;

public class BlockUtils {
    private static final Server server = BuildVoxPlugin.getInstance().getServer();

    public interface BlockEntityData {
        void merge(org.bukkit.block.BlockState blockState);
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

    public static void registerBlocks() {
        for(var material : Material.values()){
            if(material.isBlock()) {
                NamespacedKey key = material.getKey();
                NamespacedId id = new NamespacedId(key.getNamespace().toLowerCase(), key.getKey().toLowerCase());
                tokyo.nakanaka.buildvox.core.block.Block block = new BlockImpl(id, new BukkitBlockStateTransformer(server));
                BuildVoxSystem.getBlockRegistry().register(block);
            }
        }
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
        String blockStr = block.withoutEntity().toString();
        BlockData blockData = server.createBlockData(blockStr);
        voxel.setBlockData(blockData, physics);
        org.bukkit.block.BlockState blockState = voxel.getState();
        EntityImpl entity = (EntityImpl) block.getEntity();
        if(entity != null) {
            BlockUtils.BlockEntityContent entityContent = (BlockUtils.BlockEntityContent) entity.getObj();
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
