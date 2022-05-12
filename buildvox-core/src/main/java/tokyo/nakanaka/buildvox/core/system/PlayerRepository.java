package tokyo.nakanaka.buildvox.core.system;

import tokyo.nakanaka.buildvox.core.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRepository {
    private Map<UUID, Player> playerMap = new HashMap<>();

    public void register(Player player) {
        playerMap.put(player.getId(), player);
    }

    public Player get(UUID id) {
        return playerMap.get(id);
    }

    public Player unregister(UUID id) {
        return playerMap.remove(id);
    }

}
