package tokyo.nakanaka.buildvox.bukkit.block;

import tokyo.nakanaka.buildvox.core.ParseUtils;
import tokyo.nakanaka.buildvox.core.block.Block;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BukkitBlockState implements Block.State {
    private Map<String, String> stateMap;

    public BukkitBlockState(Map<String, String> stateMap) {
        this.stateMap = stateMap;
    }

    public Map<String, String> getStateMap() {
        return stateMap;
    }

    public static BukkitBlockState valueOf(String s) {
        Map<String, String> stateMap = ParseUtils.parseStateMap(s);
        return new BukkitBlockState(stateMap);
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
        BukkitBlockState state = (BukkitBlockState) o;
        return stateMap.equals(state.stateMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stateMap);
    }

}
