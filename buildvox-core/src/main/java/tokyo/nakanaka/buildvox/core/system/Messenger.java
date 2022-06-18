package tokyo.nakanaka.buildvox.core.system;

public interface Messenger {
    /**
     * Send a (non-error) message.
     * @param msg the message.
     */
    void sendOutMessage(String msg);

    /**
     * Send an error message.
     * @param msg the message.
     */
    void sendErrMessage(String msg);
}
