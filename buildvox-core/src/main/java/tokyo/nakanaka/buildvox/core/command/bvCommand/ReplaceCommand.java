package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.command.BlockParameter;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.command.Integrity;
import tokyo.nakanaka.buildvox.core.command.SelectionShapeParameter;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selectionShape.MissingPosException;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShape;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

import java.io.PrintWriter;

@Command(name = "replace", mixinStandardHelpOptions = true,
        description = "Replace specified blocks to another ones."
)
public class ReplaceCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private BvCommand bvCmd;
    @Parameters(arity = "1",
            description = "The block type to replace from.", completionCandidates = BlockParameter.Candidates.class)
    private String blockFrom;
    @Parameters(arity = "1",
            description = "The block type to replace to.", completionCandidates = BlockParameter.Candidates.class)
    private String blockTo;
    @Mixin
    private Integrity integrity;
    @Option(names = {"-s", "--shape"}, completionCandidates = SelectionShapeParameter.Candidates.class,
            converter = SelectionShapeParameter.Converter.class)
    private SelectionShape shape;

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
            integrity.checkValue();
        }catch (IllegalStateException ex) {
            err.println(ex.getMessage());
            return;
        }
        var options = new PlayerEdits.Options();
        options.integrity = integrity.integrity();
        options.shape = shape;
        EditExit exit;
        try {
            exit = PlayerEdits.replace(player, bFrom, bTo, options);
        }catch (MissingPosException | PosArrayLengthException ex) {
            err.println(Messages.SELECTION_NULL_ERROR);
            return;
        }
        out.println(Messages.ofSetExit(exit));
    }

}
