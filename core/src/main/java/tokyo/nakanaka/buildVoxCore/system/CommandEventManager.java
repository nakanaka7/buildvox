package tokyo.nakanaka.buildVoxCore.system;

import tokyo.nakanaka.buildVoxCore.MessageReceiver;
import tokyo.nakanaka.buildVoxCore.NamespacedId;
import tokyo.nakanaka.buildVoxCore.system.commandHandler.CommandHandler;

import java.util.*;

public class CommandEventManager {
    private Map<String, CommandHandler> commandHandlerMap = new HashMap<>();

    public void register(String label, CommandHandler commandHandler) {
        commandHandlerMap.put(label, commandHandler);
    }

    @SuppressWarnings("unused")
    public void onCommand(String label, String[] args, NamespacedId worldId, int x, int y, int z,
                          MessageReceiver messageReceiver, UUID playerId) {
        CommandHandler cmdHandler = commandHandlerMap.get(label);
        if(cmdHandler == null) {
            messageReceiver.println("Unsupported command");
            return;
        }
        cmdHandler.onCommand(args, worldId, x, y, z, messageReceiver, playerId);
    }

    @SuppressWarnings("unused")
    public List<String> onTabComplete(String label, String[] args) {
        CommandHandler cmdHandler = commandHandlerMap.get(label);
        if(cmdHandler == null) {
            return new ArrayList<>();
        }
        return cmdHandler.onTabComplete(args);
    }

}
