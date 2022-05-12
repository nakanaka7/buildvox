package tokyo.nakanaka.buildvox.core.system;

import tokyo.nakanaka.buildvox.core.player.DummyPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DummyPlayerRepository {
    private Map<String, DummyPlayer> dummyPlayerMap = new HashMap<>();

    public void create(String name) {
        dummyPlayerMap.put(name, new DummyPlayer(name));
    }

    public DummyPlayer get(String name) {
        return dummyPlayerMap.get(name);
    }

    public void unregister(String name) {
        dummyPlayerMap.remove(name);
    }

    public Set<String> nameSet() {
        return dummyPlayerMap.keySet();
    }

}
