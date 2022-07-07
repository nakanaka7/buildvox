package tokyo.nakanaka.buildvox.fabric.block;

import net.minecraft.block.BlockState;
import tokyo.nakanaka.buildvox.core.NamespacedId;
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
        VoxelBlock block = new VoxelBlock(id, new FabricBlockState(state.getStateMap()));
        BlockState blockState = BlockUtils.createBlockState(block);
        BlockState transState = FabricBlockState.transform(blockState, blockTrans);
        Map<String, String> transMap = BlockUtils.createFabricBlockState(transState).getStateMap();
        return new FabricBlockState(transMap);
    }

    public FabricBlockState parseState(String s) {
        return FabricBlockState.valueOf(s);
    }

}
