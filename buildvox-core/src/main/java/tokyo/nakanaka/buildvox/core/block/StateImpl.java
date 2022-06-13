package tokyo.nakanaka.buildvox.core.block;

import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* temporary
   The state object of BlockState
 */
@Deprecated
public class StateImpl implements Block.State {
    private Map<String, String> stateMap;

    public StateImpl(Map<String, String> stateMap) {
        this.stateMap = stateMap;
    }

    public Map<String, String> getStateMap() {
        return stateMap;
    }

    @Override
    public String toString() {
        Set<String> keyValueSet = new HashSet<>();
        for (Map.Entry<String, String> e : stateMap.entrySet()) {
            keyValueSet.add(e.getKey() + "=" + e.getValue());
        }
        return String.join(",", keyValueSet);
    }

}
