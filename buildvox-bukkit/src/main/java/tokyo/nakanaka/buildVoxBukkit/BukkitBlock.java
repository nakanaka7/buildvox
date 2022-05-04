package tokyo.nakanaka.buildVoxBukkit;

import org.bukkit.Server;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tokyo.nakanaka.buildVoxCore.NamespacedId;
import tokyo.nakanaka.buildVoxCore.world.Block;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class which extends {@link Block} for Bukkit Platform
 */
public class BukkitBlock extends Block {
    private Set<BlockEntityData> blockEntityDataSet = new HashSet<>();
    private Inventory inventory;

    private BukkitBlock(NamespacedId id, Map<String, String> stateMap) {
        super(id, stateMap);
    }

    private interface BlockEntityData {
        void merge(BlockState blockState);
    }

    private static record CommandBlockData(String command, String name) implements BlockEntityData {
        public void merge(BlockState blockState){
            if(blockState instanceof CommandBlock commandBlock) {
                commandBlock.setCommand(command);
                commandBlock.setName(name);
            }
        }
    }

    private static record SignData(String[] lines, boolean glowing) implements BlockEntityData {
        @Override
        public void merge(BlockState blockState) {
            if(blockState instanceof Sign sign) {
                for (int index = 0; index < lines.length; ++index) {
                    sign.setLine(index, lines[index]);
                }
                sign.setGlowingText(glowing);
            }
        }
    }

    /**
     * Get a new instance from a BlockState.
     * @param blockState a block state.
     * @return a new instance
     */
    public static BukkitBlock newInstance(BlockState blockState) {
        BlockData blockData = blockState.getBlockData();
        String blockStr = blockData.getAsString();
        Block block = Block.valueOf(blockStr);
        BukkitBlock bukkitBlock = new BukkitBlock(block.getId(), block.getStateMap());
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

    void setToWorld(Server server, org.bukkit.block.Block targetBlock, boolean physics) {
        String blockStr = toString();
        BlockData blockData = server.createBlockData(blockStr);
        targetBlock.setBlockData(blockData, physics);
        BlockState blockState = targetBlock.getState();
        for(var blockEntityData : blockEntityDataSet) {
            blockEntityData.merge(blockState);
        }
        blockState.update();
        if(blockState instanceof Container container && inventory != null) {
            ItemStack[] contents = inventory.getContents();
            container.getInventory().setContents(contents);
        }
    }

    @Override
    public BukkitBlock withStateMap(Map<String, String> stateMap) {
        BukkitBlock newBlock = new BukkitBlock(super.getId(), stateMap);
        newBlock.inventory = this.inventory;
        newBlock.blockEntityDataSet = new HashSet<>(blockEntityDataSet);
        return newBlock;
    }

}
