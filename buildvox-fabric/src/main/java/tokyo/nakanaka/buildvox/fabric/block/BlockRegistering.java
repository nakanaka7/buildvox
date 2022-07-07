package tokyo.nakanaka.buildvox.fabric.block;

import net.minecraft.block.StairsBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.Block;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;

import static tokyo.nakanaka.buildvox.fabric.NamespacedIds.createId;

public class BlockRegistering {
    private BlockRegistering() {
    }

    /** Register all the blocks into the BuildVoxSystem block registry . */
    public static void registerBlocks() {
        var registry0 = Registry.BLOCK;
        var registry = BuildVoxSystem.getBlockRegistry();
        for(Identifier id0 : registry0.getIds()) {
            NamespacedId id = createId(id0);
            net.minecraft.block.Block block0 = registry0.get(id0);
            Block<?, ?> block;
            if(block0 instanceof StairsBlock) {
                block = new StairsFabricBlock(id);
            }else {
                block = new FabricBlock(id);
            }
            registry.register(block);
        }
    }

}
