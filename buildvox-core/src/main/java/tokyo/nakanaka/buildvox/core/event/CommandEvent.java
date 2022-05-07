package tokyo.nakanaka.buildvox.core.event;

import tokyo.nakanaka.buildvox.core.MessageReceiver;
import tokyo.nakanaka.buildvox.core.NamespacedId;

import java.util.UUID;

public record CommandEvent(String label, String[] args, NamespacedId worldId, int x, int y, int z,
                           MessageReceiver messageReceiver, UUID playerId) {
}
