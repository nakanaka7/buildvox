package tokyo.nakanaka.buildvox.bukkit.block;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Sign;
import tokyo.nakanaka.buildvox.bukkit.BuildVoxPlugin;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;

public class BlockUtils {
    private static final Server server = BuildVoxPlugin.getInstance().getServer();

    public interface BlockEntityData {
        void merge(org.bukkit.block.BlockState blockState);
    }

    public static record CommandBlockData(String command, String name) implements BlockUtils.BlockEntityData {
        public void merge(org.bukkit.block.BlockState blockState){
            if(blockState instanceof CommandBlock commandBlock) {
                commandBlock.setCommand(command);
                commandBlock.setName(name);
            }
        }
    }

    public static record SignData(String[] lines, boolean glowing) implements BlockUtils.BlockEntityData {
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

    public static void registerBlocks() {
        for(var material : Material.values()){
            if(material.isBlock()) {
                NamespacedKey key = material.getKey();
                NamespacedId id = new NamespacedId(key.getNamespace().toLowerCase(), key.getKey().toLowerCase());
                tokyo.nakanaka.buildvox.core.block.Block block = new BukkitBlock(id, new BukkitBlockStateTransformer(server));
                BuildVoxSystem.getBlockRegistry().register(block);
            }
        }
    }

}
