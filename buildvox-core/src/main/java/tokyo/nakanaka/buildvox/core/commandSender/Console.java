package tokyo.nakanaka.buildvox.core.commandSender;

import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;

public abstract class Console implements CommandSender {
    public abstract void sendMessage(String msg);

    @Override
    public void sendOutMessage(String msg) {
        sendErrMessage(BuildVoxSystem.getConfig().outColor() + msg);
    }

    @Override
    public void sendErrMessage(String msg) {
        sendOutMessage(BuildVoxSystem.getConfig().errColor() + msg);
    }

}
