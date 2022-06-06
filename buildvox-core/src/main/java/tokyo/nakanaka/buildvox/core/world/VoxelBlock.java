package tokyo.nakanaka.buildvox.core.world;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.BlockImpl;
import tokyo.nakanaka.buildvox.core.block.BlockTransformation;
import tokyo.nakanaka.buildvox.core.block.StateImpl;

import java.util.*;

/**
 * Represents block.
 */
public class VoxelBlock {
    private NamespacedId id;
    private Map<String, String> stateMap;

    /**
     * Constructs a block.
     * @param id the block id
     * @param stateMap the block state map. This map is expressed as [key1=value1,key2=value2...] which follows block
     * in game.
     */
    public VoxelBlock(NamespacedId id, Map<String, String> stateMap) {
        this.id = id;
        this.stateMap = stateMap;
    }

    public VoxelBlock(BlockImpl block, StateImpl state) {
        this(block.getId(), state.getStateMap());
    }

    /**
     * Use BuildVoxSystem.parseBlock().
     * Gets a block instance parsed of the given String. The String must be the form "blockId[key1=value1,key2=value2...]".
     * blockId or block state part may be omitted. blockId must be namespaced id.
     * @throws IllegalArgumentException if the specified String is not the form stated above.
     */
    @Deprecated
    public static VoxelBlock valueOf(String str) {
        String strId;
        Map<String, String> stateMap = new HashMap<>();
        if(str.contains("[")) {
            if(!str.endsWith("]")){
                throw new IllegalArgumentException();
            }
            String[] strIdState = str.substring(0, str.length() - 1).split("\\[");
            strId = strIdState[0];
            String strState = strIdState[1];
            String[] stateArray = strState.split(",");
            for(String state : stateArray){
                String[] kv = state.split("=", -1);
                if(kv.length != 2){
                    throw new IllegalArgumentException();
                }
                stateMap.put(kv[0], kv[1]);
            }
        }else{
            strId = str;
        }
        NamespacedId id = NamespacedId.valueOf(strId);
        return new VoxelBlock(id, stateMap);
    }

    /**
     * Gets the block id.
     * @return the block id.
     */
    public NamespacedId getId() {
        return id;
    }

    public BlockImpl getBlock() {
        return new BlockImpl(id);
    }

    /**
     * Gets the block state map.
     * @return the block state map.
     */
    @Deprecated
    public Map<String, String> getStateMap() {
        return stateMap;
    }

    public StateImpl getState() {
        return new StateImpl(stateMap);
    }

    public VoxelBlock transform(BlockTransformation trans) {
        BlockImpl block = getBlock();
        StateImpl state = getState();
        StateImpl newState = block.transformState(state, trans);
        return new VoxelBlock(block, newState);
    }

    /**
     * Returns a String of the form, blockId[key1=value1,key2=value2,...]. If block state is empty, the state part will
     * be omitted.
     * @return a String of the form, blockId[key1=value1,key2=value2,...].
     */
    @Override
    public String toString() {
        String stateStr;
        if(stateMap.size() > 0){
            Set<String> keyValueSet = new HashSet<>();
            for (Map.Entry<String, String> e : stateMap.entrySet()) {
                keyValueSet.add(e.getKey() + "=" + e.getValue());
            }
            stateStr = "[" + String.join(",", keyValueSet) + "]";
        }else{
            stateStr = "";
        }
        return id.toString() + stateStr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VoxelBlock)) return false;
        VoxelBlock block = (VoxelBlock) o;
        return id.equals(block.id) && stateMap.equals(block.stateMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stateMap);
    }

    /**
     * Gets a new instance with a new state map.
     * @param newStateMap a map for new block state.
     * @return a new instance with a new state map.
     */
    @Deprecated
    public VoxelBlock withStateMap(Map<String, String> newStateMap) {
        return new VoxelBlock(id, newStateMap);
    }

}