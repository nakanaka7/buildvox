package tokyo.nakanaka.buildvox.core.block;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.ParseUtils;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;

import java.util.Objects;

/**
 * Represents block.
 */
public class VoxelBlock {
    private final NamespacedId blockId;
    private final Block.State state;
    private final Block.Entity entity;

    public VoxelBlock(NamespacedId blockId, Block.State state, Block.Entity entity) {
        this.blockId = blockId;
        this.state = state;
        this.entity = entity;
    }

    public VoxelBlock(NamespacedId blockId, Block.State state) {
        this(blockId, state, null);
    }

    /**
     * Gets a block instance parsed of the given String. The String must be the form "blockId[key1=value1,key2=value2...]".
     * blockId or block state part may be omitted. blockId must be namespaced id.
     * @throws IllegalArgumentException if the specified String is not the form stated above.
     */
    public static VoxelBlock valueOf(String s) {
        ParseUtils.NameStateEntity nse = ParseUtils.parseNameStateEntity(s);
        String idStr = nse.name();
        String stateStr = nse.state();
        String entityStr = nse.entity();
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

    /** Gets the state */
    public Block.State getState() {
        return state;
    }

    /** Gets the entity */
    public Block.Entity getEntity() {
        return entity;
    }

    /** Gets a new instance without entity */
    public VoxelBlock withoutEntity() {
        return new VoxelBlock(blockId, state);
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

    public String toStringNew() {
        String s = blockId.toString();
        String stateStr = state.toString();
        if(!stateStr.isEmpty()) {
            s = s + "[" + stateStr + "]";
        }
        if(entity != null) {
            s = s + "{" + entity + "}";
        }
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoxelBlock that = (VoxelBlock) o;
        return blockId.equals(that.blockId) && state.equals(that.state) && Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockId, state, entity);
    }

}
