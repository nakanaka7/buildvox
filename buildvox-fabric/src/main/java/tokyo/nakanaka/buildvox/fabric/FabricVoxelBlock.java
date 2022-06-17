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

}
