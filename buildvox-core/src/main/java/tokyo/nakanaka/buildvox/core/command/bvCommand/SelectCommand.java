package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.command.mixin.Integrity;
import tokyo.nakanaka.buildvox.core.command.mixin.Masked;
import tokyo.nakanaka.buildvox.core.command.mixin.Shape;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;

import java.io.PrintWriter;

@Command(name = "select", description = "Creates a new selection from existing selection or pos-array. The options will be rebound."
        , mixinStandardHelpOptions = true)
public class SelectCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @Mixin
    private Integrity integrity;
    @Mixin
    private Masked masked;
    @Mixin
    private Shape shape;

    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getPlayer();
        var options = new PlayerEdits.Options();
        options.integrity = integrity.integrity();
        options.masked = masked.masked();
        options.shape = shape.shape();
        try {
            PlayerEdits.select(player, options);
            out.println("Creates a new selection.");
        }catch (PlayerEdits.MissingPosException ex) {
            err.println(Messages.MISSING_POS_ERROR);
        }catch (PosArrayLengthException ex) {
            err.println(Messages.ofPosArrayLengthError(ex.getAcceptableLength()));
        }
    }

}
