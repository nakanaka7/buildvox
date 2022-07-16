package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.EditExit;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;
import tokyo.nakanaka.buildvox.core.command.mixin.*;
import tokyo.nakanaka.buildvox.core.edit.PlayerEdits;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;

import java.io.PrintWriter;

@Command(name = "fill", mixinStandardHelpOptions = true,
        description = "Fill blocks into the selection. Use -s option to specify a shape."
)
public class FillCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private BvCommand bvCmd;
    @Parameters(description = "The block.", completionCandidates = Block.Candidates.class, converter = Block.Converter.class)
    private VoxelBlock block;
    @Mixin
    private Shape shape;
    @Mixin
    private BlockSettingOptions blockSettingOptions;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        Player player = bvCmd.getPlayer();
        var options = new PlayerEdits.Options();
        var bsArgs = blockSettingOptions.getArguments();
        options.setBlockSettingArguments(bsArgs);
        options.shape = shape.shape();
        try {
            EditExit exit;
            VoxelBlock[] filters = bsArgs.getFilters();
            if(filters == null) {
                exit = PlayerEdits.fill(player, block, options);
            }else {
                exit = PlayerEdits.replace(player, filters[0], block, options);
            }
            out.println(Messages.ofSetExit(exit));
        }catch (PlayerEdits.MissingPosException ex) {
            err.println(Messages.MISSING_POS_ERROR);
        }catch (PosArrayLengthException ex) {
            err.println(Messages.ofPosArrayLengthError(ex.getAcceptableLength()));
        }
    }

}
