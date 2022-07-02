package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.command.NumberCompletionCandidates;
import tokyo.nakanaka.buildvox.core.command.SelectionShapeParameter;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.World;
import tokyo.nakanaka.buildvox.core.selectionShape.MissingPosException;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShape;

import java.io.PrintWriter;

@CommandLine.Command(name = "repeat", mixinStandardHelpOptions = true,
        description = "Repeat the blocks in the selection."
)
public class RepeatCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @CommandLine.Parameters(description = "The count along x-axis.", completionCandidates = NumberCompletionCandidates.Integer.class)
    private int countX;
    @CommandLine.Parameters(description = "The count along y-axis.", completionCandidates = NumberCompletionCandidates.Integer.class)
    private int countY;
    @CommandLine.Parameters(description = "The count along z-axis.", completionCandidates = NumberCompletionCandidates.Integer.class)
    private int countZ;
    @CommandLine.Option(names = {"-s", "--shape"}, completionCandidates = SelectionShapeParameter.Candidates.class,
            converter = SelectionShapeParameter.Converter.class)
    private SelectionShape shape;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        PlayerEdits.Options options = new PlayerEdits.Options();
        options.shape = shape;
        try {
            EditExit exit = PlayerEdits.repeat(player, countX, countY, countZ, options);
            out.println(Messages.ofSetExit(exit));
        } catch (MissingPosException | PosArrayLengthException ex) {
            err.println(Messages.SELECTION_NULL_ERROR);
        }
    }

    public void runOld() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        World world = player.getEditWorld();
        Vector3i[] posData = player.getPosArrayClone();
        if(posData.length != 2) {
            err.println(Messages.ofPosArrayLengthError(2));
            return;
        }
        Vector3i pos0 = posData[0];
        Vector3i pos1 = posData[1];
        if(world == null || pos0 == null || pos1 == null) {
            err.println(Messages.INCOMPLETE_POS_DATA_ERROR);
            return;
        }
        EditExit exit = PlayerEdits.repeatOld(player, pos0, pos1, countX, countY, countZ);
        out.println(Messages.ofSetExit(exit));
    }

}
