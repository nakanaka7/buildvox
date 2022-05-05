package tokyo.nakanaka.buildvox.bukkit.experimental;

import tokyo.nakanaka.buildvox.core.world.Entity;

public class BukkitEntity implements Entity {
    private org.bukkit.entity.Entity original;

    public BukkitEntity(org.bukkit.entity.Entity original) {
        this.original = original;
    }

    public org.bukkit.entity.Entity getOriginal() {
        return original;
    }

}
