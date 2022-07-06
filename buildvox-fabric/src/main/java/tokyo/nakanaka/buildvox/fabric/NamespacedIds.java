package tokyo.nakanaka.buildvox.fabric;

import net.minecraft.util.Identifier;
import tokyo.nakanaka.buildvox.core.NamespacedId;

public class NamespacedIds {
    private NamespacedIds() {
    }

    /** Creates a namespaced id from Identifier */
    static NamespacedId createId(Identifier id) {
        return new NamespacedId(id.getNamespace(), id.getPath());
    }

}
