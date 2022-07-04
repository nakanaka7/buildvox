package tokyo.nakanaka.buildvox.core.command.bvCommand.affineTransformCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.command.mixin.Integrity;
import tokyo.nakanaka.buildvox.core.command.mixin.Masked;
import tokyo.nakanaka.buildvox.core.command.mixin.Pos;
import tokyo.nakanaka.buildvox.core.command.mixin.Shape;
import tokyo.nakanaka.buildvox.core.command.util.NumberCompletionCandidates;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;

import java.io.PrintWriter;

@Command(name = "scale",
        mixinStandardHelpOptions = true,
        description = "Scale the selected blocks about the position (posX, posY, posZ).")
public class ScaleCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private BvCommand bvCmd;
    @Parameters(description = "The scale factor along x-axis.", completionCandidates = NumberCompletionCandidates.Double.class)
    private Double factorX;
    @Parameters(description = "The scale factor along y-axis.", completionCandidates = NumberCompletionCandidates.Double.class)
    private Double factorY;
    @Parameters(description = "The scale factor along z-axis.", completionCandidates = NumberCompletionCandidates.Double.class)
    private Double factorZ;
    @Mixin
    private Pos pos;
    @Mixin
    private Integrity integrity;
    @Mixin
    private Masked masked;
    @Mixin
    private Shape shape;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getPlayer();
        if(factorX * factorY * factorZ == 0) {
            err.println(Messages.SCALE_FACTOR_ERROR);
            return;
        }
        Vector3d pos = this.pos.toVector3d(bvCmd.getExecutionPos());
        var options = new PlayerEdits.Options();
        options.integrity = integrity.integrity();
        options.masked = masked.masked();
        options.shape = shape.shape();
        EditExit editExit;
        try {
            editExit = PlayerEdits.scale(player, factorX, factorY, factorZ, pos, options);
        }catch (PlayerEdits.MissingPosException | PosArrayLengthException ex) {
            err.println(Messages.SELECTION_NULL_ERROR);
            return;
        }
        out.println(Messages.ofSetExit(editExit));
    }

}
