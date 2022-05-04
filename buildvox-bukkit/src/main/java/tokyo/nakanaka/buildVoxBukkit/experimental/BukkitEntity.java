package tokyo.nakanaka.buildVoxBukkit.experimental;

import tokyo.nakanaka.buildVoxCore.world.Entity;

public class BukkitEntity implements Entity {
    private org.bukkit.entity.Entity original;

    public BukkitEntity(org.bukkit.entity.Entity original) {
        this.original = original;
    }

    public org.bukkit.entity.Entity getOriginal() {
        return original;
    }

}
