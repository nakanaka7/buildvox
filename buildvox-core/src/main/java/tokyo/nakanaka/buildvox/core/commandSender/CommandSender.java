package tokyo.nakanaka.buildvox.core.commandSender;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.world.World;

public interface CommandSender {
    void sendOutMessage(String msg);
    void sendErrMessage(String msg);
    World getWorld();
    Vector3i getBlockPos();
}
