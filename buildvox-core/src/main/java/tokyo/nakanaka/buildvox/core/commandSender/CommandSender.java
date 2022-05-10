package tokyo.nakanaka.buildvox.core.commandSender;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.World;

public interface CommandSender {
    void sendOutMessage(String msg);
    void sendErrMessage(String msg);
    default World getWorld() {
        return null;
    }
    default Vector3i getBlockPos() {
        return new Vector3i(0, 0, 0);
    }
}
