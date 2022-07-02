package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.command.mixin.Pos;
import tokyo.nakanaka.buildvox.core.command.SelectionShapeParameter;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selectionShape.MissingPosException;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShape;

import java.io.PrintWriter;

@Command(name = "cut", mixinStandardHelpOptions = true,
        description = "Cut the blocks in the selection.  (posX, posY, posZ) is a position which will be the origin in the clipboard."
)
public class CutCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private BvCommand bvCmd;
    @Mixin
    private Pos pos;
    @Option(names = {"-s", "--shape"}, completionCandidates = SelectionShapeParameter.Candidates.class,
            converter = SelectionShapeParameter.Converter.class)
    private SelectionShape shape;

    /**
     * Cut the selection. The offset point will be the origin in the clipboard. The operation will be remembered.
     * The pos data and the selection will be cleared.
     */
    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        Vector3d pos = this.pos.toVector3d(bvCmd.getExecPos());
        var options = new PlayerEdits.Options();
        options.shape = shape;
        EditExit exit;
        try {
            exit = PlayerEdits.cut(player, pos, options);
        }catch (MissingPosException | PosArrayLengthException ex) {
            err.println(Messages.SELECTION_NULL_ERROR);
            return;
        }
        out.println(Messages.ofCutExit(exit));
    }

}
