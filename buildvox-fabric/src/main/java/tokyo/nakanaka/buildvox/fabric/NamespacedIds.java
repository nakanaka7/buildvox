package tokyo.nakanaka.buildvox.fabric;

import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import tokyo.nakanaka.buildvox.core.NamespacedId;

public class NamespacedIds {
    private NamespacedIds() {
    }

    /** Creates a namespaced id from Identifier */
    public static NamespacedId createId(Identifier id) {
        return new NamespacedId(id.getNamespace(), id.getPath());
    }

    /** Gets the namespaced id of the world */
    public static NamespacedId getId(World world) {
        var registryKey = world.getRegistryKey();
        var identifier = registryKey.getValue();
        return createId(identifier);
    }

}
