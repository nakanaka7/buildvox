package tokyo.nakanaka.buildvox.core.system;

import tokyo.nakanaka.buildvox.core.MessageReceiver;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.system.clickBlockEventHandler.ClickBlockEventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClickBlockEventManager {
    private Map<ToolType, ClickBlockEventHandler> handlerMap = new HashMap<>();

    public void register(ToolType toolType, ClickBlockEventHandler handler) {
        handlerMap.put(toolType, handler);
    }

    public void onLeft(ToolType toolType, UUID playerId, NamespacedId worldId, int x, int y, int z, MessageReceiver messageReceiver) {
        ClickBlockEventHandler handler = handlerMap.get(toolType);
        if(handler != null) {
            handler.onLeft(playerId, worldId, x, y, z, messageReceiver);
        }
    }

    public void onRight(ToolType toolType, UUID playerId, NamespacedId worldId, int x, int y, int z, MessageReceiver messageReceiver) {
        ClickBlockEventHandler handler = handlerMap.get(toolType);
        if(handler != null) {
            handler.onRight(playerId, worldId, x, y, z, messageReceiver);
        }
    }

}
