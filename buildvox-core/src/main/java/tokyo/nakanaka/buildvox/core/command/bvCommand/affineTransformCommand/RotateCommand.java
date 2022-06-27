package tokyo.nakanaka.buildvox.core.command.bvCommand.affineTransformCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.edit.EditExit;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.command.mixin.PosMixin;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.property.Axis;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

@CommandLine.Command(name = "rotate",
        mixinStandardHelpOptions = true,
        description = "Rotate the selected blocks about the position (posX, posY, posZ).")
public class RotateCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @CommandLine.Parameters(description = "The coordinate axis which parallels to the rotation axis")
    private Axis axis;
    @CommandLine.Parameters(description = "The angle of rotation by degree", completionCandidates = AngleCandidates.class)
    private Double angle;
    @CommandLine.Mixin
    private PosMixin posMixin;

    private static class AngleCandidates implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            return List.of("-90", "0", "90", "180", "270", "360").iterator();
        }
    }

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        Vector3d pos = posMixin.calcAbsPos(bvCmd.getExecPos());
        EditExit editExit;
        try {
            editExit = PlayerEdits.rotate(player, axis, angle, pos);
        }catch (PlayerEdits.SelectionNotFoundException ex) {
            err.println(Messages.SELECTION_NULL_ERROR);
            return;
        }
        out.println(Messages.ofSetExit(editExit));
    }

}
