package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.command.BlockParameter;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.command.IntegrityMixin;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

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
            description = "The block type to replace from.", completionCandidates = BlockParameter.Candidates.class)
    private String blockFrom;
    @CommandLine.Parameters(arity = "1",
            description = "The block type to replace to.", completionCandidates = BlockParameter.Candidates.class)
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
            bFrom = BuildVoxSystem.parseBlock(blockFrom);
        }catch (IllegalArgumentException e) {
            err.println(Messages.ofBlockParseError(blockFrom));
            return;
        }
        VoxelBlock bTo;
        try {
            bTo = BuildVoxSystem.parseBlock(blockTo);
        }catch (IllegalArgumentException e) {
            err.println(Messages.ofBlockParseError(blockTo));
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
            err.println(Messages.SELECTION_NULL_ERROR);
            return;
        }
        out.println(Messages.ofSetExit(exit));
    }

}
