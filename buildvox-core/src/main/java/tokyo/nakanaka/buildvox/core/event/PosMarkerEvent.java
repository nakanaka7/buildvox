package tokyo.nakanaka.buildvox.core.event;

import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.World;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.RealPlayer;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;

public class PosMarkerEvent {
    private PosMarkerEvent() {
    }

    public static void onLeft(RealPlayer player, Vector3i pos) {
        var worldId = player.getPlayerEntity().getWorldId();
        var world = BuildVoxSystem.getWorldRegistry().get(worldId);
        player.setEditWorld(world);
        Vector3i[] posArray = new Vector3i[player.getPosArrayClone().length];
        posArray[0] = pos;
        player.setPosArray(posArray);
        player.getMessenger().sendOutMessage(Messages.ofPosExit(0, pos.x(), pos.y(), pos.z()));
    }

    public static void onRight(RealPlayer player, Vector3i pos) {
        var worldId = player.getPlayerEntity().getWorldId();
        var world = BuildVoxSystem.getWorldRegistry().get(worldId);
        World editWorld = player.getEditWorld();
        Vector3i[] posArray = player.getPosArrayClone();
        if (world != editWorld) {
            posArray = new Vector3i[player.getPosArrayClone().length];
        }
        int l = posArray.length;
        int index = l - 1;
        for (int i = 0; i < l; ++i) {
            if (posArray[i] == null) {
                index = i;
                break;
            }
        }
        posArray[index] = pos;
        player.setEditWorld(world);
        player.setPosArray(posArray);
        player.getMessenger().sendOutMessage(Messages.ofPosExit(index, pos.x(), pos.y(), pos.z()));
    }

}
