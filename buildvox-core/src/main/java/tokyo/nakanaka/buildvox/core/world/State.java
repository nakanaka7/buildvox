package tokyo.nakanaka.buildvox.core.world;

import java.util.Map;

/* temporary
   The state object of BlockState
 */
@Deprecated
public class State {
    private Map<String, String> stateMap;

    public State(Map<String, String> stateMap) {
        this.stateMap = stateMap;
    }

    public Map<String, String> getStateMap() {
        return stateMap;
    }

}
