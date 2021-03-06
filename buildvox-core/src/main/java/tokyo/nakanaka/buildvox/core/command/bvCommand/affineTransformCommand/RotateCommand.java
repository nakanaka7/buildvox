package tokyo.nakanaka.buildvox.core.command.bvCommand.affineTransformCommand;

import tokyo.nakanaka.buildvox.core.Axis;
import tokyo.nakanaka.buildvox.core.BlockSettingOptions;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.command.mixin.Pos;
import tokyo.nakanaka.buildvox.core.command.mixin.Shape;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import static picocli.CommandLine.*;

@Command(name = "rotate",
        mixinStandardHelpOptions = true,
        description = "Rotate the selected blocks about the position (posX, posY, posZ).")
public class RotateCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private BvCommand bvCmd;
    @Parameters(description = "The coordinate axis which parallels to the rotation axis")
    private Axis axis;
    @Parameters(description = "The angle of rotation by degree", completionCandidates = AngleCandidates.class)
    private Double angle;
    @Mixin
    private Pos pos;
    @Mixin
    private Shape shape;
    @Mixin
    private BlockSettingOptions blockSettingOptions;

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
        Player player = bvCmd.getPlayer();
        Vector3d pos = this.pos.toVector3d(bvCmd.getExecutionPos());
        try {
            EditExit editExit = PlayerEdits.rotate(player, axis, angle, pos, shape.shape(), blockSettingOptions);
            out.println(Messages.ofSetExit(editExit));
        }catch (PlayerEdits.MissingPosException ex) {
            err.println(Messages.MISSING_POS_ERROR);
        }catch (PosArrayLengthException ex) {
            err.println(Messages.ofPosArrayLengthError(ex.getAcceptableLength()));
        }
    }

}
