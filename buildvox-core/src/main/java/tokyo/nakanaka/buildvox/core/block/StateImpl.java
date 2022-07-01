package tokyo.nakanaka.buildvox.core.block;

import tokyo.nakanaka.buildvox.core.ParseUtils;

import java.util.*;

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
        Map<String, String> stateMap = ParseUtils.parseStateMap(s);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateImpl state = (StateImpl) o;
        return stateMap.equals(state.stateMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stateMap);
    }

}
