package tokyo.nakanaka.buildvox.core.system.commandHandler;

import tokyo.nakanaka.buildvox.core.MessageReceiver;
import tokyo.nakanaka.buildvox.core.NamespacedId;

import java.util.List;
import java.util.UUID;

public interface CommandHandler {

    void onCommand(String[] args, NamespacedId worldId, int x, int y, int z,
                   MessageReceiver messageReceiver, UUID playerId);

    List<String> onTabComplete(String[] args);

}
