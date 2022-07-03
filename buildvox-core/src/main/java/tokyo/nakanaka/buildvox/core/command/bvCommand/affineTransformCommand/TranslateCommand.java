package tokyo.nakanaka.buildvox.core.command.bvCommand.affineTransformCommand;

import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.command.NumberCompletionCandidates;
import tokyo.nakanaka.buildvox.core.command.Shape;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShape;
import picocli.CommandLine.*;

import java.io.PrintWriter;

@Command(name = "translate",
        mixinStandardHelpOptions = true,
        description = "Translate the selected blocks.")
public class TranslateCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private BvCommand bvCmd;
    @Parameters(description = "The displacement along x-axis.", completionCandidates = NumberCompletionCandidates.Integer.class)
    private double dx;
    @Parameters(description = "The displacement along y-axis.", completionCandidates = NumberCompletionCandidates.Integer.class)
    private double dy;
    @Parameters(description = "The displacement along z-axis.", completionCandidates = NumberCompletionCandidates.Integer.class)
    private double dz;
    @Option(names = {"-s", "--shape"}, completionCandidates = Shape.Candidates.class,
            converter = Shape.Converter.class)
    private SelectionShape shape;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        var options = new PlayerEdits.Options();
        options.shape = shape;
        EditExit editExit;
        try {
            editExit = PlayerEdits.translate(player, dx, dy, dz, options);
        }catch (PlayerEdits.MissingPosException | PosArrayLengthException ex) {
            err.println(Messages.SELECTION_NULL_ERROR);
            return;
        }
        out.println(Messages.ofSetExit(editExit));
    }

}
