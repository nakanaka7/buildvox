package tokyo.nakanaka.buildvox.core.command.bvCommand.affineTransformCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.BlockSettingOptions;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.command.mixin.Shape;
import tokyo.nakanaka.buildvox.core.command.util.NumberCompletionCandidates;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;

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
    @Mixin
    private Shape shape;
    @Mixin
    private BlockSettingOptions blockSettingOptions;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getPlayer();
        try {
            EditExit editExit = PlayerEdits.translate(player, dx, dy, dz, shape.shape(), blockSettingOptions);
            out.println(Messages.ofSetExit(editExit));
        }catch (PlayerEdits.MissingPosException ex) {
            err.println(Messages.MISSING_POS_ERROR);
        }catch (PosArrayLengthException ex) {
            err.println(Messages.ofPosArrayLengthError(ex.getAcceptableLength()));
        }
    }

}
