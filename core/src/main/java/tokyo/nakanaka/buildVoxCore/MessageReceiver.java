package tokyo.nakanaka.buildVoxCore;

/**
 * An entity to receive messages.
 */
public interface MessageReceiver {
    /**
     * Prints the message as a line.
     * @param msg the message.
     */
    void println(String msg);

}
