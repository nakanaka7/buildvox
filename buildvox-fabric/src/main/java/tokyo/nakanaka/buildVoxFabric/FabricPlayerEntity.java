package tokyo.nakanaka.buildVoxFabric;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3f;
import tokyo.nakanaka.buildVoxCore.PlayerEntity;
import tokyo.nakanaka.buildVoxCore.particleGui.Color;
import tokyo.nakanaka.buildVoxCore.world.World;

/**
 * The implementation of PlayerEntity for Fabric platform
 */
public class FabricPlayerEntity implements PlayerEntity {
    private ServerPlayerEntity original;

    /**
     * Constructs the instance from a ServerPlayerEntity
     * @param original the original player entity of minecraft/fabric.
     */
    public FabricPlayerEntity(ServerPlayerEntity original) {
        this.original = original;
    }

    @Override
    public void givePosMarker() {
        ItemConvertible item = BuildVoxMod.POS_MARKER;
        ItemStack itemStack = new ItemStack(item, 1);
        original.giveItemStack(itemStack);
    }

    /**
     * @throws IllegalArgumentException if world is not the instance of {@link FabricWorld}
     */
    @Override
    public void spawnParticle(Color color, World world, double x, double y, double z) {
        if(!(world instanceof FabricWorld fabricWorld)){
            throw new IllegalArgumentException();
        }
        float red = (float)(color.red() / 256.0);
        float green = (float)(color.green() / 256.0);
        float blue = (float)(color.blue() / 256.0);
        Vec3f color0 = new Vec3f(red, green, blue);
        float scale = 1;
        DustParticleEffect particle = new DustParticleEffect(color0, scale);
        fabricWorld.getOriginal().spawnParticles(original, particle, true, x, y, z, 1, 0, 0, 0, 0);
    }

}
