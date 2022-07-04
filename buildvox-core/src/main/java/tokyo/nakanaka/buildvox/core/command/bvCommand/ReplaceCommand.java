package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.command.mixin.Block;
import tokyo.nakanaka.buildvox.core.command.mixin.Integrity;
import tokyo.nakanaka.buildvox.core.command.mixin.Shape;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;

import java.io.PrintWriter;

@Command(name = "replace", mixinStandardHelpOptions = true,
        description = "Replace blocks to another ones. See also /fill with -r."
)
public class ReplaceCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private BvCommand bvCmd;
    @Parameters(arity = "1",
            description = "The block type to replace from.", completionCandidates = Block.Candidates.class,
            converter = Block.Converter.class)
    private VoxelBlock blockFrom;
    @Parameters(arity = "1",
            description = "The block type to replace to.", completionCandidates = Block.Candidates.class,
            converter = Block.Converter.class)
    private VoxelBlock blockTo;
    @Mixin
    private Integrity integrity;
    @Mixin
    private Shape shape;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getPlayer();
        var options = new PlayerEdits.Options();
        options.integrity = integrity.integrity();
        options.shape = shape.shape();
        try {
            EditExit exit = PlayerEdits.replace(player, blockFrom, blockTo, options);
            out.println(Messages.ofSetExit(exit));
        }catch (PlayerEdits.MissingPosException | PosArrayLengthException ex) {
            err.println(Messages.SELECTION_NULL_ERROR);
        }
    }

}
