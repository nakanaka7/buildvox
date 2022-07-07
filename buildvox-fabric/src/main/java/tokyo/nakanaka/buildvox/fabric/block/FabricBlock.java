package tokyo.nakanaka.buildvox.fabric.block;

import net.minecraft.block.BlockState;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.Block;
import tokyo.nakanaka.buildvox.core.block.BlockTransformation;

/*
 * internal
 * Block implementation class for Fabric platform.
 */
public class FabricBlock implements Block<FabricBlockState, FabricBlockEntity> {
    private NamespacedId id;

    public FabricBlock(NamespacedId id) {
        this.id = id;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public FabricBlockState transformState(FabricBlockState state, BlockTransformation blockTrans) {
        BlockState transState = state.transform(blockTrans);
        return new FabricBlockState(transState);
    }

    @Override
    public FabricBlockState parseState(String s) {
        String t = id.toString() + "[" + s + "]";
        var u = BlockUtils.parseBlockState(t);
        return new FabricBlockState(u);
    }

}
