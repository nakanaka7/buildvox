package tokyo.nakanaka.buildvox.core.block;

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

}
