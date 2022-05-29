package tokyo.nakanaka.buildvox.core.commandSender;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.World;

/**
 * Represents a command sender.
 */
public interface CommandSender {
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

    /**
     * Get the world of the command sender.
     * @return the world
     */
    World getWorld();

    /**
     * Get the block position of the command sender.
     * @return the block position
     */
    Vector3i getBlockPos();
}
