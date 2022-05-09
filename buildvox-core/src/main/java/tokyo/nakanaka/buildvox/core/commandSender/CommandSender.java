package tokyo.nakanaka.buildvox.core.commandSender;

public interface CommandSender {
    void sendOutMessage(String msg);
    void sendErrMessage(String msg);
}
