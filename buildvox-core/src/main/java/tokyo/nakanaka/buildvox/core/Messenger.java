package tokyo.nakanaka.buildvox.core;

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
