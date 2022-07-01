package tokyo.nakanaka.buildvox.core.command.bvCommand.affineTransformCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.command.NumberCompletionCandidates;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.command.PosMixin;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;

@CommandLine.Command(name = "scale",
        mixinStandardHelpOptions = true,
        description = "Scale the selected blocks about the position (posX, posY, posZ).")
public class ScaleCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @CommandLine.Parameters(description = "The scale factor along x-axis.", completionCandidates = NumberCompletionCandidates.Double.class)
    private Double factorX;
    @CommandLine.Parameters(description = "The scale factor along y-axis.", completionCandidates = NumberCompletionCandidates.Double.class)
    private Double factorY;
    @CommandLine.Parameters(description = "The scale factor along z-axis.", completionCandidates = NumberCompletionCandidates.Double.class)
    private Double factorZ;
    @CommandLine.Mixin
    private PosMixin posMixin;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        if(factorX * factorY * factorZ == 0) {
            err.println(Messages.SCALE_FACTOR_ERROR);
            return;
        }
        Vector3d pos = posMixin.calcAbsPos(bvCmd.getExecPos());
        EditExit editExit;
        try {
            editExit = PlayerEdits.scale(player, factorX, factorY, factorZ, pos);
        }catch (PlayerEdits.SelectionNotFoundException ex) {
            err.println(Messages.SELECTION_NULL_ERROR);
            return;
        }
        out.println(Messages.ofSetExit(editExit));
    }

}
