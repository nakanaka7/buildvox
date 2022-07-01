package tokyo.nakanaka.buildvox.core.command.bvCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.command.BlockParameter;
import tokyo.nakanaka.buildvox.core.command.SelectionShapeParameter;
import tokyo.nakanaka.buildvox.core.command.IntegrityMixin;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShape;

import java.io.PrintWriter;

@CommandLine.Command(name = "fill", mixinStandardHelpOptions = true,
        description = "Fill blocks into the selection or specified shape region."
)
public class FillCommand implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(FillCommand.class);
    private static final String DESC_HEAD = "Fill blocks in ";

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;
    @CommandLine.Mixin
    private IntegrityMixin integrityMixin;
    @CommandLine.Option(names = {"-m", "--masked"})
    private boolean masked;
    @CommandLine.Option(names = {"-s", "--shape"}, completionCandidates = SelectionShapeParameter.Candidates.class,
            converter = SelectionShapeParameter.Converter.class)
    private SelectionShape shape;
    @CommandLine.Parameters(description = "The block.", completionCandidates = BlockParameter.Candidates.class, converter = BlockParameter.Converter.class)
    private VoxelBlock block;
    @CommandLine.Option(names = {"-r", "--replace"}, description = "The block to replace", completionCandidates = BlockParameter.Candidates.class, converter = BlockParameter.Converter.class)
    private VoxelBlock filter;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getTargetPlayer();
        try{
            integrityMixin.checkValue();
        }catch (IllegalStateException ex) {
            err.println(ex.getMessage());
            return;
        }
        double integrity = integrityMixin.integrity();
        EditExit exit;
        if(filter == null) {
            var options = new PlayerEdits.Options();
            options.integrity = integrity;
            options.masked = masked;
            options.shape = shape;
            try {
                exit = PlayerEdits.fill(player, block, options);
            }catch (Exception ex) {
                err.println(Messages.SELECTION_NULL_ERROR);
                return;
            }
        }else {
            PlayerEdits.ReplaceOptions options = new PlayerEdits.ReplaceOptions(integrity, masked, shape);
            try {
                exit = PlayerEdits.replace(player, filter, block, options);
            }catch (Exception ex) {
                err.println(Messages.SELECTION_NULL_ERROR);
                return;
            }
        }
        out.println(Messages.ofSetExit(exit));
    }

}
