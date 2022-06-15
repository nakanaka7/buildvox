package tokyo.nakanaka.buildvox.core.world;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.Block;
import tokyo.nakanaka.buildvox.core.block.BlockTransformation;
import tokyo.nakanaka.buildvox.core.block.StateImpl;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents block.
 */
public class VoxelBlock {
    private NamespacedId blockId;
    private Block.State state;
    private Block.Entity entity;

    public VoxelBlock(NamespacedId blockId, Block.State state, Block.Entity entity) {
        this.blockId = blockId;
        this.state = state;
        this.entity = entity;
    }

    public VoxelBlock(NamespacedId blockId, Block.State state) {
        this(blockId, state, null);
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
        return new VoxelBlock(id, new StateImpl(stateMap));
    }

    public static VoxelBlock valueOfNew(String s) {
        String idStr;
        String stateStr;
        String entityStr;
        if(s.contains("[") || s.contains("{")) {
            int a = s.indexOf("[");
            int b = s.indexOf("{");
            idStr = s.substring(0, Math.min(a, b));
        } else {
            idStr = s;
        }
        s = s.substring(idStr.length());
        if(s.startsWith("[")) {
            int e = s.indexOf("]");
            if(e == -1) throw new IllegalArgumentException();
            stateStr = s.substring(1, e);
            s = s.substring(e + 1);
        }else{
            stateStr = "";
        }
        if(s.startsWith("{")) {
            int e = s.indexOf("}");
            if(e == -1) throw new IllegalArgumentException();
            entityStr = s.substring(1, e);
            s = s.substring(e + 1);
        } else {
            entityStr = "";
        }
        if(!s.isEmpty()) throw new IllegalArgumentException();
        NamespacedId blockId = NamespacedId.valueOf(idStr);
        Block<?,?> block = BuildVoxSystem.getBlockRegistry().get(blockId);
        if(block == null) throw new IllegalArgumentException();
        Block.State state = block.parseState(stateStr);
        Block.Entity entity = null;
        if(!entityStr.isEmpty()) {
            entity = block.parseEntity(entityStr);
        }
        return new VoxelBlock(blockId, state, entity);
    }

    /**
     * Gets the block id.
     * @return the block id.
     */
    public NamespacedId getBlockId() {
        return blockId;
    }

    @Deprecated
    public StateImpl getStateImpl() {
        return (StateImpl) state;
    }

    /** Gets the state */
    public Block.State getState() {
        return state;
    }

    /** Gets the entity */
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
        String blockStr = blockId.toString();
        String stateStr = state.toString();
        if(stateStr.isEmpty()) {
            return blockStr;
        }else {
            return blockStr + "[" + stateStr + "]";
        }
    }

}
