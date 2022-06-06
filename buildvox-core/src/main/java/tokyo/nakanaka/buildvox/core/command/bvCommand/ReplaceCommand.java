package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.FeedbackMessage;
import tokyo.nakanaka.buildvox.core.command.EditExit;
import tokyo.nakanaka.buildvox.core.command.completionCandidates.BlockCandidates;
import tokyo.nakanaka.buildvox.core.command.mixin.IntegrityMixin;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;

import java.io.PrintWriter;

@CommandLine.Command(name = "replace", mixinStandardHelpOptions = true,
        description = "Replace specified blocks to another ones."
)
public class ReplaceCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @CommandLine.Parameters(arity = "1",
            description = "The block type to replace from.", completionCandidates = BlockCandidates.class)
    private String blockFrom;
    @CommandLine.Parameters(arity = "1",
            description = "The block type to replace to.", completionCandidates = BlockCandidates.class)
    private String blockTo;
    @CommandLine.Mixin
    private IntegrityMixin integrityMixin;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        VoxelBlock bFrom;
        try {
            bFrom = VoxelBlock.valueOf(blockFrom);
        }catch (IllegalArgumentException e) {
            err.println(FeedbackMessage.ofBlockParseError(blockFrom));
            return;
        }
        VoxelBlock bTo;
        try {
            bTo = VoxelBlock.valueOf(blockTo);
        }catch (IllegalArgumentException e) {
            err.println(FeedbackMessage.ofBlockParseError(blockTo));
            return;
        }
        try{
            integrityMixin.checkValue();
        }catch (IllegalStateException ex) {
            err.println(ex.getMessage());
            return;
        }
        EditExit exit;
        try {
            exit = PlayerEdits.replace(player, bFrom, bTo, integrityMixin.integrity());
        }catch (PlayerEdits.SelectionNotFoundException ex) {
            err.println(FeedbackMessage.SELECTION_NULL_ERROR);
            return;
        }
        out.println(FeedbackMessage.ofSetExit(exit));
    }

}
