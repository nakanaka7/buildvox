package tokyo.nakanaka.buildvox.bukkit.block;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import tokyo.nakanaka.buildvox.bukkit.BuildVoxPlugin;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;

public class BlockUtils {
    private static final Server server = BuildVoxPlugin.getInstance().getServer();

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
