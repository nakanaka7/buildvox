package tokyo.nakanaka.buildVoxCore.system.commandHandler;

import tokyo.nakanaka.buildVoxCore.MessageReceiver;
import tokyo.nakanaka.buildVoxCore.NamespacedId;

import java.util.List;
import java.util.UUID;

public interface CommandHandler {

    void onCommand(String[] args, NamespacedId worldId, int x, int y, int z,
                   MessageReceiver messageReceiver, UUID playerId);

    List<String> onTabComplete(String[] args);

}
