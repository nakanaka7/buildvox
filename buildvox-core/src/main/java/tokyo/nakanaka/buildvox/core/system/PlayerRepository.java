package tokyo.nakanaka.buildvox.core.system;

import tokyo.nakanaka.buildvox.core.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerRepository {
    private Map<UUID, Player> playerMap = new HashMap<>();

    public void register(Player player) {
        playerMap.put(player.getId(), player);
    }

    public Player get(UUID id) {
        return playerMap.get(id);
    }

    public void unregister(UUID id) {
        Player player = playerMap.remove(id);
        BuildVoxSystem.PARTICLE_GUI_REPOSITORY.delete(player);
    }

}
