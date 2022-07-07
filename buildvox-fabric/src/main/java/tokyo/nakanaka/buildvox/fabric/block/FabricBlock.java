package tokyo.nakanaka.buildvox.fabric.block;

import net.minecraft.block.BlockState;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.ParseUtils;
import tokyo.nakanaka.buildvox.core.block.Block;
import tokyo.nakanaka.buildvox.core.block.BlockTransformation;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

import java.util.Map;

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
        VoxelBlock block = new VoxelBlock(id, state);
        BlockState blockState = BlockUtils.createBlockState(block);
        BlockState transState = FabricBlockState.transform(blockState, blockTrans);
        return new FabricBlockState(transState);
    }

    public FabricBlockState parseState(String s) {
        Map<String, String> stateMap = ParseUtils.parseStateMap(s);
        return new FabricBlockState(stateMap);
    }

}
