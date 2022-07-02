package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.command.mixin.Pos;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

@Command(name = "pos", mixinStandardHelpOptions = true,
        description = "Set a pos."
)
public class PosCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private BvCommand bvCmd;
    @Parameters(
            description = "Pos index",
            completionCandidates = posIndexIterable.class)
    private int index;
    @Mixin
    private Pos pos;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        Vector3i[] posArray = player.getPosArrayClone();
        int posDataSize = posArray.length;
        if(index < 0 || posDataSize <= index){
            err.println(Messages.ofPosRangeError(posDataSize));
            return;
        }
        Vector3d pos = this.pos.toVector3d(bvCmd.getExecPos());
        int posX = (int)Math.floor(pos.x());
        int posY = (int)Math.floor(pos.y());
        int posZ = (int)Math.floor(pos.z());
        posArray[index] = new Vector3i(posX, posY, posZ);
        player.setPosArray(posArray);
        out.println(Messages.ofPosExit(index, posX, posY, posZ));
    }

    private static class posIndexIterable implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            return List.of("0", "1", "2", "3").iterator();
        }
    }

}
