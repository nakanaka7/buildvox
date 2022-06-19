package tokyo.nakanaka.buildvox.core;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

import java.util.UUID;

public record CommandSource(UUID playerId, NamespacedId worldId, Vector3i pos, Messenger messenger) {
}
