package tokyo.nakanaka.buildvox.core.command.bvCommand.posArray;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

@CommandLine.Command(name = "create", mixinStandardHelpOptions = true,
        description = "Create new pos array with given length."
)
public class CreateCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private PosArrayCommand posArrayCmd;
    @CommandLine.Parameters(description = "length of a new pos array", completionCandidates = LengthCandidates.class)
    private int length;

    private static class LengthCandidates implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            return List.of("2", "3", "4").iterator();
        }
    }

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        if(length < 2 || 4 < length) {
            err.println(Messages.POS_ARRAY_LENGTH_ERROR);
            return;
        }
        BvCommand bvCmd = posArrayCmd.getParentCommand();
        Player player = bvCmd.getTargetPlayer();
        Vector3i[] newPosArray = new Vector3i[length];
        player.setPosArray(newPosArray);
        out.println("Created new pos array with length " + length);
    }

}
