package tokyo.nakanaka.buildvox.core.command.bvCommand.affineTransformCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.FeedbackMessage;
import tokyo.nakanaka.buildvox.core.command.EditExit;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.command.mixin.PosMixin;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.property.Axis;

import java.io.PrintWriter;

@CommandLine.Command(name = "reflect",
        mixinStandardHelpOptions = true,
        description = "Reflect the selected blocks about the position (posX, posY, posZ).")
public class ReflectCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @CommandLine.Parameters(description = "The axis which parallels to the reflection direction.")
    private Axis axis;
    @CommandLine.Mixin
    private PosMixin posMixin;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        Vector3d pos = posMixin.calcAbsPos(bvCmd.getExecPos());
        EditExit editExit;
        try {
            editExit = PlayerEdits.reflect(player, axis, pos);
        }catch (PlayerEdits.SelectionNotFoundException ex) {
            err.println(FeedbackMessage.SELECTION_NULL_ERROR);
            return;
        }
        out.println(FeedbackMessage.ofSetExit(editExit));
    }

}
