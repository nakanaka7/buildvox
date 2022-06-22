package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;

@CommandLine.Command(name = "undo", mixinStandardHelpOptions = true,
        description = "Undo edits.")
public class UndoCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @CommandLine.Parameters(description = "The undo count(default: ${DEFAULT-VALUE}).", defaultValue = "1")
    private int count;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        if(count <= 0){
            err.println(Messages.UNDO_REDO_COUNT_ERROR);
            return;
        }
        int undoEditCount = PlayerEdits.undo(player, count);
        out.println(Messages.ofUndoExit(undoEditCount));
    }

}
