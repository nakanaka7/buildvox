package tokyo.nakanaka.buildvox.core.commandSender;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.world.World;

/**
 * Represents a console command sender. This command sender has its world and block position.  The initial block
 * position will be (0, 0, 0).
 */
public abstract class AbstractConsole implements CommandSender {
    private World world;
    private Vector3i blockPos;

    /**
     * The initial world should be given.
     */
    @Deprecated
    public AbstractConsole() {
    }

    /**
     * Constructs a console command sender.
     * @param world the initial world of the console command sender.
     */
    public AbstractConsole(World world) {
        this.world = world;
        this.blockPos = Vector3i.ZERO;
    }

    /**
     * Send the message without any modification.
     * @param msg a message.
     */
    public abstract void sendMessage(String msg);

    @Override
    public void sendOutMessage(String msg) {
        sendErrMessage(BuildVoxSystem.getConfig().outColor() + msg);
    }

    @Override
    public void sendErrMessage(String msg) {
        sendOutMessage(BuildVoxSystem.getConfig().errColor() + msg);
    }

    @Override
    public World getWorld() {
        return world;
    }

    /**
     * Set the world.
     * @param world a new world
     */
    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public Vector3i getBlockPos() {
        return blockPos;
    }

    /**
     * Set the block position.
     * @param blockPos a new block position
     */
    public void setBlockPos(Vector3i blockPos) {
        this.blockPos = blockPos;
    }

}
