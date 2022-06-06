package tokyo.nakanaka.buildvox.core.block;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

import java.util.Map;

/* temporary */
@Deprecated
public class BlockImpl implements Block<StateImpl, Block.EmptyEntity> {
    private NamespacedId id;

    public BlockImpl(NamespacedId id) {
        this.id = id;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public StateImpl transformState(StateImpl state, BlockTransformation trans) {
        var transformer = BuildVoxSystem.environment.blockStateTransformer();
        Map<String, String> transMap = transformer.transform(id, state.getStateMap(), trans);
        return new StateImpl(transMap);
    }

    public StateImpl parseState(String s) {
        return new StateImpl(VoxelBlock.parseStateMap(s));
    }

}
