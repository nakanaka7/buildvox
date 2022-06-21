package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.command.EditExit;
import tokyo.nakanaka.buildvox.core.command.mixin.PosMixin;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;

@CommandLine.Command(name = "cut", mixinStandardHelpOptions = true,
        description = "Cut the blocks in the selection.  (posX, posY, posZ) is a position which will be the origin in the clipboard."
)
public class CutCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @CommandLine.Mixin
    private PosMixin posMixin;

    /**
     * Cut the selection. The offset point will be the origin in the clipboard. The operation will be remembered.
     * The pos data and the selection will be cleared.
     */
    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        Vector3d pos = posMixin.calcAbsPos(bvCmd.getExecPos());
        EditExit exit;
        try {
            exit = PlayerEdits.cut(player, pos);
        }catch (PlayerEdits.SelectionNotFoundException ex) {
            err.println(Messages.SELECTION_NULL_ERROR);
            return;
        }
        out.println(Messages.ofCutExit(exit));
    }

}
