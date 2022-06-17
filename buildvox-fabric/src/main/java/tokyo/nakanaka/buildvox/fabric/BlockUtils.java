package tokyo.nakanaka.buildvox.fabric;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.BlockImpl;
import tokyo.nakanaka.buildvox.core.block.EntityImpl;
import tokyo.nakanaka.buildvox.core.block.StateImpl;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

public class BlockUtils {
    private BlockUtils() {
    }

    public static void registerBlocks() {
        for(Identifier blockId0 : Registry.BLOCK.getIds()) {
            NamespacedId id = new NamespacedId(blockId0.getNamespace(), blockId0.getPath());
            BuildVoxSystem.getBlockRegistry().register(new BlockImpl(id, new FabricBlockStateTransformer()));
        }
    }

    public static VoxelBlock getVoxelBlock(net.minecraft.block.BlockState blockState, BlockEntity blockEntity) {
        net.minecraft.block.Block block0 = blockState.getBlock();
        Identifier id0 = Registry.BLOCK.getId(block0);
        NamespacedId id = new NamespacedId(id0.getNamespace(), id0.getPath());
        BlockImpl block = new BlockImpl(id, new FabricBlockStateTransformer());
        StateImpl state = FabricVoxelBlock.convertToStateImpl(blockState);
        EntityImpl entity = null;
        if(blockEntity != null) {
            NbtCompound nbt = blockEntity.createNbtWithId();
            entity = new EntityImpl(nbt);
        }
        return new VoxelBlock(block.getId(), state, entity);
    }

    public record StateEntity(BlockState state, BlockEntity entity) {
    }

    public static StateEntity getBlockStateEntity(int x, int y, int z, VoxelBlock block) {
        var state = getBlockState(block);
        var entity = createBlockEntity(x, y, z, block, state);
        return new StateEntity(state, entity);
    }

    private static BlockState getBlockState(VoxelBlock block) {
        String blockStr = block.withoutEntity().toString();
        return parseBlockState(blockStr);
    }

    private static BlockEntity createBlockEntity(int x, int y, int z, VoxelBlock block, BlockState blockState) {
        EntityImpl entity = (EntityImpl) block.getEntity();
        if(entity == null) {
            return null;
        }
        NbtCompound nbt = (NbtCompound) entity.getObj();
        return BlockEntity.createFromNbt(new BlockPos(x, y, z), blockState, nbt);
    }

    public static BlockState parseBlockState(String s) {
        StringReader strReader = new StringReader(s);
        BlockStateArgument blockStateArg;
        try {
            blockStateArg = new BlockStateArgumentType().parse(strReader);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Cannot parse:" + s);
        }
        return blockStateArg.getBlockState();
    }

}
