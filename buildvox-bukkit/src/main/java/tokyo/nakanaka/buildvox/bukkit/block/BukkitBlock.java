package tokyo.nakanaka.buildvox.bukkit.block;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.Block;
import tokyo.nakanaka.buildvox.core.block.BlockTransformation;

import java.util.Map;

public class BukkitBlock implements Block<BukkitBlockState, BukkitBlockEntity> {
    private NamespacedId id;
    private BukkitBlockStateTransformer stateTransformer;

    public BukkitBlock(NamespacedId id, BukkitBlockStateTransformer stateTransformer) {
        this.id = id;
        this.stateTransformer = stateTransformer;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public BukkitBlockState transformState(BukkitBlockState state, BlockTransformation trans) {
        Map<String, String> transMap = stateTransformer.transform(id, state.getStateMap(), trans);
        return new BukkitBlockState(transMap);
    }

    public BukkitBlockState parseState(String s) {
        return BukkitBlockState.valueOf(s);
    }

}
