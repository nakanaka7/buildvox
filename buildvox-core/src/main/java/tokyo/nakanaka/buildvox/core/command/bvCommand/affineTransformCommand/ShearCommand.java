package tokyo.nakanaka.buildvox.core.command.bvCommand.affineTransformCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.Axis;
import tokyo.nakanaka.buildvox.core.BlockSettingOptions;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.command.mixin.Pos;
import tokyo.nakanaka.buildvox.core.command.mixin.Shape;
import tokyo.nakanaka.buildvox.core.command.util.NumberCompletionCandidates;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;

import java.io.PrintWriter;

@Command(name = "shear",
        mixinStandardHelpOptions = true,
        description = "Shear the selected blocks about the position (posX, posY, posZ).")
public class ShearCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private BvCommand bvCmd;
    @Parameters(description = "i-axis. i, j, and k are the cyclic set of {x, y, z}")
    private Axis axisI;
    @Parameters(description = "The displacement along j-axis per +1 block along i-axis."
            , completionCandidates = NumberCompletionCandidates.Double.class)
    private Double factorJ;
    @Parameters(description = "The displacement along k-axis per +1 block along i-axis"
            , completionCandidates = NumberCompletionCandidates.Double.class)
    private Double factorK;
    @Mixin
    private Pos pos;
    @Mixin
    private Shape shape;
    @Mixin
    private BlockSettingOptions blockSettingOptions;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getPlayer();
        Vector3d pos = this.pos.toVector3d(bvCmd.getExecutionPos());
        try {
            EditExit editExit = PlayerEdits.shear(player, axisI, factorJ, factorK, pos, shape.shape(), blockSettingOptions);
            out.println(Messages.ofSetExit(editExit));
        }catch (PlayerEdits.MissingPosException ex) {
            err.println(Messages.MISSING_POS_ERROR);
        }catch (PosArrayLengthException ex) {
            err.println(Messages.ofPosArrayLengthError(ex.getAcceptableLength()));
        }
    }

}
