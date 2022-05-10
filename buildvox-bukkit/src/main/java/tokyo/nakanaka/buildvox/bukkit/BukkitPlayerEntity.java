package tokyo.nakanaka.buildvox.bukkit;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import tokyo.nakanaka.buildvox.core.ColorCode;
import tokyo.nakanaka.buildvox.core.PlayerEntity;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.particleGui.Color;
import tokyo.nakanaka.buildvox.core.world.World;

import java.util.UUID;

/**
 * The class which implements {@link PlayerEntity} for Bukkit Platform
 */
public class BukkitPlayerEntity implements PlayerEntity {
    private org.bukkit.entity.Player original;

    /**
     * Constructs an instance from a Player
     * @param original a Player
     */
    public BukkitPlayerEntity(Player original) {
        this.original = original;
    }

    @Override
    public UUID getId() {
        return original.getUniqueId();
    }

    @Override
    public void println(String msg) {
        original.sendMessage(msg);
    }

    @Override
    public World getWorld() {
        return BuildVoxPlugin.convertBukkitWorldToBvWorld(original.getWorld());
    }

    @Override
    public Vector3i getBlockPos() {
        var loc = original.getLocation();
        return new Vector3i(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @Override
    public void givePosMarker() {
        var itemStack = new org.bukkit.inventory.ItemStack(Material.STICK, 1);
        itemStack.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 0);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.setDisplayName(ColorCode.AQUA + "Pos Marker");
        itemMeta.setLocalizedName(BuildVoxPlugin.POS_MARKER_LOCALIZED_NAME);
        itemStack.setItemMeta(itemMeta);
        this.original.getInventory().addItem(itemStack);
    }

    @Override
    public void spawnParticle(Color color, World world, double x, double y, double z){
        if(!(world instanceof BukkitWorld bukkitWorld)) {
            return;
        }
        if(!original.getWorld().equals(bukkitWorld.getOriginal())) {
            return;
        }
        var bukkitColor = org.bukkit.Color.fromRGB(color.red(), color.green(), color.blue());
        original.spawnParticle(Particle.REDSTONE, x, y, z, 1, 0, 0, 0, new Particle.DustOptions(bukkitColor, 1));
    }

}
