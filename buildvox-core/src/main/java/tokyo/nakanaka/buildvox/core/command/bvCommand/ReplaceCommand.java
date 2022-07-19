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
    private Shape shape;
    @Mixin
    private Integrity integrity;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getPlayer();
        try {
            EditExit exit = PlayerEdits.replace(player, blockFrom, blockTo, shape.shape(), integrity.integrity());
            out.println(Messages.ofSetExit(exit));
        }catch (PlayerEdits.MissingPosException ex) {
            err.println(Messages.MISSING_POS_ERROR);
        }catch (PosArrayLengthException ex) {
            err.println(Messages.ofPosArrayLengthError(ex.getAcceptableLength()));
        }
    }

}
