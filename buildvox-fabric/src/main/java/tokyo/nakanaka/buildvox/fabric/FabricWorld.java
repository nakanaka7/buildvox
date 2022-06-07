package tokyo.nakanaka.buildvox.fabric;

import net.fabricmc.fabric.impl.lookup.entity.EntityApiLookupImpl;
import net.fabricmc.fabric.impl.registry.sync.RemapStateImpl;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.block.BlockImpl;
import tokyo.nakanaka.buildvox.core.block.EntityImpl;
import tokyo.nakanaka.buildvox.core.block.StateImpl;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;
import tokyo.nakanaka.buildvox.core.world.World;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
        if(blockEntity == null) {
            return FabricVoxelBlock.newInstance(blockState);
        }else {
            NbtCompound nbt = blockEntity.createNbtWithId();
            return FabricVoxelBlock.newInstance(blockState, nbt);
        }
    }

    public VoxelBlock getBlockNew(int x, int y, int z) {
        net.minecraft.block.BlockState blockState = original.getBlockState(new BlockPos(x, y, z));
        BlockEntity blockEntity = original.getBlockEntity(new BlockPos(x, y, z));
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
        return new VoxelBlock(block, state, entity);
    }

    @Override
    public void setBlock(int x, int y, int z, VoxelBlock block, boolean physics) {
        String blockStr = block.toString();
        net.minecraft.block.BlockState blockState = Utils.parseBlockState(blockStr);
        if(physics) {
            original.setBlockState(new BlockPos(x, y, z), blockState, net.minecraft.block.Block.NOTIFY_ALL);
        }else{
            stopPhysicsWorlds.add(original);
            original.setBlockState(new BlockPos(x, y, z), blockState, net.minecraft.block.Block.NOTIFY_LISTENERS, 0);
            stopPhysicsWorlds.remove(original);
        }
        if(block instanceof FabricVoxelBlock fabricBlock) {
            NbtCompound nbt = fabricBlock.getNbt();
            if(nbt == null) {
                return;
            }
            BlockEntity blockEntity = BlockEntity.createFromNbt(new BlockPos(x, y, z), blockState, nbt);
            if(blockEntity == null) {
                throw new IllegalArgumentException();
            }
            original.addBlockEntity(blockEntity);
        }
    }

    public void setBlockNew(int x, int y, int z, VoxelBlock block, boolean physics) {

    }

        /**
         * Get the original ServerWorld of this world.
         * @return the original ServerWorld of this world.
         */
    public ServerWorld getOriginal() {
        return original;
    }

}
