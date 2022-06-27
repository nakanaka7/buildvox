package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.edit.EditExit;
import tokyo.nakanaka.buildvox.core.command.mixin.PosMixin;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;

@CommandLine.Command(name = "copy", mixinStandardHelpOptions = true,
         description = "Copy the blocks in the selection. (posX, posY, posZ) is a position which will be the origin in the clipboard."
)
public class CopyCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @CommandLine.Mixin
    private PosMixin posMixin;

    /**
     * Copy the selection. The offset point will be the origin in the clipboard.
     */
    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        Vector3d pos = posMixin.calcAbsPos(bvCmd.getExecPos());
        EditExit exit;
        try{
            exit = PlayerEdits.copy(player, pos);
        }catch (PlayerEdits.SelectionNotFoundException ex) {
            err.println(Messages.SELECTION_NULL_ERROR);
            return;
        }
        out.println(Messages.ofCopyExit(exit));
    }

}
