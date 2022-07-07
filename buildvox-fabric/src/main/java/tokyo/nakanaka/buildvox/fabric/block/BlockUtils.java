package tokyo.nakanaka.buildvox.fabric.block;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.EntityImpl;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static tokyo.nakanaka.buildvox.fabric.NamespacedIds.createId;

public class BlockUtils {
    private BlockUtils() {
    }

    /** Creates a voxel block */
    public static VoxelBlock createVoxelBlock(net.minecraft.block.BlockState blockState, BlockEntity blockEntity) {
        net.minecraft.block.Block block0 = blockState.getBlock();
        Identifier id0 = Registry.BLOCK.getId(block0);
        NamespacedId id = createId(id0);
        FabricBlockState state = createStateImpl(blockState);
        EntityImpl entity = null;
        if(blockEntity != null) {
            entity = createEntityImpl(blockEntity);
        }
        return new VoxelBlock(id, state, entity);
    }

    /** Creates StateImpl */
    public static FabricBlockState createStateImpl(BlockState blockState) {
        Collection<Property<?>> properties0 = blockState.getProperties();
        Map<String, String> stateMap = new HashMap<>();
        for(var key0 : properties0){
            Object value0 = blockState.get(key0);
            stateMap.put(key0.getName().toLowerCase(), value0.toString().toLowerCase());
        }
        return new FabricBlockState(stateMap);
    }

    /** Creates EntityImpl */
    private static EntityImpl createEntityImpl(BlockEntity blockEntity) {
        NbtCompound nbt = blockEntity.createNbtWithId();
        return new EntityImpl(nbt);
    }

    public record StateEntity(BlockState state, BlockEntity entity) {
    }

    /** Creates a BlockStateEntity */
    public static StateEntity createBlockStateEntity(int x, int y, int z, VoxelBlock block) {
        var state = createBlockState(block);
        var entity = createBlockEntity(x, y, z, block, state);
        return new StateEntity(state, entity);
    }

    /**
     * Creates a BlockState
     * @throws IllegalArgumentException if fails to create.
     */
    public static BlockState createBlockState(VoxelBlock block) {
        String s = block.withoutEntity().toString();
        StringReader strReader = new StringReader(s);
        BlockStateArgument blockStateArg;
        try {
            blockStateArg = new BlockStateArgumentType().parse(strReader);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Cannot parse:" + s);
        }
        return blockStateArg.getBlockState();
    }

    /** Creates a BlockEntity */
    private static BlockEntity createBlockEntity(int x, int y, int z, VoxelBlock block, BlockState blockState) {
        EntityImpl entity = (EntityImpl) block.getEntity();
        if(entity == null) {
            return null;
        }
        NbtCompound nbt = (NbtCompound) entity.getObj();
        return BlockEntity.createFromNbt(new BlockPos(x, y, z), blockState, nbt);
    }

}
