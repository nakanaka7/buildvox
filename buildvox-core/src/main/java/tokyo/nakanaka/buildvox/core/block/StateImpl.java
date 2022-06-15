package tokyo.nakanaka.buildvox.core.block;

import java.util.HashMap;
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

    public static StateImpl valueOf(String s) {
        Map<String, String> stateMap = new HashMap<>();
        if(s.isEmpty()) return new StateImpl(stateMap);
        String[] stateArray = s.split(",");
        for(String state : stateArray){
            String[] kv = state.split("=", -1);
            if(kv.length != 2){
                throw new IllegalArgumentException();
            }
            stateMap.put(kv[0], kv[1]);
        }
        return new StateImpl(stateMap);
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
