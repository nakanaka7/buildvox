package tokyo.nakanaka.buildvox.fabric;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tokyo.nakanaka.buildVoxCore.NamespacedId;
import tokyo.nakanaka.buildVoxCore.world.Block;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The special class which extends {@link Block} class for Fabric platform. This class can store nbt of a block entity.
 */
public class FabricBlock extends Block {
    private NbtCompound nbt;

    private FabricBlock(NamespacedId id, Map<String, String> stateMap, NbtCompound nbt) {
        super(id, stateMap);
        this.nbt = nbt;
    }

    /**
     * Get a new instance from a BlockState.
     * @param blockState a block state.
     * @return an instance from a BlockState.
     */
    public static FabricBlock newInstance(BlockState blockState) {
        return FabricBlock.newInstance(blockState, null);
    }

    /**
     * Get a new instance from a BlockState and nbt of a block entity.
     * @param blockState a block state.
     * @param nbt the nbt of a block entity.
     * @return an instance from a BlockState.
     */
    public static FabricBlock newInstance(BlockState blockState, NbtCompound nbt) {
        net.minecraft.block.Block block0 = blockState.getBlock();
        Identifier id0 = Registry.BLOCK.getId(block0);
        NamespacedId id = new NamespacedId(id0.getNamespace(), id0.getPath());
        Collection<Property<?>> properties0 = blockState.getProperties();
        Map<String, String> stateMap = new HashMap<>();
        for(var key0 : properties0){
            Object value0 = blockState.get(key0);
            stateMap.put(key0.getName().toLowerCase(), value0.toString().toLowerCase());
        }
        return new FabricBlock(id, stateMap, nbt);
    }

    /**
     * Gets nbt of the block entity. If this block is not a block entity, return null.
     * @return nbt of the block entity.
     */
    public NbtCompound getNbt() {
        return nbt;
    }

    @Override
    public FabricBlock withStateMap(Map<String, String> stateMap){
        return new FabricBlock(super.getId(), stateMap, nbt);
    }

}
