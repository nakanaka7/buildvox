package tokyo.nakanaka.buildvox.core.command.bvCommand.affineTransformCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.command.NumberCompletionCandidates;
import tokyo.nakanaka.buildvox.core.command.Pos;
import tokyo.nakanaka.buildvox.core.command.Shape;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.property.Axis;
import tokyo.nakanaka.buildvox.core.selectionShape.MissingPosException;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShape;

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
    @Option(names = {"-s", "--shape"}, completionCandidates = Shape.Candidates.class,
            converter = Shape.Converter.class)
    private SelectionShape shape;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        Vector3d pos = this.pos.toVector3d(bvCmd.getExecPos());
        var options = new PlayerEdits.Options();
        options.shape = shape;
        EditExit editExit;
        try {
            editExit = PlayerEdits.shear(player, axisI, factorJ, factorK, pos, options);
        }catch (MissingPosException | PosArrayLengthException ex) {
            err.println(Messages.SELECTION_NULL_ERROR);
            return;
        }
        out.println(Messages.ofSetExit(editExit));
    }

}
