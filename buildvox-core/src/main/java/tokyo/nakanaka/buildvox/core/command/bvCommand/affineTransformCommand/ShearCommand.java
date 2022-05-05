package tokyo.nakanaka.buildvox.core.command.bvCommand.affineTransformCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.FeedbackMessage;
import tokyo.nakanaka.buildvox.core.command.EditExit;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.command.completionCandidates.DoubleCandidates;
import tokyo.nakanaka.buildvox.core.command.mixin.PosMixin;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.property.Axis;

import java.io.PrintWriter;

@CommandLine.Command(name = "shear",
        mixinStandardHelpOptions = true,
        description = "Shear the selected blocks about the position (posX, posY, posZ).")
public class ShearCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @CommandLine.Parameters(description = "i-axis. i, j, and k are the cyclic set of {x, y, z}")
    private Axis axisI;
    @CommandLine.Parameters(description = "The displacement along j-axis per +1 block along i-axis."
            , completionCandidates = DoubleCandidates.class)
    private Double factorJ;
    @CommandLine.Parameters(description = "The displacement along k-axis per +1 block along i-axis"
            , completionCandidates = DoubleCandidates.class)
    private Double factorK;
    @CommandLine.Mixin
    private PosMixin posMixin;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getPlayer();
        Vector3d pos = posMixin.calcAbsPos(bvCmd.getExecPos());
        EditExit editExit;
        try {
            editExit = PlayerEdits.shear(player, axisI, factorJ, factorK, pos);
        }catch (PlayerEdits.SelectionNotFoundException ex) {
            err.println(FeedbackMessage.SELECTION_NULL_ERROR);
            return;
        }
        out.println(FeedbackMessage.ofSetExit(editExit));
    }

}
