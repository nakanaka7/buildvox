package tokyo.nakanaka.buildvox.core.world;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.Block;
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
    private Block.Entity entity = null;

    /**
     * Constructs a block.
     * @param id the block id
     * @param stateMap the block state map. This map is expressed as [key1=value1,key2=value2...] which follows block
     * in game.
     */
    @Deprecated
    public VoxelBlock(NamespacedId id, Map<String, String> stateMap) {
        this.id = id;
        this.stateMap = stateMap;
    }

    public VoxelBlock(BlockImpl block, StateImpl state, Block.Entity entity) {
        this(block.getId(), state.getStateMap());
        this.entity = entity;
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
            stateMap = parseStateMap(strState);
        }else{
            strId = str;
        }
        NamespacedId id = NamespacedId.valueOf(strId);
        return new VoxelBlock(id, stateMap);
    }

    public static Map<String, String> parseStateMap(String strState) {
        Map<String, String> stateMap = new HashMap<>();
        String[] stateArray = strState.split(",");
        for(String state : stateArray){
            String[] kv = state.split("=", -1);
            if(kv.length != 2){
                throw new IllegalArgumentException();
            }
            stateMap.put(kv[0], kv[1]);
        }
        return stateMap;
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

    public Block.Entity getEntity() {
        return entity;
    }

    public VoxelBlock transform(BlockTransformation trans) {
        BlockImpl block = getBlock();
        StateImpl state = getState();
        StateImpl newState = block.transformState(state, trans);
        return new VoxelBlock(block, newState, entity);
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
            stateStr = "[" + getStateString(stateMap) + "]";
        }else{
            stateStr = "";
        }
        return id.toString() + stateStr;
    }

    public static String getStateString(Map<String, String> stateMap) {
        Set<String> keyValueSet = new HashSet<>();
        for (Map.Entry<String, String> e : stateMap.entrySet()) {
            keyValueSet.add(e.getKey() + "=" + e.getValue());
        }
        return String.join(",", keyValueSet);
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

}
