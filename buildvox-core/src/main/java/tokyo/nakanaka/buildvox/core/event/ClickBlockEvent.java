package tokyo.nakanaka.buildvox.core.event;

import tokyo.nakanaka.buildvox.core.MessageReceiver;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.system.ToolType;

import java.util.UUID;

public record ClickBlockEvent(ToolType tool, ButtonType button, UUID playerId, NamespacedId worldId, int x, int y, int z, MessageReceiver messageReceiver) {
    public enum ButtonType {
        LEFT, RIGHT;
    }
}
