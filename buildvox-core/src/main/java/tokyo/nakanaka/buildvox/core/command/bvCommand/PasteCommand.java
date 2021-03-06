package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.BlockSettingOptions;
import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.command.mixin.Pos;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;

@Command(name = "paste", mixinStandardHelpOptions = true,
        description = "Paste the blocks of the clipboard. The block at the origin of the clipboard will be pasted at (posX, posY, posZ) in the world"
)
public class PasteCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private BvCommand bvCmd;
    @Mixin
    private Pos pos;
    @Mixin
    private BlockSettingOptions options;

    /**
     * Paste blocks of the clipboard into the world. The origin of the clipboard matches the offset point.
     * The operation will be remembered. A new (paste) selection will be created.
     */
    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getPlayer();
        Vector3d pos = this.pos.toVector3d(bvCmd.getExecutionPos());
        Clipboard clipboard = player.getClipboard();
        if(clipboard == null){
            err.println("Copy first");
            return;
        }
        EditExit exit = PlayerEdits.paste(player, pos, options);
        out.println(Messages.ofSetExit(exit));
    }

}
