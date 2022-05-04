package tokyo.nakanaka.buildvox.core.system.clickBlockEventHandler;

import tokyo.nakanaka.buildvox.core.MessageReceiver;
import tokyo.nakanaka.buildvox.core.NamespacedId;

import java.util.UUID;

public interface ClickBlockEventHandler {
    void onLeft(UUID playerId, NamespacedId worldId, int x, int y, int z, MessageReceiver messageReceiver);
    void onRight(UUID playerId, NamespacedId worldId, int x, int y, int z, MessageReceiver messageReceiver);
}
