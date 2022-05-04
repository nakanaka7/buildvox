package tokyo.nakanaka.buildVoxFabric;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import tokyo.nakanaka.buildVoxCore.world.Block;
import tokyo.nakanaka.buildVoxCore.world.World;

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
    public Block getBlock(int x, int y, int z) {
        BlockState blockState = original.getBlockState(new BlockPos(x, y, z));
        BlockEntity blockEntity = original.getBlockEntity(new BlockPos(x, y, z));
        if(blockEntity == null) {
            return FabricBlock.newInstance(blockState);
        }else {
            NbtCompound nbt = blockEntity.createNbtWithId();
            return FabricBlock.newInstance(blockState, nbt);
        }
    }
    
    @Override
    public void setBlock(int x, int y, int z, Block block, boolean physics) {
        String blockStr = block.toString();
        BlockState blockState = Utils.parseBlockState(blockStr);
        if(physics) {
            original.setBlockState(new BlockPos(x, y, z), blockState, net.minecraft.block.Block.NOTIFY_ALL);
        }else{
            stopPhysicsWorlds.add(original);
            original.setBlockState(new BlockPos(x, y, z), blockState, net.minecraft.block.Block.NOTIFY_LISTENERS, 0);
            stopPhysicsWorlds.remove(original);
        }
        if(block instanceof FabricBlock fabricBlock) {
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

    /**
     * Get the original ServerWorld of this world.
     * @return the original ServerWorld of this world.
     */
    public ServerWorld getOriginal() {
        return original;
    }

}
