package tokyo.nakanaka.buildvox.fabric.block;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;

import static tokyo.nakanaka.buildvox.fabric.NamespacedIds.createId;

public class BlockRegistering {
    private BlockRegistering() {
    }

    public static void registerBlocks() {
        for(Identifier blockId0 : Registry.BLOCK.getIds()) {
            NamespacedId id = createId(blockId0);
            BuildVoxSystem.getBlockRegistry().register(new FabricBlock(id));
        }
    }

}
