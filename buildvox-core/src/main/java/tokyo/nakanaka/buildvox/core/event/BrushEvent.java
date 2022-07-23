package tokyo.nakanaka.buildvox.core.event;

import tokyo.nakanaka.buildvox.core.World;
import tokyo.nakanaka.buildvox.core.clientWorld.OptionalClientWorld;
import tokyo.nakanaka.buildvox.core.clientWorld.PlayerClientWorld;
import tokyo.nakanaka.buildvox.core.edit.VoxelSpaceEdits;
import tokyo.nakanaka.buildvox.core.edit.WorldEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.RealPlayer;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;

import java.util.HashSet;
import java.util.Set;

public class BrushEvent {
    private BrushEvent() {
    }

    public static void onLeft(RealPlayer player, Vector3i pos) {
        var worldId = player.getPlayerEntity().getWorldId();
        var world = BuildVoxSystem.getWorldRegistry().get(worldId);
        player.setEditWorld(world);
        var src = player.getBrushSource();
        var pcw = new PlayerClientWorld(player);
        var ocw = new OptionalClientWorld(pcw, src.getOptions());
        WorldEdits.paste(src.getClipboard(), ocw, pos.toVector3d());
        pcw.end();
    }

    public static void onRight(RealPlayer player, Vector3i pos) {
        var worldId = player.getPlayerEntity().getWorldId();
        World world = BuildVoxSystem.getWorldRegistry().get(worldId);
        player.setEditWorld(world);
        var src = player.getBrushSource();
        Set<Vector3i> clipPosSet = src.getClipboard().blockPosSet();
        Set<Vector3i> posSet = new HashSet<>();
        for(var p : clipPosSet) {
            posSet.add(p.add(pos));
        }
        var pcw = new PlayerClientWorld(player);
        VoxelSpaceEdits.fill(pcw, posSet, player.getBackgroundBlock());
        pcw.end();
    }

}
