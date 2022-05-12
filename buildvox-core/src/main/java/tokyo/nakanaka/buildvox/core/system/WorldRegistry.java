package tokyo.nakanaka.buildvox.core.system;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.world.World;

import java.util.HashMap;
import java.util.Map;

public class WorldRegistry {
    private final Map<NamespacedId, World> worldMap = new HashMap<>();

    public void register(World world) {
        worldMap.put(world.getId(), world);
    }

    public void unregister(NamespacedId id) {
        worldMap.remove(id);
    }

    public World get(NamespacedId id) {
        return worldMap.get(id);
    }

}
