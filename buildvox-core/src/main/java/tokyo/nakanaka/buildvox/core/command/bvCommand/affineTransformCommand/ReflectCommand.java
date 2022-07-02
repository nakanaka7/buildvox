package tokyo.nakanaka.buildvox.core.command.bvCommand.affineTransformCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.command.Pos;
import tokyo.nakanaka.buildvox.core.command.SelectionShapeParameter;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.property.Axis;
import tokyo.nakanaka.buildvox.core.selectionShape.MissingPosException;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShape;

import java.io.PrintWriter;

@Command(name = "reflect",
        mixinStandardHelpOptions = true,
        description = "Reflect the selected blocks about the position (posX, posY, posZ).")
public class ReflectCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private BvCommand bvCmd;
    @Parameters(description = "The axis which parallels to the reflection direction.")
    private Axis axis;
    @Mixin
    private Pos pos;
    @Option(names = {"-s", "--shape"}, completionCandidates = SelectionShapeParameter.Candidates.class,
            converter = SelectionShapeParameter.Converter.class)
    private SelectionShape shape;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        Vector3d pos = this.pos.calcAbsPos(bvCmd.getExecPos());
        var options = new PlayerEdits.Options();
        options.shape = shape;
        EditExit editExit;
        try {
            editExit = PlayerEdits.reflect(player, axis, pos, options);
        }catch (MissingPosException | PosArrayLengthException ex) {
            err.println(Messages.SELECTION_NULL_ERROR);
            return;
        }
        out.println(Messages.ofSetExit(editExit));
    }

}
