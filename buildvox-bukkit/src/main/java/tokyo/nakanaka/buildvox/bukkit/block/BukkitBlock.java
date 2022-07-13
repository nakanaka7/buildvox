package tokyo.nakanaka.buildvox.bukkit.block;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.*;

import java.util.Map;

public class BukkitBlock implements Block<StateImpl, EntityImpl> {
    private NamespacedId id;
    private BlockStateTransformer stateTransformer;

    public BukkitBlock(NamespacedId id, BlockStateTransformer stateTransformer) {
        this.id = id;
        this.stateTransformer = stateTransformer;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public StateImpl transformState(StateImpl state, BlockTransformation trans) {
        Map<String, String> transMap = stateTransformer.transform(id, state.getStateMap(), trans);
        return new StateImpl(transMap);
    }

    public StateImpl parseState(String s) {
        return StateImpl.valueOf(s);
    }

}
