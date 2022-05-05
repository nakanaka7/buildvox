package tokyo.nakanaka.buildvox.core.system;

import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WorldRegistry {
    private final Map<NamespacedId, World> worldMap = new HashMap<>();

    public void register(NamespacedId id, World world) {
        worldMap.put(id, world);
    }

    public void unregister(NamespacedId id) {
        worldMap.remove(id);
    }

    public boolean worldIsRegistered(NamespacedId id) {
        return worldMap.containsKey(id);
    }

    public World get(NamespacedId id) {
        return worldMap.get(id);
    }

    public Set<NamespacedId> worldIdSet() {
        return worldMap.keySet();
    }

    @Deprecated
    public Map<NamespacedId, World> getWorldMap() {
        return worldMap;
    }

}
