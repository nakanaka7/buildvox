package tokyo.nakanaka.buildvox.core.commandSender;

public abstract class CommandBlockCommandSender implements CommandSender {
    public abstract void sendMessage(String  msg);

    @Override
    public void sendOutMessage(String msg) {
        sendMessage(msg);
    }

    @Override
    public void sendErrMessage(String msg) {
        sendMessage(msg);
    }

}
