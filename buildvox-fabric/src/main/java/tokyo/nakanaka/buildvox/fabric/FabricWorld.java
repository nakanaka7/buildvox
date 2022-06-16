package tokyo.nakanaka.buildvox.fabric;

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
        return BlockUtils.getBlock(blockState, blockEntity);
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
        EntityImpl entity = (EntityImpl) block.getEntity();
        if(entity == null) {
            return;
        }
        NbtCompound nbt = (NbtCompound) entity.getObj();
        BlockEntity blockEntity = BlockEntity.createFromNbt(new BlockPos(x, y, z), blockState, nbt);
        if(blockEntity == null) {
            return;
        }
        original.addBlockEntity(blockEntity);
    }

    /**
     * Get the original ServerWorld of this world.
     * @return the original ServerWorld of this world.
     */
    public ServerWorld getOriginal() {
        return original;
    }

}
