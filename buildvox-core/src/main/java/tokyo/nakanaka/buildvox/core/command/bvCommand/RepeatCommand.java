package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.FeedbackMessage;
import tokyo.nakanaka.buildvox.core.command.EditExit;
import tokyo.nakanaka.buildvox.core.command.completionCandidates.IntegerCandidates;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.world.World;

import java.io.PrintWriter;

@CommandLine.Command(name = "repeat", mixinStandardHelpOptions = true,
        description = "Repeat the blocks in the selection."
)
public class RepeatCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @CommandLine.Parameters(description = "The count along x-axis.", completionCandidates = IntegerCandidates.class)
    private int countX;
    @CommandLine.Parameters(description = "The count along y-axis.", completionCandidates = IntegerCandidates.class)
    private int countY;
    @CommandLine.Parameters(description = "The count along z-axis.", completionCandidates = IntegerCandidates.class)
    private int countZ;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        World world = player.getWorld();
        Vector3i[] posData = player.getPosArrayClone();
        if(posData.length != 2) {
            err.println(FeedbackMessage.ofPosArrayLengthError(2));
            return;
        }
        Vector3i pos0 = posData[0];
        Vector3i pos1 = posData[1];
        if(world == null || pos0 == null || pos1 == null) {
            err.println(FeedbackMessage.INCOMPLETE_POS_DATA_ERROR);
            return;
        }
        EditExit exit = PlayerEdits.repeat(player, pos0, pos1, countX, countY, countZ);
        out.println(FeedbackMessage.ofSetExit(exit));
    }

}
