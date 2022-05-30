package tokyo.nakanaka.buildvox.core.commandSender;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.World;

/**
 * A general command sender.
 */
public abstract class PlainCommandSender implements CommandSender {
    private World world;
    private Vector3i blockPos;

    /**
     * Constructs the command sender.
     * @param world the world of the command sender.
     * @param blockPos the block position of the command sender.
     */
    public PlainCommandSender(World world, Vector3i blockPos) {
        this.world = world;
        this.blockPos = blockPos;
    }

    /**
     * Send a message to this command sender. sendOutMessage() and sendErrMessage() methods will be delegated to this method.
     * @param msg the message.
     */
    public abstract void sendMessage(String  msg);

    @Override
    public void sendOutMessage(String msg) {
        sendMessage(msg);
    }

    @Override
    public void sendErrMessage(String msg) {
        sendMessage(msg);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public Vector3i getBlockPos() {
        return blockPos;
    }

}
