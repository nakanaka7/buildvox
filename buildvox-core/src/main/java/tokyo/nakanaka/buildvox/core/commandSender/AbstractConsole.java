package tokyo.nakanaka.buildvox.core.commandSender;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.world.World;

public abstract class AbstractConsole implements CommandSender {
    private World world;
    private Vector3i blockPos;

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

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public Vector3i getBlockPos() {
        return blockPos;
    }

    public void setBlockPos(Vector3i blockPos) {
        this.blockPos = blockPos;
    }

}
