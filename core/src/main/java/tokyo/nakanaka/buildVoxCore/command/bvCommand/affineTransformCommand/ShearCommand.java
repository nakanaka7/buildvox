package tokyo.nakanaka.buildVoxCore.command.bvCommand.affineTransformCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildVoxCore.FeedbackMessage;
import tokyo.nakanaka.buildVoxCore.command.EditExit;
import tokyo.nakanaka.buildVoxCore.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildVoxCore.command.completionCandidates.DoubleCandidates;
import tokyo.nakanaka.buildVoxCore.command.mixin.PosMixin;
import tokyo.nakanaka.buildVoxCore.edit.PlayerEdits;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3d;
import tokyo.nakanaka.buildVoxCore.player.Player;
import tokyo.nakanaka.buildVoxCore.property.Axis;

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
