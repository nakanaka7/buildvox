package tokyo.nakanaka.buildvox.fabric;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.World;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.fabric.block.FabricBlockEntity;
import tokyo.nakanaka.buildvox.fabric.block.FabricBlockState;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static tokyo.nakanaka.buildvox.fabric.NamespacedIds.createId;

/**
 * The implementation of World for Fabric platform. This class uses a mixin class, {@link net.minecraft.world.chunk.WorldChunk}
 * to stop physics on setBlock() method. Hence, for this class being full functionally, the mod have to be effective.
 */
public class FabricWorld implements World {
    private ServerWorld original;
    public static final Set<ServerWorld> stopPhysicsWorlds = new HashSet<>();



    @Override
    public NamespacedId getId() {
        Identifier id = original.getRegistryKey().getValue();
        return new NamespacedId(id.getNamespace(), id.getPath());
    }

    /**
     * Constructs a world from a ServerWorld
     * @param original the original world of minecraft/fabric
     */
    public FabricWorld(ServerWorld original) {
        this.original = original;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FabricWorld that = (FabricWorld) o;
        return original.equals(that.original);
    }

    @Override
    public int hashCode() {
        return Objects.hash(original);
    }

    @Override
    public VoxelBlock getBlock(int x, int y, int z) {
        net.minecraft.block.BlockState blockState = original.getBlockState(new BlockPos(x, y, z));
        BlockEntity blockEntity = original.getBlockEntity(new BlockPos(x, y, z));
        return createVoxelBlock(blockState, blockEntity);
    }

    /** Creates a voxel block */
    private static VoxelBlock createVoxelBlock(BlockState blockState, BlockEntity blockEntity) {
        net.minecraft.block.Block block0 = blockState.getBlock();
        Identifier id0 = Registry.BLOCK.getId(block0);
        NamespacedId id = createId(id0);
        FabricBlockState state = createFabricBlockState(blockState);
        FabricBlockEntity entity = null;
        if(blockEntity != null) {
            entity = createFabricBlockEntity(blockEntity);
        }
        return new VoxelBlock(id, state, entity);
    }

    /** Creates FabricBlockState */
    private static FabricBlockState createFabricBlockState(BlockState blockState) {
        return new FabricBlockState(blockState);
    }



    /** Creates a FabricBlockEntity. */
    private static FabricBlockEntity createFabricBlockEntity(BlockEntity blockEntity) {
        NbtCompound nbt = blockEntity.createNbtWithId();
        return new FabricBlockEntity(nbt);
    }

    @Override
    public void setBlock(int x, int y, int z, VoxelBlock block, boolean physics) {
        var stateEntity = createBlockStateEntity(x, y, z, block);
        var state = stateEntity.state();
        if(physics) {
            original.setBlockState(new BlockPos(x, y, z), state, net.minecraft.block.Block.NOTIFY_ALL);
        }else{
            stopPhysicsWorlds.add(original);
            original.setBlockState(new BlockPos(x, y, z), state, net.minecraft.block.Block.NOTIFY_LISTENERS, 0);
            stopPhysicsWorlds.remove(original);
        }
        BlockEntity entity = stateEntity.entity();
        if(entity != null) {
            original.addBlockEntity(entity);
        }
    }

    public record StateEntity(BlockState state, BlockEntity entity) {
    }

    /** Creates a BlockStateEntity */
    private static StateEntity createBlockStateEntity(int x, int y, int z, VoxelBlock block) {
        var state = createBlockState(block);
        BlockEntity entity = null;
        FabricBlockEntity fbe = (FabricBlockEntity) block.getEntity();
        if(fbe != null) {
            entity = createBlockEntity(fbe, x, y, z, state);
        }
        return new StateEntity(state, entity);
    }

    /**
     * Creates a BlockState
     */
    private static BlockState createBlockState(VoxelBlock block) {
        return ((FabricBlockState)block.getState()).getBlockState();
    }

    /** Creates a BlockEntity */
    private static BlockEntity createBlockEntity(FabricBlockEntity fbe, int x, int y, int z, BlockState state) {
        NbtCompound nbt = fbe.getNbt();
        return BlockEntity.createFromNbt(new BlockPos(x, y, z), state, nbt);
    }

    /**
     * Get the original ServerWorld of this world.
     * @return the original ServerWorld of this world.
     */
    public ServerWorld getOriginal() {
        return original;
    }

}
