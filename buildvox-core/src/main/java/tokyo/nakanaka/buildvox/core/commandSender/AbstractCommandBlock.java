package tokyo.nakanaka.buildvox.core.commandSender;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.World;
@Deprecated
/**
 * Use PlainCommandSender
 */
public abstract class AbstractCommandBlock implements CommandSender {
    private World world;
    private Vector3i blockPos;

    public AbstractCommandBlock(World world, Vector3i blockPos) {
        this.world = world;
        this.blockPos = blockPos;
    }

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
