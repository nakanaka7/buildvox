package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.World;
import tokyo.nakanaka.buildvox.core.command.mixin.Shape;
import tokyo.nakanaka.buildvox.core.command.util.NumberCompletionCandidates;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;

import java.io.PrintWriter;

@Command(name = "repeat", mixinStandardHelpOptions = true,
        description = "Repeat the blocks in the selection."
)
public class RepeatCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private BvCommand bvCmd;
    @Parameters(description = "The count along x-axis.", completionCandidates = NumberCompletionCandidates.Integer.class)
    private int countX;
    @Parameters(description = "The count along y-axis.", completionCandidates = NumberCompletionCandidates.Integer.class)
    private int countY;
    @Parameters(description = "The count along z-axis.", completionCandidates = NumberCompletionCandidates.Integer.class)
    private int countZ;
    @Mixin
    private Shape shape;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getPlayer();
        PlayerEdits.Options options = new PlayerEdits.Options();
        options.shape = shape.shape();
        try {
            EditExit exit = PlayerEdits.repeat(player, countX, countY, countZ, options);
            out.println(Messages.ofSetExit(exit));
        } catch (PlayerEdits.MissingPosException | PosArrayLengthException ex) {
            err.println(Messages.SELECTION_NULL_ERROR);
        }
    }

    public void runOld() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getPlayer();
        World world = player.getEditWorld();
        Vector3i[] posData = player.getPosArrayClone();
        if(posData.length != 2) {
            err.println(Messages.ofPosArrayLengthError(2));
            return;
        }
        Vector3i pos0 = posData[0];
        Vector3i pos1 = posData[1];
        if(world == null || pos0 == null || pos1 == null) {
            err.println(Messages.MISSING_POS_ERROR);
            return;
        }
        EditExit exit = PlayerEdits.repeatOld(player, pos0, pos1, countX, countY, countZ);
        out.println(Messages.ofSetExit(exit));
    }

}
