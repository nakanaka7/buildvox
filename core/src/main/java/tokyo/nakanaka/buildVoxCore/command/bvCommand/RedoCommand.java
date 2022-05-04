package tokyo.nakanaka.buildVoxCore.command.bvCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildVoxCore.FeedbackMessage;
import tokyo.nakanaka.buildVoxCore.edit.PlayerEdits;
import tokyo.nakanaka.buildVoxCore.player.Player;

import java.io.PrintWriter;

@CommandLine.Command(name = "redo", mixinStandardHelpOptions = true,
        description = "Redo edits.")
public class RedoCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @CommandLine.Parameters(description = "The redo count(default: ${DEFAULT-VALUE}).", defaultValue = "1")
    private int count;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getPlayer();
        if(count <= 0){
            err.println(FeedbackMessage.UNDO_REDO_COUNT_ERROR);
            return;
        }
        int redoEditCount = PlayerEdits.redo(player, count);
        out.println(FeedbackMessage.ofRedoExit(redoEditCount));
    }

}
