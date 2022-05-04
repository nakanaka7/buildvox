package tokyo.nakanaka.buildVoxCore.command.bvCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildVoxCore.FeedbackMessage;
import tokyo.nakanaka.buildVoxCore.command.EditExit;
import tokyo.nakanaka.buildVoxCore.command.completionCandidates.BlockCandidates;
import tokyo.nakanaka.buildVoxCore.command.mixin.IntegrityMixin;
import tokyo.nakanaka.buildVoxCore.edit.PlayerEdits;
import tokyo.nakanaka.buildVoxCore.player.Player;
import tokyo.nakanaka.buildVoxCore.world.Block;

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
        Player player = bvCmd.getPlayer();
        Block bFrom;
        try {
            bFrom = Block.valueOf(blockFrom);
        }catch (IllegalArgumentException e) {
            err.println(FeedbackMessage.ofBlockParseError(blockFrom));
            return;
        }
        Block bTo;
        try {
            bTo = Block.valueOf(blockTo);
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
