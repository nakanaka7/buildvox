package tokyo.nakanaka.buildVoxCore.command.bvCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildVoxCore.FeedbackMessage;
import tokyo.nakanaka.buildVoxCore.command.EditExit;
import tokyo.nakanaka.buildVoxCore.command.mixin.IntegrityMixin;
import tokyo.nakanaka.buildVoxCore.command.mixin.PosMixin;
import tokyo.nakanaka.buildVoxCore.edit.Clipboard;
import tokyo.nakanaka.buildVoxCore.edit.PlayerEdits;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3d;
import tokyo.nakanaka.buildVoxCore.player.Player;

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
        Player player = bvCmd.getPlayer();
        Vector3d pos = posMixin.calcAbsPos(bvCmd.getExecPos());
        Clipboard clipboard = player.getClipboard();
        if(clipboard == null){
            err.println("Copy first");
            return;
        }
        EditExit exit = PlayerEdits.paste(player, pos, integrityMixin.integrity());
        out.println(FeedbackMessage.ofSetExit(exit));
    }

}
