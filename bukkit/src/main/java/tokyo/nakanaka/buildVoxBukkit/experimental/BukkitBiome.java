package tokyo.nakanaka.buildVoxBukkit.experimental;

import tokyo.nakanaka.buildVoxCore.world.Biome;

public class BukkitBiome implements Biome {
    private org.bukkit.block.Biome original;

    public BukkitBiome(org.bukkit.block.Biome original) {
        this.original = original;
    }

    public org.bukkit.block.Biome getOriginal() {
        return original;
    }

}
