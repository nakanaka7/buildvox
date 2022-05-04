package tokyo.nakanaka.buildvox.fabric.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tokyo.nakanaka.buildvox.fabric.FabricWorld;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {
    @Redirect(
        method = "setBlockState"
            + "("
            + "Lnet/minecraft/util/math/BlockPos;"
            + "Lnet/minecraft/block/BlockState;"
            + "Z"
            + ")"
            + "Lnet/minecraft/block/BlockState;",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;"
            + "onBlockAdded"
            + "("
            + "Lnet/minecraft/world/World;"
            + "Lnet/minecraft/util/math/BlockPos;"
            + "Lnet/minecraft/block/BlockState;"
            + "Z"
            + ")"
            + "V"
        )
    )
    private void redirectSetBlockState(BlockState state, World world, BlockPos pos, BlockState blockState, boolean moved) {
        if(world instanceof ServerWorld serverWorld) {
            if (FabricWorld.stopPhysicsWorlds.contains(serverWorld)) {
                return;
            }
        }
        state.onBlockAdded(world, pos, blockState, moved);
    }

}
