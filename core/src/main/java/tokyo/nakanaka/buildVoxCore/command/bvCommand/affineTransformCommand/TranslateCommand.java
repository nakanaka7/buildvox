package tokyo.nakanaka.buildVoxCore.command.bvCommand.affineTransformCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildVoxCore.FeedbackMessage;
import tokyo.nakanaka.buildVoxCore.command.EditExit;
import tokyo.nakanaka.buildVoxCore.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildVoxCore.command.completionCandidates.IntegerCandidates;
import tokyo.nakanaka.buildVoxCore.edit.PlayerEdits;
import tokyo.nakanaka.buildVoxCore.player.Player;

import java.io.PrintWriter;

@CommandLine.Command(name = "translate",
        mixinStandardHelpOptions = true,
        description = "Translate the selected blocks.")
public class TranslateCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @CommandLine.Parameters(description = "The displacement along x-axis.", completionCandidates = IntegerCandidates.class)
    private double dx;
    @CommandLine.Parameters(description = "The displacement along y-axis.", completionCandidates = IntegerCandidates.class)
    private double dy;
    @CommandLine.Parameters(description = "The displacement along z-axis.", completionCandidates = IntegerCandidates.class)
    private double dz;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getPlayer();
        EditExit editExit;
        try {
            editExit = PlayerEdits.translate(player, dx, dy, dz);
        }catch (PlayerEdits.SelectionNotFoundException ex) {
            err.println(FeedbackMessage.SELECTION_NULL_ERROR);
            return;
        }
        out.println(FeedbackMessage.ofSetExit(editExit));
    }

}
