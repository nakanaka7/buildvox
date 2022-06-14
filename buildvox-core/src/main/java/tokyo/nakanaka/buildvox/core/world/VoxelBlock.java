package tokyo.nakanaka.buildvox.core.world;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.Block;
import tokyo.nakanaka.buildvox.core.block.BlockTransformation;
import tokyo.nakanaka.buildvox.core.block.StateImpl;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents block.
 */
public class VoxelBlock {
    private NamespacedId blockId;
    private Block.State state;
    private Block.Entity entity = null;

    /**
     * Constructs a block.
     * @param id the block id
     * @param stateMap the block state map. This map is expressed as [key1=value1,key2=value2...] which follows block
     * in game.
     */
    @Deprecated
    public VoxelBlock(NamespacedId id, Map<String, String> stateMap) {
        this.blockId = id;
        this.state = new StateImpl(stateMap);
    }

    public VoxelBlock(NamespacedId blockId, Block.State state, Block.Entity entity) {
        this.blockId = blockId;
        this.state = state;
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
            stateMap = StateImpl.valueOf(strState).getStateMap();
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
    public NamespacedId getBlockId() {
        return blockId;
    }

    public StateImpl getState() {
        return (StateImpl) state;
    }

    public Block.Entity getEntity() {
        return entity;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public VoxelBlock transform(BlockTransformation trans) {
        Block block = BuildVoxSystem.getBlockRegistry().get(blockId);
        Block.State newState = block.transformState(state, trans);
        return new VoxelBlock(blockId, newState, entity);
    }

    /**
     * Returns a String of the form, blockId[key1=value1,key2=value2,...]. If block state is empty, the state part will
     * be omitted.
     * @return a String of the form, blockId[key1=value1,key2=value2,...].
     */
    @Override
    public String toString() {
        String stateStr;
        if(((StateImpl)state).getStateMap().size() > 0){
            stateStr = "[" + getState().toString() + "]";
        }else{
            stateStr = "";
        }
        return blockId.toString() + stateStr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VoxelBlock)) return false;
        VoxelBlock block = (VoxelBlock) o;
        return blockId.equals(block.blockId) && ((StateImpl)state).getStateMap().equals(block.getState().getStateMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockId, ((StateImpl)state).getStateMap());
    }

}
