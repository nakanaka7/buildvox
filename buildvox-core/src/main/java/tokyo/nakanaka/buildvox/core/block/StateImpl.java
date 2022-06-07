package tokyo.nakanaka.buildvox.core.block;

import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

import java.util.Map;

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
        return VoxelBlock.getStateString(stateMap);
    }

}
