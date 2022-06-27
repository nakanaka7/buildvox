package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.edit.EditExit;
import tokyo.nakanaka.buildvox.core.command.mixin.IntegrityMixin;
import tokyo.nakanaka.buildvox.core.command.mixin.PosMixin;
import tokyo.nakanaka.buildvox.core.edit.Clipboard;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;

@CommandLine.Command(name = "paste", mixinStandardHelpOptions = true,
        description = "Paste the blocks of the clipboard. The block at the origin of the clipboard will be pasted at (posX, posY, posZ) in the world"
)
public class PasteCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @CommandLine.Mixin
    private IntegrityMixin integrityMixin;
    @CommandLine.Mixin
    private PosMixin posMixin;
    @CommandLine.Option(names = {"-m", "--masked"})
    private boolean masked;

    /**
     * Paste blocks of the clipboard into the world. The origin of the clipboard matches the offset point.
     * The operation will be remembered. A new (paste) selection will be created.
     */
    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        try{
            integrityMixin.checkValue();
        }catch (IllegalStateException ex) {
            err.println(ex.getMessage());
            return;
        }
        Player player = bvCmd.getTargetPlayer();
        Vector3d pos = posMixin.calcAbsPos(bvCmd.getExecPos());
        Clipboard clipboard = player.getClipboard();
        if(clipboard == null){
            err.println("Copy first");
            return;
        }
        EditExit exit = PlayerEdits.paste(player, pos, integrityMixin.integrity(), masked);
        out.println(Messages.ofSetExit(exit));
    }

}
