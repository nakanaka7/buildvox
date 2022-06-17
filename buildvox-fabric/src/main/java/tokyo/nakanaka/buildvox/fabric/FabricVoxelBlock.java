package tokyo.nakanaka.buildvox.fabric;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.StateImpl;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The special class which extends {@link VoxelBlock} class for Fabric platform. This class can store nbt of a block entity.
 */
public class FabricVoxelBlock extends VoxelBlock {

    private FabricVoxelBlock(NamespacedId id, Map<String, String> stateMap) {
        super(id, new StateImpl(stateMap));
    }

    /**
     * Get a new instance from a BlockState.
     * @param blockState a block state.
     * @return an instance from a BlockState.
     */
    public static VoxelBlock getVoxelBlock(net.minecraft.block.BlockState blockState) {
        net.minecraft.block.Block block0 = blockState.getBlock();
        Identifier id0 = Registry.BLOCK.getId(block0);
        NamespacedId id = new NamespacedId(id0.getNamespace(), id0.getPath());
        var stateMap = convertToStateImpl(blockState).getStateMap();
        return new VoxelBlock(id, new StateImpl(stateMap));
    }

    public static StateImpl convertToStateImpl(BlockState blockState) {
        Collection<Property<?>> properties0 = blockState.getProperties();
        Map<String, String> stateMap = new HashMap<>();
        for(var key0 : properties0){
            Object value0 = blockState.get(key0);
            stateMap.put(key0.getName().toLowerCase(), value0.toString().toLowerCase());
        }
        return new StateImpl(stateMap);
    }

}
