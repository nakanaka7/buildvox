package tokyo.nakanaka.buildVoxCore.command.bvCommand.posArray;

import picocli.CommandLine;
import tokyo.nakanaka.buildVoxCore.FeedbackMessage;
import tokyo.nakanaka.buildVoxCore.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3i;
import tokyo.nakanaka.buildVoxCore.player.Player;

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
            err.println(FeedbackMessage.POS_ARRAY_LENGTH_ERROR);
            return;
        }
        BvCommand bvCmd = posArrayCmd.getParentCommand();
        Player player = bvCmd.getPlayer();
        Vector3i[] newPosArray = new Vector3i[length];
        player.setPosArrayWithSelectionNull(player.getWorld(), newPosArray);
        out.println("Created new pos array with length " + length);
    }

}
