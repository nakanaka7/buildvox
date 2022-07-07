package tokyo.nakanaka.buildvox.fabric.block;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import tokyo.nakanaka.buildvox.core.ParseUtils;
import tokyo.nakanaka.buildvox.core.block.Block;

import java.util.*;

/* internal */
public class FabricBlockState implements Block.State {
    private Map<String, String> stateMap;

    public FabricBlockState(Map<String, String> stateMap) {
        this.stateMap = stateMap;
    }

    public FabricBlockState(BlockState blockState) {
        Collection<Property<?>> properties0 = blockState.getProperties();
        Map<String, String> stateMap = new HashMap<>();
        for(var key0 : properties0){
            Object value0 = blockState.get(key0);
            stateMap.put(key0.getName().toLowerCase(), value0.toString().toLowerCase());
        }
        this.stateMap = stateMap;
    }

    public Map<String, String> getStateMap() {
        return stateMap;
    }

    public static FabricBlockState valueOf(String s) {
        Map<String, String> stateMap = ParseUtils.parseStateMap(s);
        return new FabricBlockState(stateMap);
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
        FabricBlockState state = (FabricBlockState) o;
        return stateMap.equals(state.stateMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stateMap);
    }

}
