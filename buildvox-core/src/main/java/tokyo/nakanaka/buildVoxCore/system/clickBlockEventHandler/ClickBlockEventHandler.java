package tokyo.nakanaka.buildVoxCore.system.clickBlockEventHandler;

import tokyo.nakanaka.buildVoxCore.MessageReceiver;
import tokyo.nakanaka.buildVoxCore.NamespacedId;

import java.util.UUID;

public interface ClickBlockEventHandler {
    void onLeft(UUID playerId, NamespacedId worldId, int x, int y, int z, MessageReceiver messageReceiver);
    void onRight(UUID playerId, NamespacedId worldId, int x, int y, int z, MessageReceiver messageReceiver);
}
