package tokyo.nakanaka.buildvox.core.world;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.blockStateTransformer.BlockTransformation;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;

import java.util.Map;

/* temporary */
@Deprecated
public class BlockImpl implements Block<State> {
    private NamespacedId id;

    public BlockImpl(NamespacedId id) {
        this.id = id;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public State transformState(State state, BlockTransformation trans) {
        var transformer = BuildVoxSystem.environment.blockStateTransformer();
        Map<String, String> transMap = transformer.transform(id, state.getStateMap(), trans);
        return new State(transMap);
    }

}
