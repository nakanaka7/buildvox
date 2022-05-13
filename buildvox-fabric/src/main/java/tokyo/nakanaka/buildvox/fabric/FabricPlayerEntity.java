package tokyo.nakanaka.buildvox.fabric;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3f;
import tokyo.nakanaka.buildvox.core.PlayerEntity;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.particleGui.Color;
import tokyo.nakanaka.buildvox.core.world.World;

import java.util.UUID;

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
    public UUID getId() {
        return original.getUuid();
    }

    @Override
    public void println(String msg) {
        original.sendMessage(Text.of(msg), false);
    }

    @Override
    public World getWorld() {
        return BuildVoxMod.convertServerWorldToBvWorld(original.getWorld());
    }

    @Override
    public Vector3i getBlockPos() {
        var pos = original.getBlockPos();
        return new Vector3i(pos.getX(), pos.getY(), pos.getZ());
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
