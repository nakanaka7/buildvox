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

import java.io.PrintWriter;

@CommandLine.Command(name = "scale",
        mixinStandardHelpOptions = true,
        description = "Scale the selected blocks about the position (posX, posY, posZ).")
public class ScaleCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @CommandLine.Parameters(description = "The scale factor along x-axis.", completionCandidates = DoubleCandidates.class)
    private Double factorX;
    @CommandLine.Parameters(description = "The scale factor along y-axis.", completionCandidates = DoubleCandidates.class)
    private Double factorY;
    @CommandLine.Parameters(description = "The scale factor along z-axis.", completionCandidates = DoubleCandidates.class)
    private Double factorZ;
    @CommandLine.Mixin
    private PosMixin posMixin;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getPlayer();
        if(factorX * factorY * factorZ == 0) {
            err.println(FeedbackMessage.SCALE_FACTOR_ERROR);
            return;
        }
        Vector3d pos = posMixin.calcAbsPos(bvCmd.getExecPos());
        EditExit editExit;
        try {
            editExit = PlayerEdits.scale(player, factorX, factorY, factorZ, pos);
        }catch (PlayerEdits.SelectionNotFoundException ex) {
            err.println(FeedbackMessage.SELECTION_NULL_ERROR);
            return;
        }
        out.println(FeedbackMessage.ofSetExit(editExit));
    }

}
