package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.command.mixin.PosMixin;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

@CommandLine.Command(name = "pos", mixinStandardHelpOptions = true,
        description = "Set a pos."
)
public class PosCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @CommandLine.Parameters(
            description = "Pos index",
            completionCandidates = posIndexIterable.class)
    private int index;
    @CommandLine.Mixin
    private PosMixin posMixin;

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
        Vector3d pos = posMixin.calcAbsPos(bvCmd.getExecPos());
        int posX = (int)Math.floor(pos.x());
        int posY = (int)Math.floor(pos.y());
        int posZ = (int)Math.floor(pos.z());
        posArray[index] = new Vector3i(posX, posY, posZ);
        player.setPosArray(player.getEditTargetWorld(), posArray);
        out.println(Messages.ofPosExit(index, posX, posY, posZ));
    }

    private static class posIndexIterable implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            return List.of("0", "1", "2", "3").iterator();
        }
    }

}
