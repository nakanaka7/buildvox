package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.command.mixin.Shape;
import tokyo.nakanaka.buildvox.core.command.mixin.Pos;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShape;

import java.io.PrintWriter;

@Command(name = "copy", mixinStandardHelpOptions = true,
         description = "Copy the blocks in the selection. (posX, posY, posZ) is a position which will be the origin in the clipboard."
)
public class CopyCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private BvCommand bvCmd;
    @Mixin
    private Pos pos;
    @Option(names = {"-s", "--shape"}, completionCandidates = Shape.Candidates.class,
    converter = Shape.Converter.class)
    private SelectionShape shape;

    /**
     * Copy the selection. The offset point will be the origin in the clipboard.
     */
    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        Vector3d pos = this.pos.toVector3d(bvCmd.getExecPos());
        var options = new PlayerEdits.Options();
        options.shape = shape;
        try{
            EditExit exit = PlayerEdits.copy(player, pos, options);
            out.println(Messages.ofCopyExit(exit));
        }catch (PlayerEdits.MissingPosException | PosArrayLengthException ex) {
            err.println(Messages.SELECTION_NULL_ERROR);
        }
    }

}
