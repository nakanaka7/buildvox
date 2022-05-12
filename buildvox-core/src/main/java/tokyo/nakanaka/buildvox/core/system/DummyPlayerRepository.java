package tokyo.nakanaka.buildvox.core.system;

import tokyo.nakanaka.buildvox.core.player.DummyPlayer;

import java.util.*;

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

    public List<String> idList() {
        return new ArrayList<>(nameSet());
    }

}
