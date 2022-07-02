package tokyo.nakanaka.buildvox.core.command.bvCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.command.Block;
import tokyo.nakanaka.buildvox.core.command.Integrity;
import tokyo.nakanaka.buildvox.core.command.Shape;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selectionShape.MissingPosException;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;

import java.io.PrintWriter;

@Command(name = "fill", mixinStandardHelpOptions = true,
        description = "Fill blocks into the selection or specified shape region."
)
public class FillCommand implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(FillCommand.class);
    private static final String DESC_HEAD = "Fill blocks in ";

    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private BvCommand bvCmd;
    @Mixin
    private Integrity integrity;
    @Option(names = {"-m", "--masked"})
    private boolean masked;
    @Mixin
    private Shape shape;
    @Parameters(description = "The block.", completionCandidates = Block.Candidates.class, converter = Block.Converter.class)
    private VoxelBlock block;
    @Option(names = {"-r", "--replace"}, description = "The block to replace", completionCandidates = Block.Candidates.class, converter = Block.Converter.class)
    private VoxelBlock filter;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        double integrity = this.integrity.integrity();
        var options = new PlayerEdits.Options();
        options.integrity = integrity;
        options.masked = masked;
        options.shape = shape.shape();
        EditExit exit;
        try {
            if(filter == null) {
                exit = PlayerEdits.fill(player, block, options);
            }else {
                exit = PlayerEdits.replace(player, filter, block, options);
            }
            out.println(Messages.ofSetExit(exit));
        }catch (MissingPosException | PosArrayLengthException ex) {
            err.println(Messages.SELECTION_NULL_ERROR);
        }

    }

}
